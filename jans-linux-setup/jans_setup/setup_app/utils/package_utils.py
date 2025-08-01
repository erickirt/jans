import os
import shutil
import sys
import importlib
import tempfile

from setup_app import paths
from setup_app.config import Config
from setup_app.utils import base
from setup_app.utils.setup_utils import SetupUtils
from setup_app.static import InstallTypes

class PackageUtils(SetupUtils):

    #TODO: get commands from paths
    def get_install_commands(self):
        if base.clone_type == 'deb':
            install_command = 'DEBIAN_FRONTEND=noninteractive apt-get install -y {0}'
            update_command = 'DEBIAN_FRONTEND=noninteractive apt-get update -y'
            query_command = 'dpkg-query -W -f=\'${{Status}}\' {} 2>/dev/null | grep -c "ok installed"'
            check_text = '0'

        elif base.clone_type == 'rpm':
            if base.os_type == 'suse':
                install_command = 'zypper --no-gpg-checks install -y {0}'
                update_command = ''
            else:
                install_command = 'yum install -y {0}'
                update_command = 'yum install -y epel-release'
            query_command = 'rpm -q {0}'
            check_text = 'is not installed'

        return install_command, update_command, query_command, check_text


    def check_installed(self, package):
        install_command, update_command, query_command, check_text = self.get_install_commands()
        sout, serr = self.run(query_command.format(package), shell=True, get_stderr=True)
        return not check_text in sout+serr


    def installNetPackage(self, packages):
        if base.clone_type == 'rpm' and base.os_type != 'suse':
            self.run(['yum', '-y', 'module', 'enable', 'mod_auth_openidc'])
        install_command, update_command, query_command, check_text = self.get_install_commands()
        self.run(install_command.format(packages), shell=True)

    def check_and_install_packages(self):
        install_command, update_command, query_command, check_text = self.get_install_commands()
        dnf_command = shutil.which('dnf')
        if dnf_command:
            for action_, repo_ in (('disable', 'postgresql'), ('reset', 'postgresql'), ('enable', 'postgresql:12')):
                self.run([dnf_command, '-y', 'module', action_, repo_])

        install_list = {'mandatory': [], 'optional': []}

        package_list = base.get_os_package_list()

        os_type_version = base.os_type + ' ' + base.os_version

        if not Config.installed_instance:
            if base.argsp.local_rdbm == 'mysql' or (Config.get('rdbm_install_type') == InstallTypes.LOCAL and Config.rdbm_type == 'mysql'):
                if base.os_type == 'suse':
                    self.run(['rpm', '-i', f'https://dev.mysql.com/get/mysql80-community-release-sl{base.os_version}-{base.os_subversion}.noarch.rpm'])
                    self.run(['rpm', '--import', '/etc/RPM-GPG-KEY-mysql'])
                    self.run(['zypper', '--no-gpg-checks', '--gpg-auto-import-keys', 'refresh'])
                    package_list[os_type_version]['mandatory'] += ' mysql-community-server-8.0.39'
                elif base.os_type == 'debian' and base.os_version in ('12', '13'):
                    with tempfile.TemporaryDirectory() as tmpdirname:
                        libaio1_url = 'http://ftp.de.debian.org/debian/pool/main/liba/libaio/libaio1_0.3.113-4_amd64.deb'
                        base.download(libaio1_url, os.path.join(tmpdirname, os.path.basename(libaio1_url)))
                        for deb_fn in ( 'mysql-community-server_9.2.0-1debian12_amd64.deb',
                                        'mysql-common_9.2.0-1debian12_amd64.deb',
                                        'mysql-community-server-core_9.2.0-1debian12_amd64.deb',
                                        'mysql-client_9.2.0-1debian12_amd64.deb',
                                        'mysql-community-client_9.2.0-1debian12_amd64.deb',
                                        'mysql-community-client-core_9.2.0-1debian12_amd64.deb',
                                        'mysql-community-client-plugins_9.2.0-1debian12_amd64.deb',
                                        ):
                            base.download(f'https://downloads.mysql.com/archives/get/p/23/file/{deb_fn}', os.path.join(tmpdirname, deb_fn))
                        self.run([install_command.format(os.path.join(tmpdirname, '*.deb'))],  shell=True)

                else:
                    package_list[os_type_version]['mandatory'] += ' mysql-server'

        for pypackage in package_list[os_type_version]['python']:
            try:
                importlib.import_module(pypackage)
            except:
                package_list[os_type_version]['mandatory'] += ' ' + package_list[os_type_version]['python'][pypackage]

        for install_type in install_list:
            for package in package_list[os_type_version][install_type].split():
                if os_type_version in ('centos 7', 'red 7') and package.startswith('python3-'):
                    package_query = package.replace('python3-', 'python36-')
                else:
                    package_query = package
    
                if self.check_installed(package_query):
                    self.logIt('Package {0} was installed'.format(package_query))
                else:
                    self.logIt('Package {0} was not installed'.format(package_query))
                    install_list[install_type].append(package_query)

        install = {'mandatory': True, 'optional': False}

        for install_type in install_list:
            if install_list[install_type]:
                packages = " ".join(install_list[install_type])

                if install_type == 'mandatory':
                    print("The following packages are required for Janssen Server")
                    print(packages)
                    if not base.argsp.n:
                        r = input("Do you want to install these now? [Y/n] ")
                        if r and r.lower()=='n':
                            install[install_type] = False
                            if install_type == 'mandatory':
                                print("Can not proceed without installing required packages. Exiting ...")
                                sys.exit()

                elif install_type == 'optional':
                    print("You may need the following packages")
                    print(packages)
                    r = input("Do you want to install these now? [y/N] ")
                    if r and r.lower()=='y':
                        install[install_type] = True

                if install[install_type]:
                    self.logIt("Installing packages " + packages)
                    print("Installing packages", packages)
                    if not base.os_type in ('fedora', 'suse'):
                        sout, serr = self.run(update_command, shell=True, get_stderr=True)
                    self.installNetPackage(packages)

        if base.clone_type == 'deb':
            self.run('a2enmod ssl headers proxy proxy_http proxy_ajp', shell=True)
            default_site = '/etc/apache2/sites-enabled/000-default.conf'
            if os.path.exists(default_site):
                os.remove(default_site)


    def installPackage(self, packageName, remote=False):
        base.logIt("Installing " + packageName)
        install_command, update_command, query_command, check_text = self.get_install_commands()
        if remote:
            output = self.run(install_command.format(packageName), shell=True)
        else:
            if base.clone_type == 'deb':
                output = self.run([paths.cmd_dpkg, '--install', packageName])
            else:
                output = self.run([paths.cmd_rpm, '--install', '--verbose', '--hash', packageName])

        return output

packageUtils = PackageUtils()

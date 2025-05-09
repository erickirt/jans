import os
import glob
import shutil
import uuid

from pathlib import Path

from setup_app import paths
from setup_app.utils import base
from setup_app.static import AppType, InstallOption
from setup_app.config import Config
from setup_app.installers.jetty import JettyInstaller

Config.jans_fido2_port = '8073'

class FidoInstaller(JettyInstaller):

    source_files = [
                (os.path.join(Config.dist_jans_dir, 'jans-fido2.war'), os.path.join(base.current_app.app_info['JANS_MAVEN'], 'maven/io/jans/jans-fido2-server/{0}/jans-fido2-server-{0}.war').format(base.current_app.app_info['jans_version'])),
                (os.path.join(Config.dist_app_dir, os.path.basename(base.current_app.app_info['APPLE_WEBAUTHN'])), base.current_app.app_info['APPLE_WEBAUTHN']),
                (os.path.join(Config.dist_app_dir, 'fido2/mds/toc/toc.jwt'), 'https://mds.fidoalliance.org/'),
                (os.path.join(Config.dist_app_dir, 'fido2/mds/cert/root-r3.crt'), 'https://secure.globalsign.com/cacert/root-r3.crt'),
                (os.path.join(Config.dist_jans_dir, 'fido2-plugin.jar'), os.path.join(base.current_app.app_info['JANS_MAVEN'], 'maven/io/jans/jans-config-api/plugins/fido2-plugin/{0}/fido2-plugin-{0}-distribution.jar').format(base.current_app.app_info['jans_version'])),
                ]

    def __init__(self):
        setattr(base.current_app, self.__class__.__name__, self)
        self.service_name = 'jans-fido2'
        self.needdb = True
        self.app_type = AppType.SERVICE
        self.install_type = InstallOption.OPTONAL
        self.install_var = 'install_fido2'
        self.register_progess()

        self.fido2ConfigFolder = os.path.join(Config.configFolder, 'fido2')
        self.output_folder = os.path.join(Config.output_dir, 'jans-fido2')
        self.template_folder = os.path.join(Config.templateFolder, 'jans-fido2')
        self.fido2_dynamic_conf_json = os.path.join(self.output_folder, 'dynamic-conf.json')
        self.fido2_error_json = os.path.join(self.output_folder, 'jans-fido2-errors.json')
        self.fido2_static_conf_json = os.path.join(self.output_folder, 'static-conf.json')
        self.ldif_fido2 = os.path.join(self.output_folder, 'fido2.ldif')
        self.ldif_fido2_documents = os.path.join(self.output_folder, 'docuemts.ldif')

        self.fido_document_certs_dir = os.path.join(self.fido2ConfigFolder, 'mds/cert')
        self.fido_document_tocs_dir = os.path.join(self.fido2ConfigFolder, 'mds/toc')
        self.fido_document_authenticator_cert_dir = os.path.join(self.fido2ConfigFolder, 'authenticator_cert')

    def install(self):
        self.install_jettyService(self.jetty_app_configuration[self.service_name], True)
        self.enable()

    def generate_configuration(self):
        Config.fido_document_certs_inum = str(uuid.uuid4())
        Config.fido_document_tocs_inum = str(uuid.uuid4())
        Config.Apple_WebAuthn_Root_CA_inum = str(uuid.uuid4())
        self.fido_document_creation_date = self.get_ldap_time()

    def render_import_templates(self):

        for tmp_ in (
                self.fido2_dynamic_conf_json,
                self.fido2_error_json,
                self.fido2_static_conf_json,
                ):
            self.renderTemplateInOut(tmp_, self.template_folder, self.output_folder)

        Config.templateRenderingDict['fido2_dynamic_conf_base64'] = self.generate_base64_file(self.fido2_dynamic_conf_json, 1)
        Config.templateRenderingDict['fido2_error_base64'] = self.generate_base64_file(self.fido2_error_json, 1)
        Config.templateRenderingDict['fido2_static_conf_base64'] = self.generate_base64_file(self.fido2_static_conf_json, 1)

        Config.templateRenderingDict['fido_document_tocs_base64'] = self.generate_base64_file(self.source_files[2][0], 1)
        Config.templateRenderingDict['fido_document_certs_base64'] = self.generate_base64_file(self.source_files[3][0], 1)
        Config.templateRenderingDict['Apple_WebAuthn_Root_CA_base64'] = self.generate_base64_file(self.source_files[1][0], 1)

        for f in ('yubico-u2f-ca-cert.crt', 'HyperFIDO_CA_Cert_V1.pem', 'HyperFIDO_CA_Cert_V2.pem'):
            src = os.path.join(Config.install_dir, 'static/fido2/authenticator_cert/', f)
            doc_var, _ = os.path.splitext(f)
            doc_var = doc_var.replace('-','_')
            setattr(Config, doc_var + '_inum', str(uuid.uuid4()))
            Config.templateRenderingDict[doc_var + '_base64'] = self.generate_base64_file(src, 1)


        for tmp_ in (self.ldif_fido2, self.ldif_fido2_documents):
            self.renderTemplateInOut(tmp_, self.template_folder, self.output_folder)

        ldif_files = [self.ldif_fido2, self.ldif_fido2_documents]
        self.dbUtils.import_ldif(ldif_files)


    def create_folders(self):
        for d in ('mds/cert', 'mds/toc', 'server_metadata'):
            dpath = os.path.join(self.fido2ConfigFolder, d)
            self.run([paths.cmd_mkdir, '-p', dpath])

    def copy_static(self):
        #copy fido2 server metadata
        src_dir = os.path.join(Config.install_dir, 'static/fido2/server_metadata')
        trgt_dir = os.path.join(self.fido2ConfigFolder, 'server_metadata')
        self.copy_tree(src_dir, trgt_dir, ignore='.dontdelete')


    def service_post_install_tasks(self):
        base.current_app.ConfigApiInstaller.install_plugin('fido2')

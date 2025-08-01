# FROM blazemeter/taurus:master-f96971fa-2022-12-12
FROM blazemeter/taurus:1.16.45@sha256:8b867cb7ea6a0595020553eac68fd3fe97ac702bdd1daaf3c4d3a84120159979

# ===============
# Ubuntu packages
# ===============

RUN apt-get clean \
&& mv /var/lib/apt/lists /tmp \
&& mkdir -p /var/lib/apt/lists/partial \
&& apt-get clean

RUN apt-get update \
    &&  apt-get install openjdk-8-jre-headless wget git bash python3 python3-dev lsb-core python3-setuptools cmake build-essential apt-utils python3-pip -y

# ===============
# Environmental Variables
# ===============

ENV FQDN="https://demoexample.jans.io" \
    AUTHZ_CLIENT_ID="" \
    AUTHZ_CLIENT_SECRET="" \
    ROPC_CLIENT_ID="" \
    ROPC_CLIENT_SECRET="" \
    DCR_CLIENT_ID="" \
    DCR_CLIENT_SECRET="" \
    RUN_AUTHZ_TEST=false \
    RUN_ROPC_TEST=false \
    RUN_DCR_TEST=false \
    TEST_USERS_PREFIX_STRING="test_user" \
    THREAD_COUNT=200 \
    USER_NUMBER_STARTING_POINT=0 \
    USER_NUMBER_ENDING_POINT=50000000 \
    LOAD_USERS_TO_RDBMS=false \
    USER_SPLIT_PARALLEL_THREADS=20 \
    # pgsql or mysql
    RDBMS_TYPE="mysql" \
    RDBMS_DB="jans" \
    RDBMS_USER="jans" \
    RDBMS_PASSWORD="" \
    RDBMS_HOST="localhost"



# ======
# Python
# ======

COPY requirements.txt /scripts/requirements.txt
RUN pip install --no-cache-dir -U pip wheel \
    && pip3 install --no-cache-dir --default-timeout=300 -r /scripts/requirements.txt \
    && pip3 uninstall -y pip wheel

COPY scripts /scripts

RUN mkdir -p /root/.bzt/jmeter-taurus \
    && wget -q https://dlcdn.apache.org/jmeter/binaries/apache-jmeter-5.6.3.tgz -O /scripts/apache-jmeter-5.6.tgz \
    && tar zxvf /scripts/apache-jmeter-5.6.tgz -C /root/.bzt/jmeter-taurus \
    && mv /root/.bzt/jmeter-taurus/apache-jmeter-5.6.3 /root/.bzt/jmeter-taurus/5.6.3

LABEL org.opencontainers.image.url="ghcr.io/janssenproject/jans/loadtesting-jmeter" \
    org.opencontainers.image.authors="Janssen Project <support@jans.io>" \
    org.opencontainers.image.vendor="Janssen Project" \
    org.opencontainers.image.version="0.0.0-nightly" \
    org.opencontainers.image.title="Janssen Jmeter tests" \
    org.opencontainers.image.description="Janssen Jmeter and user loader image"

ENTRYPOINT ["bash", "/scripts/entrypoint.sh"]

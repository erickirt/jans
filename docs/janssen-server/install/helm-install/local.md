---
tags:
  - administration
  - installation
  - helm
---

# Install Janssen Server Locally with minikube and MicroK8s

## System Requirements

For local deployments like `minikube` and `MicroK8s` or cloud installations in demo mode, resources may be set to the minimum as below:

- 8 GB RAM
- 4 CPU cores
- 50 GB hard-disk

Use the listing below for a detailed estimation of minimum required resources. The table contains the default resources recommendation per service. Depending on the use of each service the resources need may increase or decrease.

| Service           | CPU Unit | RAM   | Disk Space | Processor Type | Required                           |
|-------------------|----------|-------|------------|----------------|------------------------------------|
| Auth server       | 2.5      | 2.5GB | N/A        | 64 Bit         | Yes                                |
| fido2             | 0.5      | 0.5GB | N/A        | 64 Bit         | No                                 |
| scim              | 1        | 1GB   | N/A        | 64 Bit         | No                                 |
| config - job      | 0.3      | 0.3GB | N/A        | 64 Bit         | Yes on fresh installs              |
| persistence - job | 0.3      | 0.3GB | N/A        | 64 Bit         | Yes on fresh installs              |
| nginx             | 1        | 1GB   | N/A        | 64 Bit         | Yes ALB/Istio not used             |
| auth-key-rotation | 0.3      | 0.3GB | N/A        | 64 Bit         | No [Strongly recommended]          |
| config-api        | 1        | 1GB   | N/A        | 64 Bit         | No                                 |
| casa              | 0.5      | 0.5GB | N/A        | 64 Bit         | No                                 |
| link              | 0.5      | 1GB   | N/A        | 64 Bit         | No                                 |
| saml              | 0.5      | 1GB   | N/A        | 64 Bit         | No                                 |

Releases of images are in style 1.0.0-beta.0, 1.0.0-0

## Installation Steps

Start a fresh Ubuntu `18.04`/`20.04`/`22.04` VM with ports `443` and `80` open. Then execute the following:

```bash
sudo su -
```
```bash
wget https://raw.githubusercontent.com/JanssenProject/jans/vreplace-janssen-version/automation/startjanssendemo.sh && chmod u+x startjanssendemo.sh && ./startjanssendemo.sh
```

This will install Docker, Microk8s, Helm and Janssen with the default settings that can be found inside [values.yaml](https://github.com/JanssenProject/jans/blob/main/charts/janssen/values.yaml).  

The installer will automatically add a record to your hosts record in the VM but if you want to access the endpoints outside the VM you must map the `ip` of the instance running Ubuntu to the FQDN you provided and then access the endpoints at your browser such in the example in the table below.

| Service     | Example endpoint                                              |
| ----------- |---------------------------------------------------------------|
| Auth server | `https://FQDN/.well-known/openid-configuration`                 |
| fido2       | `https://FQDN/.well-known/fido2-configuration` |
| scim        | `https://FQDN/.well-known/scim-configuration`  |

## Configure Janssen
  You can use the [TUI](../../kubernetes-ops/tui-k8s.md) to configure Janssen components. The TUI calls the Config API to perform ad hoc configuration.

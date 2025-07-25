# casa

![Version: 1.9.0](https://img.shields.io/badge/Version-1.9.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 1.9.0](https://img.shields.io/badge/AppVersion-1.9.0-informational?style=flat-square)

Jans Casa ("Casa") is a self-service web portal for end-users to manage authentication and authorization preferences for their account in a Jans Server.

**Homepage:** <https://gluu.org/docs/casa/>

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| Mohammad Abudayyeh | <support@jans.io> | <https://github.com/moabu> |

## Source Code

* <https://gluu.org/casa/>
* <https://github.com/JanssenProject/jans/docker-jans-casa>

## Requirements

Kubernetes: `>=v1.21.0-0`

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| additionalAnnotations | object | `{}` | Additional annotations that will be added across all resources  in the format of {cert-manager.io/issuer: "letsencrypt-prod"}. key app is taken |
| additionalLabels | object | `{}` | Additional labels that will be added across all resources definitions in the format of {mylabel: "myapp"} |
| customCommand | list | `[]` | Add custom pod's command. If passed, it will override the default conditional command. |
| customScripts | list | `[]` | Add custom scripts that have been mounted to run before the entrypoint. |
| dnsConfig | object | `{}` | Add custom dns config |
| dnsPolicy | string | `""` | Add custom dns policy |
| fullnameOverride | string | `""` |  |
| hpa | object | `{"behavior":{},"enabled":true,"maxReplicas":10,"metrics":[],"minReplicas":1,"targetCPUUtilizationPercentage":50}` | Configure the HorizontalPodAutoscaler |
| hpa.behavior | object | `{}` | Scaling Policies |
| hpa.metrics | list | `[]` | metrics if targetCPUUtilizationPercentage is not set |
| image.pullPolicy | string | `"IfNotPresent"` | Image pullPolicy to use for deploying. |
| image.pullSecrets | list | `[]` | Image Pull Secrets |
| image.repository | string | `"janssenproject/casa"` | Image  to use for deploying. |
| image.tag | string | `"1.9.0-1"` | Image  tag to use for deploying. |
| lifecycle | object | `{}` |  |
| livenessProbe | object | `{"httpGet":{"path":"/jans-casa/health-check","port":"http-casa"},"initialDelaySeconds":25,"periodSeconds":25,"timeoutSeconds":5}` | Configure the liveness healthcheck for casa if needed. |
| livenessProbe.httpGet.path | string | `"/jans-casa/health-check"` | http liveness probe endpoint |
| nameOverride | string | `""` |  |
| nodeSelector | object | `{}` | Add nodeSelector (see https://kubernetes.io/docs/concepts/scheduling-eviction/assign-pod-node/#nodeselector) |
| podSecurityContext | object | `{}` |  |
| readinessProbe | object | `{"httpGet":{"path":"/jans-casa/health-check","port":"http-casa"},"initialDelaySeconds":30,"periodSeconds":30,"timeoutSeconds":5}` | Configure the readiness healthcheck for the casa if needed. |
| readinessProbe.httpGet.path | string | `"/jans-casa/health-check"` | http readiness probe endpoint |
| replicas | int | `1` | Service replica number. |
| resources | object | `{"limits":{"cpu":"500m","memory":"500Mi"},"requests":{"cpu":"500m","memory":"500Mi"}}` | Resource specs. |
| resources.limits.cpu | string | `"500m"` | CPU limit. |
| resources.limits.memory | string | `"500Mi"` | Memory limit. |
| resources.requests.cpu | string | `"500m"` | CPU request. |
| resources.requests.memory | string | `"500Mi"` | Memory request. |
| securityContext | object | `{}` |  |
| service.name | string | `"http-casa"` | The name of the casa port within the casa service. Please keep it as default. |
| service.port | int | `8080` | Port of the casa service. Please keep it as default. |
| service.sessionAffinity | string | `"None"` | Default set to None If you want to make sure that connections from a particular client are passed to the same Pod each time, you can select the session affinity based on the client's IP addresses by setting this to ClientIP |
| service.sessionAffinityConfig | object | `{"clientIP":{"timeoutSeconds":10800}}` | the maximum session sticky time if sessionAffinity is ClientIP |
| tolerations | list | `[]` | Add tolerations for the pods |
| usrEnvs | object | `{"normal":{},"secret":{}}` | Add custom normal and secret envs to the service |
| usrEnvs.normal | object | `{}` | Add custom normal envs to the service variable1: value1 |
| usrEnvs.secret | object | `{}` | Add custom secret envs to the service variable1: value1 |
| volumeMounts | list | `[]` | Configure any additional volumesMounts that need to be attached to the containers |
| volumes | list | `[]` | Configure any additional volumes that need to be attached to the pod |

/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.service.uma;

import org.gluu.site.ldap.persistence.exception.EntryPersistenceException;
import org.slf4j.Logger;
import org.xdi.oxauth.model.common.AuthorizationGrant;
import org.xdi.oxauth.model.common.AuthorizationGrantList;
import org.xdi.oxauth.model.common.uma.UmaRPT;
import org.xdi.oxauth.model.configuration.AppConfiguration;
import org.xdi.oxauth.model.error.ErrorResponseFactory;
import org.xdi.oxauth.model.uma.UmaErrorResponseType;
import org.xdi.oxauth.model.uma.UmaScopeType;
import org.xdi.oxauth.model.uma.persistence.UmaPermission;
import org.xdi.oxauth.model.uma.persistence.UmaResource;
import org.xdi.oxauth.service.token.TokenService;
import org.xdi.util.StringHelper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.xdi.oxauth.model.uma.UmaErrorResponseType.*;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 04/02/2013
 */
@Named
@Stateless
public class UmaValidationService {

    @Inject
    private Logger log;

    @Inject
    private ErrorResponseFactory errorResponseFactory;

    @Inject
    private TokenService tokenService;

    @Inject
    private AuthorizationGrantList authorizationGrantList;

    @Inject
   	private UmaResourceService resourceService;

    @Inject
    private UmaScopeService umaScopeService;

    @Inject
    private AppConfiguration appConfiguration;

    public String validateAmHost(String host) {
        if (StringHelper.isEmpty(host)) {
            log.error("AM host is invalid");
            throw new WebApplicationException(Response.status(BAD_REQUEST)
                    .entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_REQUEST)).build());
        }

        String hostUri;
        try {
        	int index = host.indexOf("://");
        	if (index != -1) {
        		hostUri = (new URI(host)).getHost();
        	} else {
        		hostUri = (new URI("https://" + host)).getHost();
        	}
        } catch (URISyntaxException ex) {
            log.error("Failed to parse AM host: '{}'", ex, host);
            throw new WebApplicationException(Response.status(BAD_REQUEST)
                    .entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_REQUEST)).build());
        }

        try {
            URI umaBaseEndpoint = new URI(appConfiguration.getBaseEndpoint());
            if (!StringHelper.equalsIgnoreCase(hostUri, umaBaseEndpoint.getHost())) {
                log.error("Get request for another AM: '{}'. Expected: '{}'", hostUri, umaBaseEndpoint.getHost());
                throw new WebApplicationException(Response.status(BAD_REQUEST)
                        .entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_REQUEST)).build());
            }
        } catch (URISyntaxException ex) {
            log.error("Failed to parse AM host: '{}'", ex, hostUri);
            throw new WebApplicationException(Response.status(BAD_REQUEST)
                    .entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_REQUEST)).build());
        }

        return StringHelper.toLowerCase(hostUri).trim();
    }

//    public String validateHost(String host) {
//   		if (StringHelper.isEmpty(host)) {
//   			log.error("Host is invalid");
//   			throw new WebApplicationException(Response.status(BAD_REQUEST)
//   					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_REQUEST)).build());
//   		}
//
//        String hostUri;
//   		try {
//        	int index = host.indexOf("://");
//        	if (index != -1) {
//        		hostUri = (new URI(host)).getHost();
//        	} else {
//        		hostUri = (new URI("https://" + host)).getHost();
//        	}
//   		} catch (URISyntaxException ex) {
//   			log.error("Failed to parse host: '{}'", ex, host);
//   			throw new WebApplicationException(Response.status(BAD_REQUEST)
//   					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_REQUEST)).build());
//   		}
//
//   		return StringHelper.toLowerCase(hostUri).trim();
//   	}


    public AuthorizationGrant assertHasProtectionScope(String authorization) {
        return validateAuthorization(authorization, UmaScopeType.PROTECTION);
    }

    public AuthorizationGrant assertHasAuthorizationScope(String authorization) {
        return validateAuthorization(authorization, UmaScopeType.AUTHORIZATION);
    }

    private AuthorizationGrant validateAuthorization(String authorization, UmaScopeType umaScopeType) {
        log.trace("Validate authorization: {}", authorization);
        if (StringHelper.isEmpty(authorization)) {
            throw new WebApplicationException(Response.status(UNAUTHORIZED)
                    .entity(errorResponseFactory.getUmaJsonErrorResponse(UNAUTHORIZED_CLIENT)).build());
        }

        String token = tokenService.getTokenFromAuthorizationParameter(authorization);
        if (StringHelper.isEmpty(token)) {
            log.debug("Token is invalid");
            throw new WebApplicationException(Response.status(UNAUTHORIZED)
                    .entity(errorResponseFactory.getUmaJsonErrorResponse(UNAUTHORIZED_CLIENT)).build());
        }

        AuthorizationGrant authorizationGrant = authorizationGrantList.getAuthorizationGrantByAccessToken(token);
        if (authorizationGrant == null) {
            throw new WebApplicationException(Response.status(UNAUTHORIZED)
                    .entity(errorResponseFactory.getUmaJsonErrorResponse(ACCESS_DENIED)).build());
        }

        if (!authorizationGrant.isValid()) {
            throw new WebApplicationException(Response.status(UNAUTHORIZED)
                    .entity(errorResponseFactory.getUmaJsonErrorResponse(INVALID_TOKEN)).build());
        }

        Set<String> scopes = authorizationGrant.getScopes();
        if (!scopes.contains(umaScopeType.getValue())) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_CLIENT_SCOPE)).build());
        }
        return authorizationGrant;
    }

    public void validateRPT(UmaRPT rpt) {
   		if (rpt == null) {
   			throw new WebApplicationException(Response.status(UNAUTHORIZED)
   					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.NOT_AUTHORIZED_PERMISSION)).build());
   		}

   		rpt.checkExpired();
   		if (!rpt.isValid()) {
   			throw new WebApplicationException(Response.status(UNAUTHORIZED)
   					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.NOT_AUTHORIZED_PERMISSION)).build());
   		}
   	}

    public void validatePermission(UmaPermission permission) {
   		if (permission == null || "invalidated".equalsIgnoreCase(permission.getAmHost())) {
   			throw new WebApplicationException(Response.status(BAD_REQUEST)
   					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_TICKET)).build());
   		}

   		permission.checkExpired();
   		if (!permission.isValid()) {
   			throw new WebApplicationException(Response.status(BAD_REQUEST)
   					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.EXPIRED_TICKET)).build());
   		}
   	}

    public void validateResource(org.xdi.oxauth.model.uma.UmaPermission permissionRequest) {
   		String resourceId = permissionRequest.getResourceId();
   		if (StringHelper.isEmpty(resourceId)) {
   			log.error("Resource id is empty");
   			throw new WebApplicationException(Response.status(BAD_REQUEST)
   					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_RESOURCE_SET_ID)).build());
   		}

   		UmaResource resource;
   		try {
   			UmaResource exampleResource = new UmaResource();
   			exampleResource.setDn(resourceService.getBaseDnForResource());
   			exampleResource.setId(resourceId);
            List<UmaResource> resources = resourceService.findResources(exampleResource);
            if (resources.size() != 1) {
       			log.error("Resource isn't registered or there are two resources with same Id");
       			throw new WebApplicationException(Response.status(BAD_REQUEST)
       					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_RESOURCE_SET_ID)).build());
            }
            resource = resources.get(0);
   		} catch (EntryPersistenceException ex) {
   			log.error("Resource isn't registered");
   			throw new WebApplicationException(Response.status(BAD_REQUEST)
   					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_RESOURCE_SET_ID)).build());
   		}

        final List<String> scopeUrls = umaScopeService.getScopeUrlsByDns(resource.getScopes());
        if (!scopeUrls.containsAll(permissionRequest.getScopes())) {
   			log.error("At least one of the scope isn't registered");
   			throw new WebApplicationException(Response.status(BAD_REQUEST)
   					.entity(errorResponseFactory.getUmaJsonErrorResponse(UmaErrorResponseType.INVALID_RESOURCE_SET_SCOPE)).build());
   		}
   	}
}

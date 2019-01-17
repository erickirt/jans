/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.model.token;

import org.xdi.oxauth.model.error.IErrorType;

import java.util.HashMap;
import java.util.Map;

/**
 * Error codes for token revocation error responses.
 *
 * @author Javier Rojas Blum
 * @version January 16, 2019
 */
public enum TokenRevocationErrorResponseType implements IErrorType {

    /**
     * The authorization server does not support the revocation of the presented token type.
     * That is, the client tried to revoke an access token on a server not supporting this feature.
     */
    UNSUPPORTED_TOKEN_TYPE("unsupported_token_type"),

    /**
     * The request is missing a required parameter, includes an unsupported
     * parameter or parameter value, repeats a parameter, includes multiple
     * credentials, utilizes more than one mechanism for authenticating the
     * client, or is otherwise malformed.
     */
    INVALID_CLIENT("invalid_client"),

    /**
     * The request is missing a required parameter, includes an unsupported
     * parameter or parameter value, repeats a parameter, includes multiple
     * credentials, utilizes more than one mechanism for authenticating the
     * client, or is otherwise malformed.
     */
    INVALID_REQUEST("invalid_request");

    private final String paramName;

    private static Map<String, TokenRevocationErrorResponseType> mapByValues = new HashMap<String, TokenRevocationErrorResponseType>();

    static {
        for (TokenRevocationErrorResponseType enumType : values()) {
            mapByValues.put(enumType.getParameter(), enumType);
        }
    }

    TokenRevocationErrorResponseType(String paramName) {
        this.paramName = paramName;
    }

    public static TokenRevocationErrorResponseType getByValue(String value) {
        return mapByValues.get(value);
    }

    /**
     * Gets error parameter.
     *
     * @return error parameter
     */
    @Override
    public String getParameter() {
        return paramName;
    }

    /**
     * Returns a string representation of the object. In this case, the lower
     * case code of the error.
     */
    @Override
    public String toString() {
        return paramName;
    }
}

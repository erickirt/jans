/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.as.client.dev;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 27/09/2012
 */

public enum HostnameVerifierType {
    DEFAULT("default"),
    ALLOW_ALL("allow_all");

    private final String value;

    HostnameVerifierType(String value) {
        this.value = value;
    }

    public static HostnameVerifierType fromString(String value) {
        if (StringUtils.isNotBlank(value)) {
            for (HostnameVerifierType v : values()) {
                if (v.value.equals(value)) {
                    return v;
                }
            }
        }
        return DEFAULT;
    }
}

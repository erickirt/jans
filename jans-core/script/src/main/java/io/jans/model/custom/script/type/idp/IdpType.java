/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.model.custom.script.type.idp;

import java.util.Map;

import io.jans.model.SimpleCustomProperty;
import io.jans.model.custom.script.type.BaseExternalType;

/**
 * Base interface for IDP script
 *
 * @author Yuriy Movchan Date: 06/18/2020
 */
public interface IdpType extends BaseExternalType {

	boolean translateAttributes(Object context, Map<String, SimpleCustomProperty> configurationAttributes);

	boolean updateAttributes(Object context, Map<String, SimpleCustomProperty> configurationAttributes);

}

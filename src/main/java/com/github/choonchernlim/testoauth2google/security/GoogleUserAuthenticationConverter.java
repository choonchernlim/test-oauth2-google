/*
 * Cloud Foundry 2012.02.03 Beta
 * Copyright (c) [2009-2012] VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 */

package com.github.choonchernlim.testoauth2google.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * The default implementation uses USERNAME as user id, which is a long internal ID.
 * This implementation uses EMAIL as user id so that it is easier to lookup custom user data and authorities.
 */
@Service
public class GoogleUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleUserAuthenticationConverter.class);
    private static final String EMAIL = "email";
    private final UserDetailsService userDetailsService;

    @Autowired
    public GoogleUserAuthenticationConverter(final UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public Authentication extractAuthentication(final Map<String, ?> map) {
        LOGGER.debug("map: {}", map);

        if (map.containsKey(EMAIL)) {
            final UserDetails principal = userDetailsService.loadUserByUsername((String) map.get(EMAIL));
            final Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
            return new UsernamePasswordAuthenticationToken(principal, null, authorities);
        }

        return null;
    }
}

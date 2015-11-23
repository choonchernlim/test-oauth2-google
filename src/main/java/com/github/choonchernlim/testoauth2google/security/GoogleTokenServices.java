/*******************************************************************************
 * Cloud Foundry
 * Copyright (c) [2009-2014] Pivotal Software, Inc. All Rights Reserved.
 * <p/>
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 * <p/>
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/
package com.github.choonchernlim.testoauth2google.security;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Copied the RemoteTokenServices and modified for Google token details.
 */
@Service
public class GoogleTokenServices extends RemoteTokenServices {

    private static Logger LOGGER = LoggerFactory.getLogger(GoogleTokenServices.class);

    private final String checkTokenEndpointUrl;
    private final String clientId;
    private final String clientSecret;
    private final AccessTokenConverter tokenConverter;
    private final RestOperations restTemplate;

    @Autowired
    public GoogleTokenServices(@Value("${google.check.token.endpoint.url}") final String checkTokenEndpointUrl,
                               @Value("${google.client.id}") final String clientId,
                               @Value("${google.client.secret}") final String clientSecret,
                               final AccessTokenConverter tokenConverter) {
        this.checkTokenEndpointUrl = checkTokenEndpointUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenConverter = tokenConverter;

        this.restTemplate = new RestTemplate();
        ((RestTemplate) restTemplate).setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            // Ignore 400
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode() != HttpStatus.BAD_REQUEST) {
                    super.handleError(response);
                }
            }
        });
    }

    @Override
    public OAuth2Authentication loadAuthentication(final String accessToken) throws AuthenticationException, InvalidTokenException {
        LOGGER.debug("BEFORE: Access token: {}", accessToken);

        final Map<String, Object> checkTokenResponse = postForMap(accessToken);

        final Map<String, Object> standardizedResponse = transformNonStandardValuesToStandardValues(checkTokenResponse);

        final OAuth2Authentication oAuth2Authentication = tokenConverter.extractAuthentication(standardizedResponse);

        LOGGER.debug("AFTER: oAuth2Authentication: {}", oAuth2Authentication);

        return oAuth2Authentication;
    }

    /**
     * Use Spring Security constant fields defined at http://docs.spring.io/spring-security/oauth/apidocs/constant-values.html.
     */
    private Map<String, Object> transformNonStandardValuesToStandardValues(final Map<String, Object> map) {
        LOGGER.debug("BEFORE: Original map   : {}", map);

        final Map<String, Object> transformedMap = ImmutableMap.<String, Object>builder()
                .putAll(map)
                .put("client_id", map.get("issued_to"))
                .put("user_name", map.get("email"))
                .build();

        LOGGER.debug("AFTER: Transformed map : {}", transformedMap);

        return transformedMap;
    }

    private String getAuthorizationHeader(final String clientId, final String clientSecret) {
        LOGGER.debug("clientId: {}", clientId);
        LOGGER.debug("clientSecret: {}", clientSecret);

        final String credential = String.format("%s:%s", clientId, clientSecret);

        try {
            return "Basic " + new String(Base64.encode(credential.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

    private Map<String, Object> postForMap(final String accessToken) {
        LOGGER.debug("BEFORE: accessToken: {}", accessToken);

        final String url = String.format(checkTokenEndpointUrl, accessToken);
        LOGGER.debug("BEFORE: url: {}", url);

        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
        formData.set("token", accessToken);
        LOGGER.debug("BEFORE: formData: {}", formData);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", getAuthorizationHeader(clientId, clientSecret));
        LOGGER.debug("BEFORE: headers: {}", headers);

        final ParameterizedTypeReference<Map<String, Object>> map = new ParameterizedTypeReference<Map<String, Object>>() {
        };

        final Map<String, Object> checkTokenEndpointResponse = ImmutableMap.copyOf(
                restTemplate
                        .exchange(url,
                                  HttpMethod.POST,
                                  new HttpEntity<MultiValueMap<String, String>>(formData, headers),
                                  map)
                        .getBody());

        LOGGER.debug("AFTER: body: {} of type {}", checkTokenEndpointResponse, checkTokenEndpointResponse.getClass());

        if (checkTokenEndpointResponse.containsKey("error")) {
            LOGGER.debug("check_token returned error: " + checkTokenEndpointResponse.get("error"));
            throw new InvalidTokenException(accessToken);
        }

        return checkTokenEndpointResponse;
    }
}

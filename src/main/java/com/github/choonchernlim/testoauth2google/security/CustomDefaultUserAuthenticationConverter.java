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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Copied from the original implementation of the <code>CustomDefaultUserAuthenticationConverter</code> to fix a bug in the
 * <code>getAuthorities</code> method. Rest all unchanged. Class with the original bug
 * <code>org.springframework.security.oauth2.provider.token.CustomDefaultUserAuthenticationConverter</code>
 *
 * @author Dave Syer
 */
// TODO drop this class and use Spring's CustomDefaultUserAuthenticationConverter, then wire it CustomUserDetails to pull in custom user data
public class CustomDefaultUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    //private static final Logger LOGGER = LoggerFactory.getLogger(CustomDefaultUserAuthenticationConverter.class);

    private static final String EMAIL = "email";

//    private Collection<? extends GrantedAuthority> defaultAuthorities;
//
//    private AuthorityGranter authorityGranter;

//    /**
//     * Default value for authorities if an Authentication is being created and the input has no data for authorities.
//     * Note that unless this property is set, the default Authentication created by {@link #extractAuthentication(Map)}
//     * will be unauthenticated.
//     *
//     * @param defaultAuthorities the defaultAuthorities to set. Default null.
//     */
//    public void setDefaultAuthorities(String[] defaultAuthorities) {
//        LOGGER.debug("defaultAuthorities: {}", defaultAuthorities);
//        this.defaultAuthorities = commaSeparatedStringToAuthorityList(arrayToCommaDelimitedString(defaultAuthorities));
//    }
//
//    /**
//     * Authority granter which can grant additional authority to the user based on custom rules.
//     *
//     * @param authorityGranter
//     */
//    public void setAuthorityGranter(AuthorityGranter authorityGranter) {
//        this.authorityGranter = authorityGranter;
//    }

//    public Authentication extractAuthentication(Map<String, ?> map) {
//        LOGGER.debug("map: {}", map);
//
////        if (map.containsKey(USERNAME)) {
////            return new UsernamePasswordAuthenticationToken(map.get(USERNAME), "N/A", getAuthorities(map));
////        }
//
//        // instead of looking up user_name, which is some long internal ID, use email as user name
//        // so that `CustomUserDetailsService` can use the email to lookup custom user data lookup
//        if (map.containsKey(EMAIL)) {
//            return new UsernamePasswordAuthenticationToken(map.get(EMAIL), null);
//        }
//
//        return null;
//    }

//    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
//        LOGGER.debug("map: {}", map);
//
//        List<GrantedAuthority> authorityList = newArrayList();
//        if (!map.containsKey(AUTHORITIES)) {
//            assignDefaultAuthorities(authorityList);
//        }
//        else {
//            grantAuthoritiesBasedOnValuesInMap(map, authorityList);
//        }
//        grantAdditionalAuthorities(map, authorityList);
//
//        LOGGER.debug("authorityList: {}", authorityList);
//
//        return authorityList;
//    }
//
//    private void grantAuthoritiesBasedOnValuesInMap(Map<String, ?> map, List<GrantedAuthority> authorityList) {
//        List<GrantedAuthority> parsedAuthorities = parseAuthorities(map);
//        authorityList.addAll(parsedAuthorities);
//    }
//
//    private void grantAdditionalAuthorities(Map<String, ?> map, List<GrantedAuthority> authorityList) {
//        if (authorityGranter != null) {
//            authorityList.addAll(authorityGranter.getAuthorities(map));
//        }
//    }
//
//    private void assignDefaultAuthorities(List<GrantedAuthority> authorityList) {
//        if (defaultAuthorities != null) {
//            authorityList.addAll(defaultAuthorities);
//        }
//    }
//
//    private List<GrantedAuthority> parseAuthorities(Map<String, ?> map) {
//        Object authorities = map.get(AUTHORITIES);
//        List<GrantedAuthority> parsedAuthorities;
//        if (authorities instanceof String) {
//            // Bugfix for Spring OAuth codebase
//            parsedAuthorities = commaSeparatedStringToAuthorityList((String) authorities);
//        }
//        else if (authorities instanceof Collection) {
//            parsedAuthorities = commaSeparatedStringToAuthorityList(collectionToCommaDelimitedString((Collection<?>) authorities));
//        }
//        else {
//            throw new IllegalArgumentException("Authorities must be either a String or a Collection");
//        }
//        return parsedAuthorities;
//    }


    private Collection<? extends GrantedAuthority> defaultAuthorities;

    private UserDetailsService userDetailsService;

    /**
     * Optional {@link UserDetailsService} to use when extracting an {@link Authentication} from the incoming map.
     *
     * @param userDetailsService the userDetailsService to set
     */
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Default value for authorities if an Authentication is being created and the input has no data for authorities.
     * Note that unless this property is set, the default Authentication created by {@link #extractAuthentication(Map)}
     * will be unauthenticated.
     *
     * @param defaultAuthorities the defaultAuthorities to set. Default null.
     */
    public void setDefaultAuthorities(String[] defaultAuthorities) {
        this.defaultAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                                                                                             .arrayToCommaDelimitedString(
                                                                                                     defaultAuthorities));
    }

    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }

    // TODO override method to use EMAIL instead of USERNAME as user ID so that UserDetailsService can lookup custom user data using email
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(EMAIL)) {
            Object principal = map.get(EMAIL);
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
            if (userDetailsService != null) {
                UserDetails user = userDetailsService.loadUserByUsername((String) map.get(EMAIL));
                authorities = user.getAuthorities();
                principal = user;
            }
            return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(AUTHORITIES)) {
            return defaultAuthorities;
        }
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                                                                              .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }
}

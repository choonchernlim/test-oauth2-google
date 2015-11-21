package com.github.choonchernlim.testoauth2google.security;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;

@Service
public class GoogleUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleUserDetailsService.class);
    private final OAuth2RestOperations oauth2RestTemplate;

    @Autowired
    public GoogleUserDetailsService(final OAuth2RestOperations oauth2RestTemplate) {
        this.oauth2RestTemplate = oauth2RestTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        LOGGER.debug("userId: {}", email);

        final String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" +
                           oauth2RestTemplate.getAccessToken();
        final ResponseEntity<GoogleProfile> forEntity = oauth2RestTemplate.getForEntity(url, GoogleProfile.class);
        final GoogleProfile googleProfile = forEntity.getBody();

        LOGGER.debug("googleProfile: {}", googleProfile);

        // TODO LIMC hardcode authorities right now, should go against DB
        return new GoogleUserDetails(googleProfile, ImmutableSet.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ));
    }
}

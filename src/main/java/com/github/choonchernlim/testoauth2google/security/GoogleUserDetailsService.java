package com.github.choonchernlim.testoauth2google.security;

import static com.github.choonchernlim.betterPreconditions.preconditions.PreconditionFactory.expect;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;

/**
 * Returns user profile from Google and roles from DB (currently hardcoded).
 */
@Service
public class GoogleUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleUserDetailsService.class);

    private final String userInfoUrl;
    private final OAuth2RestOperations oauth2RestTemplate;

    @Autowired
    public GoogleUserDetailsService(@Value("${google.user.info.url}") final String userInfoUrl,
                                    final OAuth2RestOperations oauth2RestTemplate) {
        this.userInfoUrl = userInfoUrl;
        this.oauth2RestTemplate = oauth2RestTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        expect(email, "email").not().toBeBlank().check();

        LOGGER.debug("userId: {}", email);

        final String url = String.format(userInfoUrl, oauth2RestTemplate.getAccessToken());
        final GoogleProfile googleProfile = oauth2RestTemplate.getForEntity(url, GoogleProfile.class).getBody();

        LOGGER.debug("googleProfile: {}", googleProfile);

        // TODO Hardcode authorities right now, in theory we can use the email to get the user roles from DB
        return new GoogleUserDetails(googleProfile, ImmutableSet.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ));
    }
}

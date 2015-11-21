package com.github.choonchernlim.testoauth2google.security;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);


    // TODO cannot wire this because it is session scoped and session isn't established at this point
    private OAuth2RestTemplate oauth2RestTemplate;

    public void setOauth2RestTemplate(OAuth2RestTemplate oauth2RestTemplate) {
        this.oauth2RestTemplate = oauth2RestTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        LOGGER.debug("userId: {}", email);

//        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" +
//                     oauth2RestTemplate.getAccessToken();
//        ResponseEntity<GoogleProfile> forEntity = oauth2RestTemplate.getForEntity(url, GoogleProfile.class);
        //final GoogleProfile googleProfile = forEntity.getBody();

        // TODO use dummy first until I figure out how to wire OAuth2RestTemplate
        final GoogleProfile googleProfile = new GoogleProfile();

        LOGGER.debug("googleProfile: {}", googleProfile);

        // TODO LIMC hardcode user details for now, need to combine google profile, should go against DB
        return new CustomUserDetails(googleProfile, ImmutableSet.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ));
    }


}

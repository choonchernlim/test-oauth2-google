package com.github.choonchernlim.testoauth2google.security;

import static com.github.choonchernlim.betterPreconditions.preconditions.PreconditionFactory.expect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Logs out by revoking the access token.
 */
@Service
public class GoogleLogoutHandler implements LogoutHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleLogoutHandler.class);

    private final OAuth2RestOperations oAuth2RestTemplate;
    private final String logoutUrl;

    @Autowired
    public GoogleLogoutHandler(@Value("${google.logout.url}") final String logoutUrl,
                               final OAuth2RestOperations oAuth2RestTemplate) {
        this.logoutUrl = logoutUrl;
        this.oAuth2RestTemplate = oAuth2RestTemplate;
    }

    @Override
    public void logout(final HttpServletRequest request,
                       final HttpServletResponse response,
                       final Authentication authentication) {
        expect(request, "request").not().toBeNull().check();
        expect(response, "response").not().toBeNull().check();
        expect(authentication, "authentication").not().toBeNull().check();

        final String url = String.format(logoutUrl, oAuth2RestTemplate.getAccessToken());

        LOGGER.debug("Revoking access token: {}", url);

        oAuth2RestTemplate.execute(url, HttpMethod.GET, null, null);
    }
}

package com.github.choonchernlim.testoauth2google.controller;

import com.github.choonchernlim.testoauth2google.security.GoogleUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/")
public final class IndexController {
//    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

//    @Autowired
//    private OAuth2RestOperations oauth2RestTemplate;

    @RequestMapping(method = RequestMethod.GET)
    public String main(Model model, Authentication authentication) {

//        OAuth2Authentication requestingUser = (OAuth2Authentication) SecurityContextHolder.getContext()
//                .getAuthentication();
//        Object principal = requestingUser.getUserAuthentication().getPrincipal();
//
//
//        LOGGER.debug("credentials: {} of type {}", requestingUser.getCredentials(),requestingUser.getCredentials().getClass());
//        LOGGER.debug("authorities: {}", requestingUser.getAuthorities());
//        LOGGER.debug("details: {} of type {}", requestingUser.getDetails(), requestingUser.getDetails().getClass());
//
//        LOGGER.debug("principal: {} of type {}", principal, principal.getClass());
//
//        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" +
//                     oauth2RestTemplate.getAccessToken();
//        ResponseEntity<GoogleProfile> forEntity = oauth2RestTemplate.getForEntity(url, GoogleProfile.class);
//        final GoogleProfile googleProfile = forEntity.getBody();


        final GoogleUserDetails principal = (GoogleUserDetails) authentication.getPrincipal();

        model.addAttribute("profile", principal);


        return "index";
    }
}

package com.github.choonchernlim.testoauth2google.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/")
public final class IndexController {
    @RequestMapping(method = RequestMethod.GET)
    public String main(Model model, Authentication authentication) {
        model.addAttribute("profile", authentication.getPrincipal());
        return "index";
    }

    @RequestMapping(value = "/logout-success", method = RequestMethod.GET)
    public String logoutSuccess() {
        return "logout-success";
    }
}

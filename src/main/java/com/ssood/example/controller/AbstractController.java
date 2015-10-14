package com.ssood.example.controller;

import com.ssood.example.domain.SpringSecurityExampleUser;
import com.ssood.example.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class AbstractController {

    protected static final String USER = "user";
    protected static final String HOST_URL = "hostURL";

    @Autowired
    private ApplicationService applicationService;

    @ModelAttribute(USER)
    public SpringSecurityExampleUser determineUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            if (SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof SpringSecurityExampleUser) {
                return (SpringSecurityExampleUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
            }
        }
        return null;
    }

    @ModelAttribute(HOST_URL)
    public String determineHostURL() {
        return applicationService.getHostURL();
    }

}
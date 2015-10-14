package com.ssood.example.controller;

import com.ssood.example.service.ApplicationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Map;

@Controller
public class LandingController extends AbstractController {

    private Log log = LogFactory.getLog(LandingController.class);

    @Autowired
    protected ApplicationService applicationService;

    @RequestMapping(value="/")
    public String showLanding(Map<String,Object> model) throws IOException {
        return "landing";
    }

}

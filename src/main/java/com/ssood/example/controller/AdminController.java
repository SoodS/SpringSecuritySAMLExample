package com.ssood.example.controller;

import com.ssood.example.service.ApplicationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping(value="/admin")
public class AdminController extends AbstractController {

    private Log log = LogFactory.getLog(AdminController.class);

    @Autowired
    protected ApplicationService applicationService;

    @RequestMapping(method = RequestMethod.GET)
    public String viewAdmin(Map<String,Object> model) throws IOException {
        return "admin";
    }

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String adminLogin(HttpServletRequest request, Map<String,Object> model) throws IOException {
        if (request.getParameter("error") != null) {
            model.put("error", true);
        }
        return "adminLogin";
    }

}
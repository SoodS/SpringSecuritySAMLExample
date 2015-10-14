package com.ssood.example.controller;

import com.ssood.example.util.StubbedSAMLAttributes;
import org.opensaml.xml.util.Base64;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
@Profile({"uat"})
public class SAMLStubsController extends AbstractController {

    @ModelAttribute("environment")
    public String determineEnvironment() {
        return System.getProperty("system.environment");
    }

    @RequestMapping(value="/samlstubs/idp/samlv20", method = RequestMethod.POST)
    public String handleAuthRequest(@RequestParam("SAMLRequest") String samlRequest, Model model, RedirectAttributes ra) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(new ByteArrayInputStream(Base64.decode(samlRequest)));
        String elementName = doc.getFirstChild().getNodeName();
        String destination = "redirect:/samlstubs/login";

        if (elementName.equals("saml2p:LogoutRequest")) {
            destination += "?doLogout=true";
        }
        return destination;

    }

    @RequestMapping(value="/samlstubs/login", method = { RequestMethod.GET, RequestMethod.POST })
    public String handleStubLogin(@RequestBody(required = false) StubbedSAMLAttributes attributes, Model model) throws IOException {
        if (attributes != null) {
            model.addAttribute("firstName", attributes.getFirstName());
            model.addAttribute("lastName", attributes.getLastName());
            model.addAttribute("mail", attributes.getMail());
            model.addAttribute("doSubmit", true);
        } else {
            model.addAttribute("firstName", "John");
            model.addAttribute("lastName", "Doe");
            model.addAttribute("mail", "test.tester@test.com");
        }
        return "stubbedLogin";
    }

}
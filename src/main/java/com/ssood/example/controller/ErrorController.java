package com.ssood.example.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Controller
public class ErrorController extends AbstractController implements org.springframework.boot.autoconfigure.web.ErrorController {

    private static final String ERROR_PATH = "/error";
    private Log log = LogFactory.getLog(ErrorController.class);

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(value=ERROR_PATH)
    public String displayError(HttpServletRequest request, Map<String,Object> model, @RequestParam(value = "code",required = false) String code, @RequestParam(value = "msg",required = false) String msg) throws IOException {
        model.put("messageKey", msg);

        if (StringUtils.isBlank(code)) {
            Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
            if (statusCode != null) {
                code = statusCode.toString();
            } else if(request.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null) {
                log.info("Error Details " + request.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
                code="800";
            }

        }

        log.info("Error Code : " + code);
        log.info("Error Message : " + request.getAttribute("javax.servlet.error.message"));

        model.put("errorCode", code);
        model.put("errorMessage", request.getAttribute("javax.servlet.error.message"));
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        if (throwable == null) {
            throwable = (Throwable) request.getAttribute("javax.servlet.jsp.jspException");
        }
        if (throwable != null) {
            model.put("errorDetails", ExceptionUtils.getStackTrace(throwable));
        }

        return "error";
    }

}
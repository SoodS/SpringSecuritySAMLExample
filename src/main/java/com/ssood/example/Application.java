package com.ssood.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@ComponentScan
@EnableAutoConfiguration
@ImportResource({"file:src/main/webapp/WEB-INF/spring/application-context.xml", "file:src/main/webapp/WEB-INF/spring/webmvc-context.xml", "file:src/main/webapp/WEB-INF/spring/security-context.xml"})
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication springApp = new SpringApplication(Application.class);
        springApp.addListeners(new ApplicationPidListener());
        springApp.run(args);
    }

}
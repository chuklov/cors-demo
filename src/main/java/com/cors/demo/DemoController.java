package com.cors.demo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class DemoController {

    private static final Logger LOGGER = LogManager.getLogger(DemoController.class);

    @GetMapping
    public String getSomething()
    {
        LOGGER.info("GET SOMETHING");
        return "GET USERS";
    }

}

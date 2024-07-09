package com.cors.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:./config/config.properties")
@EnableCaching
public class DemoApplication {

	private static final Logger LOGGER = LogManager.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		LOGGER.info("Start of the demo application");
		SpringApplication.run(DemoApplication.class, args);
	}

}

package com.example.demo;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import ch.qos.logback.classic.Logger;

@SpringBootApplication
@PropertySource({
	"classpath:application.properties", 
	"classpath:provider.properties"
})

public class DemoApplication {

	static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DemoApplication.class); 
  
	public static void main(String[] args) {
			
		SpringApplication.run(DemoApplication.class, args);
	}

}
package com.scheme.reactive.scheme_reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import ch.qos.logback.classic.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.scheme.reactive.scheme_reactive.feign"})
@ComponentScan(basePackages = {"com.scheme.reactive.scheme_reactive.scheme_reactive"})
public class SchemeReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchemeReactiveApplication.class, args);
		turnOfInsecureLogger();
	}

	private static void turnOfInsecureLogger(){
		Logger log = LoggerFactory.getLogger("org.apache.http.wire");
        log.debug(log.getClass().getCanonicalName());
        ch.qos.logback.classic.Logger lg = (ch.qos.logback.classic.Logger)log;
        lg.setLevel(Level.ERROR);
        lg.setAdditive(false);
    }

}

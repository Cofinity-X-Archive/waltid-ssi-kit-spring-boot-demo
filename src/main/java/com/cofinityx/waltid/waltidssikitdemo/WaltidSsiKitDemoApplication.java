package com.cofinityx.waltid.waltidssikitdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WaltidSsiKitDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaltidSsiKitDemoApplication.class, args);
	}
}
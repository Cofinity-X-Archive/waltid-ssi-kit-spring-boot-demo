package com.cofinityx.waltid.waltidssikitdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ssi")
public record ApplicationSettings(String host, String baseTenant) {
}

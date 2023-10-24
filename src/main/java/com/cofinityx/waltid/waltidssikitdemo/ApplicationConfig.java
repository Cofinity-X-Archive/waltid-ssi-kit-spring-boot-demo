package com.cofinityx.waltid.waltidssikitdemo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.smartsensesolutions.java.commons.specification.SpecificationUtil;
import id.walt.servicematrix.ServiceMatrix;
import id.walt.services.key.KeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

@Configuration
@Slf4j
public class ApplicationConfig {

    private final ResourceLoader resourceLoader;

    private final ServiceMatrix serviceMatrix;

    public ApplicationConfig(ResourceLoader resourceLoader) throws IOException {
        this.resourceLoader = resourceLoader;
        Resource fileResource = resourceLoader.getResource("classpath:service-matrix.properties");
        serviceMatrix = new ServiceMatrix(fileResource.getFile().getAbsolutePath());
    }

    @Bean
    public KeyService keyService(){
        return id.walt.services.key.KeyService.Companion.getService();

    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) throws IOException {
        log.debug("Application ready, set up config");
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Bean
    public SpecificationUtil specificationUtil() {
        return new SpecificationUtil<>();
    }

}

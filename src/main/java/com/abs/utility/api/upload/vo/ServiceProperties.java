package com.abs.utility.api.upload.vo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "service")
@Configuration
public class ServiceProperties {

    private String name;
    private String domain;
    private String protocol;

}

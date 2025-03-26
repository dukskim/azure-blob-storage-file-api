package com.abs.utility.api.upload.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Getter
@Setter
@Primary
public class AzureProperties {

    @Value("${azure.storage.blob.connection-string}")
    private String connectionString;

    @Value("${azure.storage.blob.container-name}")
    private String containerName;

    @Value("${azure.storage.cdn.url}")
    private String cdnUrl;

}

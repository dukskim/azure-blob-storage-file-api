package com.abs.utility.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@Slf4j
public class MessageConfig {


    @Bean
    public MessageSource messageSource(){

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/message/messages");
        messageSource.setDefaultEncoding("UTF-8");
//        messageSource.setCacheSeconds(3);

        return messageSource;

    }



}

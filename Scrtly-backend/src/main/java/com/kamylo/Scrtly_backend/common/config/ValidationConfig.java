package com.kamylo.Scrtly_backend.common.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class ValidationConfig {
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean vb = new LocalValidatorFactoryBean();
        vb.setValidationMessageSource(messageSource);
        return vb;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(LocalValidatorFactoryBean validator) {
        MethodValidationPostProcessor mp = new MethodValidationPostProcessor();
        mp.setValidator(validator);
        return mp;
    }
}

package com.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAspectJAutoProxy
@Import({
    WebConfig.class, 
    DBConfig.class,
    SecurityConfig.class
})
@ComponentScan("com.app.exceptionHandler")
@PropertySource("classpath:application.properties")
public class AppConfig {}

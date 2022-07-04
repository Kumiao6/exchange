package com.miao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @author ：m
 * @date ：Created in 2022/7/3 23:43
 */
@EnableResourceServer
@Configuration
public class ResouceServerConfig extends ResourceServerConfigurerAdapter {

}



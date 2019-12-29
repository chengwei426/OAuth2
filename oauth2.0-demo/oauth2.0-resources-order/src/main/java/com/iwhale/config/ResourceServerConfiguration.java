package com.iwhale.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {


    // 拦截资源, 应该放在网关  开放接口和内部接口要独立出来
    // @EnableResourceServer 开启资源服务中心
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        // 对请求进行拦截, 验证accessToken
        httpSecurity.authorizeRequests().antMatchers("/api/order/**").authenticated();
    }
}

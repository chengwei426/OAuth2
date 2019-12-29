package com.iwhale.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.sql.DataSource;

/**
 * @Description 授权认证服务中心配置 <br>
 *  @EnableAuthorizationServer 开启认证授权服务中心
 * @author cheng.wei <br>
 * @version 1.0<br>
 * @taskId $ <br>
 * @CreateDate 2019/12/21 <br>
 * @since V8.1<br>
 * @see AuthorizationServerConfig <br>
 *
 *     http://localhost:8080/oauth/authorize?response_type=code&client_id=clientId_1&redirect_uri=https://www.baidu.com/
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    // 设置token的有效期 - 2h
    private static final int ACCESS_TOKEN_VALID_SECONDS = 7200;

    private static final int REFRESH_TOKEN_VALID_SECONDS = 7200;

    @Autowired
    // @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Bean
    public TokenStore tokenStore() {
        //return new InMemoryTokenStore(); // 使用内存中的token
        return new JdbcTokenStore(dataSource);
    }

    /**
     * @Description: 配置appId appKey 回调地址 token有效期 <br/>
     * @Param:  <br/>
     * @return:  <br/>
     * @throws  <br/>
     * @Author: cheng.wei <br/>
     * @Date: 2019/12/21 <br/>
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 添加授权用户
        clients.jdbc(dataSource);
    }

    // 设置token请求的类型
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService());
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer authServer) throws Exception {
        // 允许表单认证
        //authServer.allowFormAuthenticationForClients();
        // 允许check_token访问
        authServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }



    AuthenticationManager authenticationManager() {
        AuthenticationManager authenticationManager = new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return daoAuthenticationProvider().authenticate(authentication);
            }
        };

        return authenticationManager;
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // 设置添加用户信息, 正常应该从数据库中读取
    @Bean
    UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(User.withUsername("user_1").password(passwordEncoder().encode("123456"))
                .authorities("ROLE_USER").build());
        userDetailsManager.createUser(User.withUsername("user_2").password(passwordEncoder().encode("123456"))
                .authorities("ROLE_USER").build());

        return userDetailsManager;
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        // 加密方式
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder;
    }
}

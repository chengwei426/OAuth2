package com.iwhale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证授权中心
 *
 * 1. 生成授权连接
 * http://localhost:8080/oauth/authorize?response_type=code&client_id=clientId_1&redirect_uri=https://www.baidu.com/
 *
 * 2. 使用授权码获取accessToken
 *   http://localhost:8080/oauth/token?grant_type=authorization_code&code=Wmw9Xj&redirect_uri=https://www.baidu.com/&scope=all
 *
 *   3. 验证token
 *   http://localhost:8080/oauth/check_token?token=4333481d-39c3-4c58-96c2-fc74bbbf6fe4
 *
 */
@SpringBootApplication
public class OauthServerApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(OauthServerApp.class, args);
    }
}

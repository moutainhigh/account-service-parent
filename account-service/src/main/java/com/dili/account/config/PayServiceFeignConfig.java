package com.dili.account.config;

import org.springframework.context.annotation.Bean;

import feign.RequestInterceptor;

/**
 * 支付服务的Feign配置
 * ps：不要在该类上面加诸如 @Configuration，否则会变成全局配置
 * @Auther: miaoguoxin
 * @Date: 2020/6/29 17:01
 */
public class PayServiceFeignConfig {

    private static final String APPID = "1010";
    private static final String TOKEN = "abcd1010";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("appid", APPID);
            template.header("token", TOKEN);
        };
    }
}

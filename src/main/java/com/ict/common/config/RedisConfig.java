package com.ict.common.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author: DevWenjiang
 * Description: Redis配置类
 * @date : 2020-06-10 17:34
 */
@Configuration
@EnableAutoConfiguration
public class RedisConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        JedisPoolConfig config = new JedisPoolConfig();
        jedisConnectionFactory.setPoolConfig(config);
        return jedisConnectionFactory;
    }

}

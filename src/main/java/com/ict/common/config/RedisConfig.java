package com.ict.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author: DevWenjiang
 * Description: Redis配置类
 * @date : 2020-06-10 17:34
 */
@Configuration
@EnableAutoConfiguration
public class RedisConfig {

    @Value("${spring.redis.defaultExpiration:3600}")
    private Long defaultExpiration;

    /**
     * 创建JedisConnectionFactory对象
     */
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        JedisPoolConfig config = jedisPoolConfig();
        jedisConnectionFactory.setPoolConfig(config);
        return jedisConnectionFactory;
    }

    /**
     * 创建JedisPoolConfig对象
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisPoolConfig jedisPoolConfig(){
        return new JedisPoolConfig();
    }


    @Bean
    public RedisTemplate<String,Object> jdkRedisTemplate(){
        RedisTemplate jdkRedisTemplate = new RedisTemplate();
        jdkRedisTemplate.setConnectionFactory(jedisConnectionFactory());
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        jdkRedisTemplate.setHashKeySerializer(stringRedisSerializer);
        jdkRedisTemplate.setKeySerializer(stringRedisSerializer);
        jdkRedisTemplate.afterPropertiesSet();
        return jdkRedisTemplate;
    }

    /**
     * 创建以jackson序列化方式redisTemplate
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        //设置对象mapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.setVisibility(PropertyAccessor.ALL,JsonAutoDetect.Visibility.ANY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        //设置key与value序列化方式
        redisTemplate.setKeySerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 创建stringRedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(){
        return new StringRedisTemplate(jedisConnectionFactory());
    }

    /**
     * 设置缓存管理
     */
    @Bean
    @ConfigurationProperties("spring.redis")
    public CacheManager cacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
        redisCacheManager.setDefaultExpiration(defaultExpiration);
        return redisCacheManager;
    }


}

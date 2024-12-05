package ru.skillbox.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfiguration {

  @Value("${geo.cache.time-to-alive}")
  private Duration expire;

  @Bean
  public CacheManager redisCacheManager (LettuceConnectionFactory connectionFactory) {
    var defaultConfig = RedisCacheConfiguration.defaultCacheConfig();
    Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
    redisCacheConfigurationMap.put(CacheNames.COUNTRIES_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig()
                                                                            .entryTtl(expire));
    redisCacheConfigurationMap.put(CacheNames.CITIES_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig()
                                                                            .entryTtl(expire));
    return RedisCacheManager.builder(connectionFactory)
          .cacheDefaults(defaultConfig)
          .withInitialCacheConfigurations(redisCacheConfigurationMap)
          .build();
  }

  @Bean
  public LettuceConnectionFactory lettuceConnectionFactory (RedisProperties redisProperties) {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
    configuration.setHostName(redisProperties.getHost());
    configuration.setPort(redisProperties.getPort());
    return new LettuceConnectionFactory(configuration);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate (LettuceConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    return redisTemplate;
  }

}

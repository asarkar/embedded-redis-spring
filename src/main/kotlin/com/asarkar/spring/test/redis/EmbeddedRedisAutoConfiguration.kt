package com.asarkar.spring.test.redis

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(
    name = [
        "redis.embedded.RedisServer"
    ]
)
class EmbeddedRedisAutoConfiguration {
    @Bean
    fun embeddedRedisLifecycle(): EmbeddedRedisLifecycle {
        return EmbeddedRedisLifecycle()
    }
}

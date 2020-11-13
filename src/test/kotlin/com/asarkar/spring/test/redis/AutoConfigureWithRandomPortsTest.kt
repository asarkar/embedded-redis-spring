package com.asarkar.spring.test.redis

import io.lettuce.core.RedisClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureEmbeddedRedis(
    port = 0
)
class AutoConfigureWithRandomPortsTest {
    @Value("\${embedded-redis.port:-1}")
    private var port: Int = -1

    @Test
    fun testConnection() {
        val redisClient = RedisClient
            .create("redis://localhost:$port/")
        val syncCommands = redisClient.connect().sync()
        syncCommands.set("key", "value")
        assertThat(syncCommands.get("key")).isEqualTo("value")
    }
}

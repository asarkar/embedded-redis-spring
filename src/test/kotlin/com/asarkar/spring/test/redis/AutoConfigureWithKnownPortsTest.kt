package com.asarkar.spring.test.redis

import io.lettuce.core.RedisClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureEmbeddedRedis
class AutoConfigureWithKnownPortsTest {
    @Test
    fun testConnection() {
        val redisClient = RedisClient
            .create("redis://localhost:6379/")
        val syncCommands = redisClient.connect().sync()
        syncCommands.set("key", "value")
        assertThat(syncCommands.get("key")).isEqualTo("value")
    }
}

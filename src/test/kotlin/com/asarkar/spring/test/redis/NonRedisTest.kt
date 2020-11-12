package com.asarkar.spring.test.redis

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

@SpringBootTest
class NonRedisTest {
    @Value("\${embedded-redis.port:-1}")
    private var port: Int = Random.nextInt()

    @Test
    fun testConnection() {
        assertThat(port).isEqualTo(-1)
    }
}

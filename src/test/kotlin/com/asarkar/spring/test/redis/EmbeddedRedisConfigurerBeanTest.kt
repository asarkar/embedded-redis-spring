package com.asarkar.spring.test.redis

import io.lettuce.core.RedisClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import redis.embedded.RedisServerBuilder

@SpringBootTest
@AutoConfigureEmbeddedRedis(
    port = 0,
    serverConfigurerClass = "com.asarkar.spring.test.redis.TestEmbeddedRedisConfigurerBean"
)
@Import(EmbeddedRedisConfigurerBeanTestConfiguration::class)
class EmbeddedRedisConfigurerBeanTest {
    @Value("\${embedded-redis.port:-1}")
    private var port: Int = -1

    @Autowired
    private lateinit var embeddedRedisConfigurer: TestEmbeddedRedisConfigurerBean

    @Test
    fun testEmbeddedRedisConfigurer() {
        val redisClient = RedisClient
            .create("redis://localhost:$port/")
        val syncCommands = redisClient.connect().sync()
        syncCommands.set("key", "value")
        assertThat(syncCommands.get("key")).isEqualTo("value")
        assertThat(embeddedRedisConfigurer.called).isTrue
    }
}

@TestConfiguration
open class EmbeddedRedisConfigurerBeanTestConfiguration {
    @Bean
    open fun embeddedRedisConfigurer() = TestEmbeddedRedisConfigurerBean()
}

class TestEmbeddedRedisConfigurerBean : EmbeddedRedisConfigurer {
    var called: Boolean = false

    override fun configure(builder: RedisServerBuilder) {
        called = true
    }
}

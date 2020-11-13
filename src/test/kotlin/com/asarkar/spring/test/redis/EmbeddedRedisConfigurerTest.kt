package com.asarkar.spring.test.redis

import io.lettuce.core.RedisClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import redis.embedded.RedisServerBuilder
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
private val file = Paths.get(AutoConfigureWithRandomPortsTest::class.java.getResource("/").toURI())
    .let { testPath ->
        generateSequence(testPath) {
            if (Files.isDirectory(it) && Files.exists(it.resolve("build.gradle.kts"))) {
                null
            } else {
                it.parent
            }
        }
            .take(10) // should be plenty
            .toList()
            .last()
            .resolve("build")
            .resolve(
                (1..6)
                    .map { Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString("")
            )
    }

@SpringBootTest
@AutoConfigureEmbeddedRedis(
    port = 0,
    serverConfigurerClass = "com.asarkar.spring.test.redis.TestEmbeddedRedisConfigurer"
)
class EmbeddedRedisConfigurerTest {
    @Value("\${embedded-redis.port:-1}")
    private var port: Int = -1

    @Test
    fun testEmbeddedRedisConfigurer() {
        val redisClient = RedisClient
            .create("redis://localhost:$port/")
        val syncCommands = redisClient.connect().sync()
        syncCommands.set("key", "value")
        assertThat(syncCommands.get("key")).isEqualTo("value")
        assertThat(Files.exists(file)).isTrue
    }

    @AfterEach
    fun afterEach() {
        Files.deleteIfExists(file)
    }
}

class TestEmbeddedRedisConfigurer : EmbeddedRedisConfigurer {
    private val log = LoggerFactory.getLogger(TestEmbeddedRedisConfigurer::class.java)

    override fun configure(builder: RedisServerBuilder) {
        Files.createFile(file)
            .also { log.info("Created file: {}", it.toAbsolutePath()) }
    }
}

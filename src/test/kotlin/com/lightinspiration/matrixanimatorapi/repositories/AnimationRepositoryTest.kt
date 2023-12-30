package com.lightinspiration.matrixanimatorapi.repositories

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lightinspiration.matrixanimatorapi.configuration.JacksonConfiguration
import com.lightinspiration.matrixanimatorapi.configuration.TestDataSourceConfiguration
import com.lightinspiration.matrixanimatorapi.configuration.TransactionManagerConfiguration
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional


@ActiveProfiles("integration-test", "test")
@SpringBootTest(
    classes = [
        AnimationRepository::class,
        TestDataSourceConfiguration::class,
        JacksonConfiguration::class,
        TransactionManagerConfiguration::class,
        LiquibaseAutoConfiguration::class
    ]
)
class AnimationRepositoryTest {

    @Autowired
    lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private lateinit var animationRepository: AnimationRepository

    @BeforeEach
    fun setUp() {
        animationRepository = AnimationRepository(namedParameterJdbcTemplate, objectMapper)
    }

    @Test
    @Transactional
    fun `saveAnimation - can save animation record successfully`() {
        val animation = buildAnimation()

        val id = animationRepository.saveAnimation(animation)

        val actual = getAnimationRecord(id)
        assertEquals(animation.copy(id = id), actual)
    }

    @Test
    @Transactional
    fun `updateAnimation - can update an existing animation record successfully`() {
        insertAnimationRecord(Animation("not target animation", 1, 1, 1, 1, emptyList()))
        val animation = Animation("target animation", 1, 1, 1, 1, emptyList())
        val id = insertAnimationRecord(animation)
        val updatedAnimation = Animation("New title", 2, 3, 4, 5, listOf(Frame(0, listOf(0xFFFFFF))))

        animationRepository.updateAnimation(id, updatedAnimation)

        assertEquals(updatedAnimation.copy(id = id), getAnimationRecord(id))
    }


    @Test
    @Transactional
    fun `deleteAnimation - can delete an animation record`() {
        insertAnimationRecord(Animation("first animation", 1, 1, 1, 1, emptyList()))
        val id = insertAnimationRecord(Animation("second animation", 1, 1, 1, 1, emptyList()))

        val numberOfRowsAffected = animationRepository.deleteAnimation(id)

        assertEquals(1, numberOfRowsAffected)
        assertThrows<NoSuchElementException> {
            getAnimationRecord(id)
        }
    }

    @Test
    @Transactional
    fun `deleteAnimation - if record doesn't exist to delete - expect no record change`() {
        val numberOfRowsAffected = animationRepository.deleteAnimation(999)

        assertEquals(0, numberOfRowsAffected)
    }


    @Test
    @Transactional
    fun `getAnimation - if animation record exists - can get it by id`() {
        val expected = buildAnimation()
        val id = insertAnimationRecord(expected)

        val actual = animationRepository.getAnimation(id)

        assertEquals(expected.copy(id = id), actual)
    }

    @Test
    @Transactional
    fun `getAnimation - if animation record doesn't exist - expect null`() {
        val actual = animationRepository.getAnimation(1)

        assertEquals(null, actual)
    }

    @Test
    @Transactional
    fun `getAnimations - if animation records exists - can get list`() {
        val animations = listOf(
            buildAnimation("animation 1"),
            buildAnimation("animation 2"),
        )
        val expected = animations.map {
            val id = insertAnimationRecord(it)
            it.copy(id = id)
        }

        val actual = animationRepository.getAnimations()

        assertEquals(expected, actual)
    }


    private fun insertAnimationRecord(animation: Animation): Int {
        val keyHolder = GeneratedKeyHolder()
        val parameterMap =
            MapSqlParameterSource()
                .addValue("title", animation.title)
                .addValue("frames", objectMapper.writeValueAsString(animation.frames))
                .addValue("userId", animation.userId)
                .addValue("height", animation.height)
                .addValue("width", animation.width)
                .addValue("speed", animation.speed)

        namedParameterJdbcTemplate.update(
            """
                INSERT INTO matrix_animator.animations
                (title, frames, user_id, height, width, speed)
                VALUES
                (:title, :frames::jsonb, :userId, :height, :width, :speed)
            """,
            parameterMap,
            keyHolder,
            arrayOf("id")
        )
        return keyHolder.keys?.get("id") as Int
    }

    private fun getAnimationRecord(id: Int): Animation {
        return namedParameterJdbcTemplate.query(
            """
            SELECT * FROM matrix_animator.animations WHERE :id = id
        """,
            MapSqlParameterSource()
                .addValue("id", id)
        ) { resultSet, _ ->
            return@query Animation(
                resultSet.getString("title"),
                resultSet.getInt("user_id"),
                resultSet.getInt("height"),
                resultSet.getInt("width"),
                resultSet.getInt("speed"),
                objectMapper.readValue(resultSet.getString("frames"), object : TypeReference<List<Frame>>() {}),
                resultSet.getInt("id")
            )
        }
            .first()
    }

    companion object {
        fun buildAnimation(title: String? = null, id: Int? = null): Animation {
            return Animation(
                title ?: "a cool animation",
                1,
                8,
                8,
                1,
                listOf(Frame(1, listOf(0xFFFFFF, 0xCCCCCC))),
                id
            )
        }
    }
}
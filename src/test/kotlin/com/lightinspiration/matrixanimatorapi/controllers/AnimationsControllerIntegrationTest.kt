package com.lightinspiration.matrixanimatorapi.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("integration-test")
class AnimationsControllerIntegrationTest {

    @Autowired
    private lateinit var animationController: AnimationController

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @Test
    fun `saveAnimation - can save an animation`() {
        val animation = buildAnimationInstance()

        animationController.saveAnimation(animation)

        assertEquals(animation, getAnimationFromDatabase())
    }

    @Test
    fun `getAnimation - if record exists - can get animation`() {
        val animation = buildAnimationInstance()
        val insertedId = insertAnimationIntoDatabase(animation)

        val actual = animationController.getAnimation(insertedId)

        assertEquals(animation.copy(id = insertedId), actual)
    }

    private fun insertAnimationIntoDatabase(animation: Animation): Int {
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

    private fun getAnimationFromDatabase() =
        jdbcTemplate.queryForObject("""SELECT * FROM matrix_animator.animations LIMIT 1""") { resultSet, _ ->
            val a = Animation(
                resultSet.getString("title"),
                resultSet.getInt("user_id"),
                resultSet.getInt("height"),
                resultSet.getInt("width"),
                resultSet.getInt("speed"),
                objectMapper.readValue(resultSet.getString("frames"), object : TypeReference<List<Frame>>() {})
            )
            a
        }

    private fun buildAnimationInstance() = Animation(
        "Test animation",
        1,
        8,
        8,
        1,
        listOf(Frame(0, listOf(0xFF00FF, 0x00FF00, 0xFF00FF)))
    )
}
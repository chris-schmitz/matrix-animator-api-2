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
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("integration-test")
class AnimationsControllerIntegrationTest {

    @Autowired
    private lateinit var animationController: AnimationController

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @Test
    fun `saveAnimation - can save an animation`() {
        val animation = Animation(
            "Test animation",
            1,
            8,
            8,
            1,
            listOf(Frame(0, listOf(0xFF00FF, 0x00FF00, 0xFF00FF)))
        )

        animationController.saveAnimation(animation)

        val actual =
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
        assertEquals(animation, actual)
    }
}
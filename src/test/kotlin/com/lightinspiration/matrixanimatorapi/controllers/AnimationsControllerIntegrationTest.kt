package com.lightinspiration.matrixanimatorapi.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.AnimationMeta
import com.lightinspiration.matrixanimatorapi.domain.Frame
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

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
    @Transactional
    fun `saveAnimation - can save an animation`() {
        val animation = buildAnimationInstance()

        val storedAnimationId = animationController.saveAnimation(animation)

        val actual =
            getAnimationsFromDatabase().first { it.id == storedAnimationId }
                ?: fail("Got a null when trying to get animation record from the database.")
        assertEquals(animation.title, actual.title)
        assertEquals(animation.height, actual.height)
        assertEquals(animation.width, actual.width)
        assertEquals(animation.speed, actual.speed)
        assertEquals(animation.frames, actual.frames)
        assertEquals(storedAnimationId, actual.id)
    }

    @Test
    @Transactional
    fun `getAnimation - if record exists - can get animation`() {
        val animation = buildAnimationInstance()
        val insertedId = insertAnimationIntoDatabase(animation)

        val actual = animationController.getAnimation(insertedId)

        assertEquals(animation.copy(id = insertedId), actual)
    }

    @Test
    @Transactional
    fun `getAnimationList - can get list of animation metadata`() {
        val animations =
            listOf(
                buildAnimationInstance("animation 1"),
                buildAnimationInstance("animation 2")
            )
        val expected = animations.map {
            val id = insertAnimationIntoDatabase(it)
            AnimationMeta(id, it.title)
        }

        val actual = animationController.getAnimationList()

        assertEquals(expected, actual)
    }

    @Test
    @Transactional
    fun `updateAnimation - can update an existing animation`() {
        val animation = Animation("original title", 1, 1, 1, 1, emptyList())
        val id = insertAnimationIntoDatabase(animation)
        val updatedAnimation = Animation(
            "New title",
            2,
            3,
            4,
            5,
            listOf(Frame(0, listOf(0xFFFFFF))),
            id
        )

        val actual = animationController.updateAnimation(id, updatedAnimation)

        assertEquals(updatedAnimation, getAnimationsFromDatabase().first())
        assertEquals(id, actual)
    }

    @Test
    @Transactional
    fun `updateAnimation - if the animation record doesn't exist, expect a 422 http exception`() {
        val updatedAnimation = Animation(
            "New title",
            2,
            3,
            4,
            5,
            listOf(Frame(0, listOf(0xFFFFFF))),
            999
        )

        val exception = assertThrows<ResponseStatusException> {
            animationController.updateAnimation(999, updatedAnimation)
        }

        assertEquals(exception.statusCode, UNPROCESSABLE_ENTITY)
    }

    @Test
    @Transactional
    fun `deleteAnimation - can delete an existing animation`() {
        val animations = listOf(
            Animation("animation 1", 1, 1, 1, 1, emptyList()),
            Animation("animation 2", 1, 1, 1, 1, emptyList()),
            Animation("animation 3", 1, 1, 1, 1, emptyList()),
        )
        val insertedAnimations = animations
            .map { it.copy(id = insertAnimationIntoDatabase(it)) }

        animationController.deleteAnimation(insertedAnimations[1].id!!)

        val actual = getAnimationsFromDatabase()
        assertEquals(
            listOf(insertedAnimations[0], insertedAnimations[2]),
            actual
        )
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

    private fun getAnimationsFromDatabase(id: Int? = null): List<Animation> {
        var query = """SELECT * FROM matrix_animator.animations"""
        if (id != null) {
            query += " WHERE id = $id"
        }

        return namedParameterJdbcTemplate.query(
            query,
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
    }

    private fun buildAnimationInstance(title: String? = null) = Animation(
        title ?: "Test animation",
        1,
        8,
        8,
        1,
        listOf(Frame(0, listOf(0xFF00FF, 0x00FF00, 0xFF00FF)))
    )
}
package com.lightinspiration.matrixanimatorapi.repositories

import com.fasterxml.jackson.databind.ObjectMapper
import com.lightinspiration.matrixanimatorapi.domain.Animation
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class AnimationRepository(
    private val template: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper
) {
    fun save(animation: Animation) {
        template.update(
            """
                INSERT INTO matrix_animator.animations
                (title, frames, user_id, height, width, speed)
                VALUES
                (:title, :frames::jsonb, :userId, :height, :width, :speed)
            """,
            MapSqlParameterSource()
                .addValue("title", animation.title)
                .addValue("frames", objectMapper.writeValueAsString(animation.frames))
                .addValue("userId", animation.userId)
                .addValue("height", animation.height)
                .addValue("width", animation.width)
                .addValue("speed", animation.speed)
        )
    }

}

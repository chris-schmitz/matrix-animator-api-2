package com.lightinspiration.matrixanimatorapi.repositories

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

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

    fun getAnimation(id: Int): Animation? {
        return template.query(
            """
            SELECT 
                id, title, frames, user_id, height, width, speed
            FROM 
                matrix_animator.animations
            WHERE 
                id = :id
        """,
            MapSqlParameterSource().addValue("id", id),
            animationRowMapper
        )
            .firstOrNull()
    }

    private val animationRowMapper: (ResultSet, Int) -> Animation =
        { resultSet, _ ->
            Animation(
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

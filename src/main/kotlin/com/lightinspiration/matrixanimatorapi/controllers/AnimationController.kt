package com.lightinspiration.matrixanimatorapi.controllers

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/animations")
class AnimationController(
    private val animationService: AnimationService,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {


    @GetMapping("/{animationId}")
    fun getAnimations(
        @PathVariable animationId: Int
    ): List<Animation>? {
        val result = jdbcTemplate.query(
            "SELECT * FROM matrix_animator.animations WHERE id = :id",
            MapSqlParameterSource().addValue("id", animationId)
        ) { it, _ ->
            Animation(
                it.getString("title"),
                it.getInt("user_id"),
                it.getInt("height"),
                it.getInt("width"),
                it.getInt("speed"),
                emptyList(),
                //ObjectMapper().readTree(it.getString("frames")).,
                it.getInt("id")
            )
        }
        return result
    }

    @PostMapping
    fun saveAnimation(@RequestBody animation: Animation) {
        animationService.saveAnimation(animation)
    }
}

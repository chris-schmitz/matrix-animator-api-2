package com.lightinspiration.matrixanimatorapi.controllers

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/animations")
class AnimationController(
    private val animationService: AnimationService,
    private val jdbcTemplate: JdbcTemplate
) {

    @GetMapping
    fun getAnimations(): Int? {
        val result = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM matrix_animator.animations",
            Int::class.java
        )
        return result
    }

    @PostMapping
    fun saveAnimation(@RequestBody animation: Animation) {
        animationService.saveAnimation(animation)
    }
}

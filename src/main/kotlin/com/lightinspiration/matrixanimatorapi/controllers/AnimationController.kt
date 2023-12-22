package com.lightinspiration.matrixanimatorapi.controllers

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/rest/animations")
class AnimationController(
    private val animationService: AnimationService,
) {

    @GetMapping("/{id}")
    fun getAnimation(@PathVariable("id") id: Int): Animation {
        return animationService.getAnimation(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun saveAnimation(@RequestBody animation: Animation) {
        animationService.saveAnimation(animation)
    }
}

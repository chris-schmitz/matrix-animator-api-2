package com.lightinspiration.matrixanimatorapi.controllers

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/animations")
class AnimationController(
    private val animationService: AnimationService,
) {

    @GetMapping("/test")
    fun getAnimations(): String {
        return "worked again!! :O :O :O :nice:"
    }

    @PostMapping
    fun saveAnimation(@RequestBody animation: Animation) {
        animationService.saveAnimation(animation)
    }
}

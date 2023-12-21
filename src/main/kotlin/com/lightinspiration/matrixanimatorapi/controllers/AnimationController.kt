package com.lightinspiration.matrixanimatorapi.controllers

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/animations")
class AnimationController(
    private val animationService: AnimationService,
) {

    // TODO: rip out after adding a legit get call
    // ! don't forget to pull it out of the http client.
    @GetMapping("/test")
    fun test(): String {
        return "worked again!! :O :O :O :nice:"
    }

    @PostMapping
    fun saveAnimation(@RequestBody animation: Animation) {
        animationService.saveAnimation(animation)
    }
}

package com.lightinspiration.matrixanimatorapi.controllers

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/animations")
class AnimationController(
    private val animationService: AnimationService
) {


    @PostMapping
    fun saveAnimation(@RequestBody animation: Animation){
        animationService.saveAnimation(animation)
    }
}

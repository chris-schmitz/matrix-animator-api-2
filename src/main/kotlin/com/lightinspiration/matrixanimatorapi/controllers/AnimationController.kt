package com.lightinspiration.matrixanimatorapi.controllers

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.AnimationMeta
import com.lightinspiration.matrixanimatorapi.repositories.NoRecordToUpdateException
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/rest/animations")
class AnimationController(
    private val animationService: AnimationService,
) {

    @GetMapping("/{id}")
    fun getAnimation(@PathVariable("id") id: Int): Animation {
        return animationService.getAnimation(id) ?: throw ResponseStatusException(NOT_FOUND)
    }

    @GetMapping
    fun getAnimationList(): List<AnimationMeta> {
        return animationService.getAnimationList()
    }

    @PostMapping
    fun saveAnimation(@RequestBody animation: Animation): Int {
        return animationService.saveAnimation(animation)
    }

    @PutMapping("/{id}")
    fun updateAnimation(@PathVariable("id") animationId: Int, @RequestBody animation: Animation): Int {
        try {
            return animationService.updateAnimation(animationId, animation)
        } catch (exception: NoRecordToUpdateException) {
            throw ResponseStatusException(UNPROCESSABLE_ENTITY, "We can't update an animation without it's ID.")
        }

    }

    @DeleteMapping("/{id}")
    fun deleteAnimation(@PathVariable("id") id: Int) {
        animationService.deleteAnimation(id)
    }
}

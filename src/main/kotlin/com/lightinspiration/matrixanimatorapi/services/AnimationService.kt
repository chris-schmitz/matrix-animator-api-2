package com.lightinspiration.matrixanimatorapi.services

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.AnimationMeta
import com.lightinspiration.matrixanimatorapi.repositories.AnimationRepository
import org.springframework.stereotype.Service

@Service
class AnimationService(
    val animationRepository: AnimationRepository
) {
    fun saveAnimation(animation: Animation) {
        animationRepository.saveAnimation(animation)
    }

    fun getAnimation(id: Int): Animation? {
        return animationRepository.getAnimation(id)
    }

    fun getAnimationList(): List<AnimationMeta> {
        return animationRepository
            .getAnimations()
            .map { it.metadata() }
    }

    fun updateAnimation(id: Int, animation: Animation) {
        animationRepository.updateAnimation(id, animation)
    }

    fun deleteAnimation(id: Int) {
        animationRepository.deleteAnimation(id)
    }

}

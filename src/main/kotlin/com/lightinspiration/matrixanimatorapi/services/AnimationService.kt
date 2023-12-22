package com.lightinspiration.matrixanimatorapi.services

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.repositories.AnimationRepository
import org.springframework.stereotype.Service

@Service
class AnimationService(
    val animationRepository: AnimationRepository
) {
    fun saveAnimation(animation: Animation) {
        animationRepository.save(animation)
    }

    fun getAnimation(id: Int): Animation? {
        return animationRepository.getAnimation(id)
    }

}

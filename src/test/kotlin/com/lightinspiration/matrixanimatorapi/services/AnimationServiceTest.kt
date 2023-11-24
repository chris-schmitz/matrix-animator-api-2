package com.lightinspiration.matrixanimatorapi.services

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import com.lightinspiration.matrixanimatorapi.repositories.AnimationRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AnimationServiceTest{

    @Mock
    private lateinit var animationRepository: AnimationRepository

    @InjectMocks
    private lateinit var animationService: AnimationService

    @Test
    fun `saveAnimation - can save an animation`() {
        val animation = Animation(
            "a cool annimation",
            "someuuuid",
            8,
            8,
            1,
            listOf(Frame(1, listOf(0xFFFFFF, 0xCCCCCC)))
        )

        animationService.saveAnimation(animation)

        verify(animationRepository).save(animation)
    }
}
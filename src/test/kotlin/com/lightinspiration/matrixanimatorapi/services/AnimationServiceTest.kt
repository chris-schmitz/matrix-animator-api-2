package com.lightinspiration.matrixanimatorapi.services

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import com.lightinspiration.matrixanimatorapi.repositories.AnimationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever


@ExtendWith(MockitoExtension::class)
class AnimationServiceTest {

    @Mock
    private lateinit var animationRepository: AnimationRepository

    @InjectMocks
    private lateinit var animationService: AnimationService

    @Test
    fun `getAnimation - if animation record exists - can get it by id `() {
        val id = 1
        val expected = buildAnimation(id)
        whenever(animationRepository.getAnimation(id))
            .thenReturn(expected)

        val actual = animationService.getAnimation(id)

        assertEquals(expected, actual)
    }

    @Test()
    fun `getAnimation - if animation does not exist - throw 404`() {
        val id = 1
        whenever(animationRepository.getAnimation(id))
            .thenReturn(null)

        val actual = animationService.getAnimation(id)

        assertEquals(null, actual)
    }

    @Test
    fun `saveAnimation - can save an animation`() {
        val animation = buildAnimation()

        animationService.saveAnimation(animation)

        verify(animationRepository).save(animation)
    }

    companion object {
        fun buildAnimation(id: Int? = null): Animation {
            return Animation(
                "a cool annimation",
                1,
                8,
                8,
                1,
                listOf(Frame(1, listOf(0xFFFFFF, 0xCCCCCC))),
                id
            )
        }
    }
}
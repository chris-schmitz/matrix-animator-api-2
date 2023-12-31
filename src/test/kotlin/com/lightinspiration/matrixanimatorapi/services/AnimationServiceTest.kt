package com.lightinspiration.matrixanimatorapi.services

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.AnimationMeta
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

    @Test
    fun `getAnimation - if animation does not exist - throw 404`() {
        val id = 1
        whenever(animationRepository.getAnimation(id))
            .thenReturn(null)

        val actual = animationService.getAnimation(id)

        assertEquals(null, actual)
    }

    @Test
    fun `getAnimationsList - if animations exist - expect list of animation metadata`() {
        val animations = listOf(
            buildAnimation(1, "animation 1"),
            buildAnimation(2, "animation 2")
        )
        whenever(animationRepository.getAnimations())
            .thenReturn(animations)
        val expected = listOf(
            AnimationMeta(1, "animation 1"),
            AnimationMeta(2, "animation 2")
        )

        val actual = animationService.getAnimationList()

        assertEquals(expected, actual)
    }

    @Test
    fun `saveAnimation - can save an animation and return the stored id`() {
        val animation = buildAnimation()
        val storedAnimationId = 5
        whenever(animationRepository.saveAnimation(animation))
            .thenReturn(storedAnimationId)

        val actual = animationService.saveAnimation(animation)

        assertEquals(storedAnimationId, actual)
    }


    @Test
    fun `updateAnimation - can update an animation`() {
        val id = 10
        val animation = buildAnimation(id)
        whenever(animationRepository.updateAnimation(id, animation)).thenReturn(id)

        val actual = animationService.updateAnimation(id, animation)

        assertEquals(id, actual)
    }

    @Test
    fun `deleteAnimation - can delete an animation`() {
        val id = 10

        animationService.deleteAnimation(id)

        verify(animationRepository).deleteAnimation(id)
    }

    companion object {
        fun buildAnimation(id: Int? = null, title: String? = null): Animation {
            return Animation(
                title ?: "a cool animation",
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
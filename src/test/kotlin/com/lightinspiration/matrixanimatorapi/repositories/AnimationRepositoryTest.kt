package com.lightinspiration.matrixanimatorapi.repositories

import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AnimationRepositoryTest {
    private lateinit var animationRepository: AnimationRepository

    @BeforeEach
    fun setUp() {
        animationRepository = AnimationRepository()
    }

    @Test
    fun `saveAnimation - can save animation record successfully`() {
        val animation = Animation(
            "a cool annimation",
            "someuuuid",
            8,
            8,
            1,
            listOf(Frame(1, listOf(0xFFFFFF, 0xCCCCCC)))
        )

        animationRepository.save(animation)


    }
}
package com.lightinspiration.matrixanimatorapi

import com.lightinspiration.matrixanimatorapi.controllers.AnimationController
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [AnimationController::class])
class AnimationsControllerTest {

    @MockBean
    private lateinit var animationService: AnimationService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `saveAnimation - can save an animation`() {
        val animation = Animation(
            "Test animation",
            "someuuid",
            8,
            8,
            1,
            listOf(Frame(0, listOf(0xFF00FF, 0x00FF00, 0xFF00FF)))
        )
        val request = MockMvcRequestBuilders
            .post("/rest/animations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(animation.toJson())

        mockMvc.perform(request)
            .andExpect { status().isOk }

        verify(animationService).saveAnimation(animation)
    }
}
package com.lightinspiration.matrixanimatorapi.web

import com.lightinspiration.matrixanimatorapi.controllers.AnimationController
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [AnimationController::class])
class AnimationsWebLayerTest {

    @MockBean
    private lateinit var animationService: AnimationService

    @Autowired
    private lateinit var mockMvc: MockMvc

    //TODO: ripout once we have a legit get
    @Test
    fun `can hit test get endpoint`() {
        mockMvc.perform(get("/rest/animations/test"))
            .andExpect(status().isOk)
    }

    @Test
    fun `can save an animation`() {
        val animation = Animation("an animation", 1, 8, 8, 2, listOf(Frame(0, listOf(0xFFFFFF))))
        val request = MockMvcRequestBuilders
            .post("/rest/animations")
            .content(animation.toJson())
            .contentType(APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)

        verify(animationService).saveAnimation(animation)
    }


}
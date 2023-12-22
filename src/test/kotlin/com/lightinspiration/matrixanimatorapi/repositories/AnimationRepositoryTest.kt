package com.lightinspiration.matrixanimatorapi.repositories

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lightinspiration.matrixanimatorapi.configuration.JacksonConfiguration
import com.lightinspiration.matrixanimatorapi.configuration.TestDataSourceConfiguration
import com.lightinspiration.matrixanimatorapi.configuration.TransactionManagerConfiguration
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource


//TODO: !!!!! WHY DO I HAVE TO CALL LIQUIBASE BY HAND HERE  BUT NOT IN THE INTEGRATION TEST?!?!?!?
@ActiveProfiles("integration-test", "test")
@SpringBootTest(
    classes = [
        AnimationRepository::class,
        TestDataSourceConfiguration::class,
        JacksonConfiguration::class,
        TransactionManagerConfiguration::class
    ]
)
class AnimationRepositoryTest {

    @Autowired
    lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    @Qualifier("test-postgres-datasource")
    lateinit var dataSource: DataSource

    private lateinit var animationRepository: AnimationRepository


    @BeforeEach
    fun setUp() {
        TestDataSourceConfiguration.buildSchema(dataSource)
        animationRepository = AnimationRepository(namedParameterJdbcTemplate, objectMapper)
    }

    @Test
    @Transactional
    fun `saveAnimation - can save animation record successfully`() {
        val animation = Animation("a cool annimation", 1, 8, 8, 1, listOf(Frame(1, listOf(0xFFFFFF, 0xCCCCCC))))

        animationRepository.save(animation)

        val actual = namedParameterJdbcTemplate.query(
            """
            SELECT * FROM matrix_animator.animations
        """,
        ) { resultSet, _ ->
            val a = Animation(
                resultSet.getString("title"),
                resultSet.getInt("user_id"),
                resultSet.getInt("height"),
                resultSet.getInt("width"),
                resultSet.getInt("speed"),
                objectMapper.readValue(resultSet.getString("frames"), object : TypeReference<List<Frame>>() {})
            )
            a
        }
        assertEquals(listOf(animation), actual)
    }

    @Test
    @Transactional
    fun `1saveAnimation - can save animation record successfully`() {
        val animation = Animation("a cool annimation", 1, 8, 8, 1, listOf(Frame(1, listOf(0xFFFFFF, 0xCCCCCC))))

        animationRepository.save(animation)

        val actual = namedParameterJdbcTemplate.query(
            """
            SELECT * FROM matrix_animator.animations
        """,
        ) { resultSet, _ ->
            val a = Animation(
                resultSet.getString("title"),
                resultSet.getInt("user_id"),
                resultSet.getInt("height"),
                resultSet.getInt("width"),
                resultSet.getInt("speed"),
                objectMapper.readValue(resultSet.getString("frames"), object : TypeReference<List<Frame>>() {})
            )
            a
        }
        assertEquals(listOf(animation), actual)
    }
}
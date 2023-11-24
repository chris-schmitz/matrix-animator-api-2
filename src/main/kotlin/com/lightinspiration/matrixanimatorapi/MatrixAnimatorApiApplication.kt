package com.lightinspiration.matrixanimatorapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class MatrixAnimatorApiApplication

fun main(args: Array<String>) {
	runApplication<MatrixAnimatorApiApplication>(*args)
}

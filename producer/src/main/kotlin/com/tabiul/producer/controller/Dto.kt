package com.tabiul.producer.controller

import javax.validation.constraints.NotBlank

data class Book(
    val name: String,
    val author: String
)

data class Request(

    @field:NotBlank
    val name: String?,

    @field:NotBlank
    val author: String?
)
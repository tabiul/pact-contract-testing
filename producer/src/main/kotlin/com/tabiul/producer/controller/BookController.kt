package com.tabiul.producer.controller

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@Controller
@RequestMapping("/api")
class BookController {

    @GetMapping("/book/{id}")
    @ResponseBody
    fun get(@PathVariable id: Long): Book {
        if (id == 1L) {
            return Book(name = "ABC", author = "tabiul")
        }
        throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PostMapping("/book")
    @ResponseBody
    fun save(@Valid @RequestBody request: Request): Book {
        return Book(name = request.name!!, author = request.author!!)
    }
}
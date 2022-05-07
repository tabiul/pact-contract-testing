package com.tabiul.consumer.client

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class ApiClient(private val restTemplate: RestTemplate) {
    fun get(id: Long): Book? {
        return restTemplate.getForObject("/book/{id}", Book::class.java, mapOf("id" to id))
    }

    fun save(book: Book): Book? {
        return restTemplate.postForObject("/book", book, Book::class.java)
    }
}
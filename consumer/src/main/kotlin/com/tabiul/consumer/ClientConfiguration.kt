package com.tabiul.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class ClientConfiguration {
    @Bean
    fun restTemplate(): RestTemplate {
        val builder = RestTemplateBuilder()
        return builder.rootUri("http://localhost:8080/api").build()
    }

    @Bean
    fun jacksonMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(KotlinModule.Builder().build())
        return objectMapper
    }
}
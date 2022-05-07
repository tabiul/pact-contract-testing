package com.tabiul.consumer.client

import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import org.apache.http.impl.NoConnectionReuseStrategy
import org.apache.http.impl.client.HttpClientBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate


@SpringBootTest
@ExtendWith(SpringExtension::class)
@ExtendWith(PactConsumerTestExt::class)
@ContextConfiguration(classes = [TestClientConfiguration::class])
@PactTestFor(port = "8081")
class ApiClientTests {

    @Autowired
    private lateinit var apiClient: ApiClient

    @Test
    @PactTestFor(pactMethod = "getBookThatIsFound")
    fun testGetShouldReturn() {
        val book = apiClient.get(1)
        assert(book?.name == "ABC")
        assert(book?.author == "tabiul")
    }

    @Test
    @PactTestFor(pactMethod = "getBookThatIsNotFound")
    fun testNotFoundShouldReturn404() {
        val e = assertThrows<HttpStatusCodeException> { apiClient.get(2) }
        assert(e.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    @PactTestFor(pactMethod = "saveBookSuccessfully")
    fun testSaveShouldSave() {
        val book = apiClient.save(Book(name = "Pact Contract", author = "Dius"))
        assert(book?.name == "Pact Contract")
        assert(book?.author == "Dius")
    }

    @Pact(consumer = "BookConsumer", provider = "BookProvider")
    private fun getBookThatIsFound(builder: PactDslWithProvider): RequestResponsePact {
        return builder.given("book exist")
            .uponReceiving("get one book")
            .method("GET")
            .path("/api/book/1")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""{"name": "ABC", "author": "tabiul"}""")
            .toPact()
    }

    @Pact(consumer = "BookConsumer", provider = "BookProvider")
    private fun getBookThatIsNotFound(builder: PactDslWithProvider): RequestResponsePact {
        return builder.given("book does not exist")
            .uponReceiving("get one book")
            .method("GET")
            .path("/api/book/2")
            .willRespondWith()
            .status(404)
            .toPact()
    }

    @Pact(consumer = "BookConsumer", provider = "BookProvider")
    private fun saveBookSuccessfully(builder: PactDslWithProvider): RequestResponsePact {
        return builder.given("book does not exist")
            .uponReceiving("save a new book")
            .method("POST")
            .body("""{"name": "Pact Contract", "author": "Dius"}""")
            .path("/api/book")
            .willRespondWith()
            .status(200)
            .headers(mapOf("Content-Type" to "application/json"))
            .body("""{"name": "Pact Contract", "author": "Dius"}""")
            .toPact()
    }

}


@TestConfiguration
class TestClientConfiguration {

    @Bean
    fun restTemplate(): RestTemplate {
        /**
         * The reason this is needed is due to the fact same port is used for multiple test
         * https://github.com/pact-foundation/pact-jvm/issues/1383
         * Pact expects each test to use a random port
         * This can be done via step mentioned here https://docs.pact.io/implementation_guides/jvm/provider/spring
         * Since using a test configuration this is difficult
         */
        val requestFactory = HttpComponentsClientHttpRequestFactory(
            HttpClientBuilder
                .create()
                .setConnectionReuseStrategy(NoConnectionReuseStrategy()).build()
        )

        val builder = RestTemplateBuilder()
        return builder
            .rootUri("http://localhost:8081/api")
            .requestFactory { requestFactory }
            .build()
    }
}
package one.digitalinnovation.beerstock.controller;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import one.digitalinnovation.beerstock.modal.BeerRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class BeerTestController {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String BEERS_PATH = "/api/v1/beers";
    private Response response;

    @Test
    public void createBeer() {

        BeerRequest beerToCreate = BeerRequest.builder()
                .name("Brahma")
                .brand("Ambev")
                .max(50)
                .quantity(10)
                .type("LAGER")
                .build();

        response = given()
                .contentType("application/json")
                .body(beerToCreate)
                .log().body()
                .when()
                .post(BASE_URL + BEERS_PATH)
                .then()
                .extract()
                .response();

        // Usando assertThat do Hamcrest para validar o status code
        MatcherAssert.<Integer>assertThat(response.statusCode(), equalTo(201));

        log.info("Status Code: {}", response.statusCode());
        log.info("Resultado do Teste - Corpo: \n{}", response.asPrettyString());
    }

    @Test
    public void beerAlreadyRegistered() {
        BeerRequest beerToCreate = BeerRequest.builder()
                .name("Brahma")
                .brand("Ambev")
                .max(50)
                .quantity(10)
                .type("LAGER")
                .build();

        response = given()
                .contentType("application/json")
                .body(beerToCreate)
                .log().body()
                .when()
                .post(BASE_URL + BEERS_PATH)
                .then()
                .extract()
                .response();

        // Usando assertThat do Hamcrest para validar o status code
        assertThat(response.statusCode(), equalTo(400));
        log.info("StatusCode: {}", response.statusCode());
        assertThat(response.jsonPath().getString("message"), equalTo("Beer with name Brahma already registered in the system."));
        log.info("Teste de Cerveja Já Cadastrada - Corpo: \n{}", response.asPrettyString());
    }

}
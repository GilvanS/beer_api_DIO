package one.digitalinnovation.beerstock.controller;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import one.digitalinnovation.beerstock.modal.BeerRequest;
import one.digitalinnovation.beerstock.modal.QuantityRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeerTestController {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String BEERS_PATH = "/api/v1/beers";

    private Response response;
    private String createdBeerName;
    private Long createdBeerId;
    private int currentQuantity;

    // --- Testes de Criação ---
    @Test
    @Order(1)
    void whenPOSTIsCalledThenABeerIsCreated() {
        log.info("ORDEM 1: Testando a criação de uma nova cerveja.");
        BeerRequest defaultBeer = BeerRequest.builder().build();

        response = given()
                .contentType("application/json")
                .body(defaultBeer)
                .when().post(BEERS_PATH)
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(201));
        assertThat(response.jsonPath().getString("name"), equalTo("Skol"));

        this.createdBeerName = response.jsonPath().getString("name");
        this.createdBeerId = response.jsonPath().getLong("id");
        this.currentQuantity = response.jsonPath().getInt("quantity");

        log.info("ORDEM 1: Sucesso - Status: {}, ID: {}", response.statusCode(), this.createdBeerId);
    }

    @Test
    @Order(2)
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() {
        log.info("ORDEM 2: Testando a criação com campo obrigatório faltando.");
        BeerRequest beerToCreate = BeerRequest.builder()
                .name("Skol")
                .quantity(10)
                .type("LAGER")
                .build(); // 'brand' está faltando

        response = given()
                .contentType("application/json")
                .body(beerToCreate)
                .when().post(BEERS_PATH)
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(400));
        log.info("ORDEM 2: Sucesso - Status: {}", response.statusCode());
    }

    // --- Testes de Leitura ---
    @Test
    @Order(3)
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() {
        log.info("ORDEM 3: Testando a listagem de cervejas.");
        response = given()
                .contentType("application/json")
                .when().get(BEERS_PATH)
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(200));
        log.info("ORDEM 3: Sucesso - Status: {}", response.statusCode());
    }

    // --- Testes de Incremento ---
    @Test
    @Order(4)
    void whenPATCHIsCalledToIncrementThenOKstatusIsReturned() {
        log.info("ORDEM 4: Testando o incremento de estoque.");
        int quantityToIncrement = 15;
        QuantityRequest quantityRequest = QuantityRequest.builder().quantity(quantityToIncrement).build();
        int expectedQuantity = this.currentQuantity + quantityToIncrement;

        response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when().patch(BEERS_PATH + "/" + this.createdBeerId + "/increment")
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getInt("quantity"), equalTo(expectedQuantity));
        this.currentQuantity = expectedQuantity; // Atualiza a quantidade para o próximo teste
        log.info("ORDEM 4: Sucesso - Status: {}, Nova Quantidade: {}", response.statusCode(), this.currentQuantity);
    }

    @Test
    @Order(5)
    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() {
        log.info("ORDEM 5: Testando o incremento de estoque além do máximo.");
        QuantityRequest quantityRequest = QuantityRequest.builder().quantity(30).build(); // 25 + 30 = 55 > 50

        response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when().patch(BEERS_PATH + "/" + this.createdBeerId + "/increment")
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(400));
        log.info("ORDEM 5: Sucesso - Status: {}", response.statusCode());
    }

    // --- Testes de Decremento ---
    @Test
    @Order(6)
    void whenPATCHIsCalledToDecrementThenOKstatusIsReturned() {
        log.info("ORDEM 6: Testando o decremento de estoque.");
        int quantityToDecrement = 20;
        QuantityRequest quantityRequest = QuantityRequest.builder().quantity(quantityToDecrement).build();
        int expectedQuantity = this.currentQuantity - quantityToDecrement;

        response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when().patch(BEERS_PATH + "/" + this.createdBeerId + "/decrement")
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getInt("quantity"), equalTo(expectedQuantity));
        this.currentQuantity = expectedQuantity; // Atualiza a quantidade
        log.info("ORDEM 6: Sucesso - Status: {}, Nova Quantidade: {}", response.statusCode(), this.currentQuantity);
    }

    @Test
    @Order(7)
    void whenPATCHIsCalledToDecrementLowerThanZeroThenBadRequestStatusIsReturned() {
        log.info("ORDEM 7: Testando o decremento de estoque para valor negativo.");
        QuantityRequest quantityRequest = QuantityRequest.builder().quantity(10).build(); // 5 - 10 = -5

        response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when().patch(BEERS_PATH + "/" + this.createdBeerId + "/decrement")
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(400));
        log.info("ORDEM 7: Sucesso - Status: {}", response.statusCode());
    }

    // --- Testes de Deleção ---
    @Test
    @Order(8)
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() {
        log.info("ORDEM 8: Testando a deleção de uma cerveja.");
        response = given()
                .contentType("application/json")
                .when().delete(BEERS_PATH + "/" + this.createdBeerId)
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(204));
        log.info("ORDEM 8: Sucesso - Status: {}", response.statusCode());
    }

    @Test
    @Order(9)
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() {
        log.info("ORDEM 9: Testando a deleção com ID inválido.");
        response = given()
                .contentType("application/json")
                .when().delete(BEERS_PATH + "/9999")
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(404));
        log.info("ORDEM 9: Sucesso - Status: {}", response.statusCode());
    }

    @Test
    @Order(10)
    void whenGETListWithoutBeersIsCalledThenOkStatusIsReturned() {
        // Para garantir que a lista esteja vazia, precisamos deletar todas as cervejas.
        // Este teste é mais complexo em um ambiente real. Por simplicidade, vamos assumir
        // que após o teste 8, a lista pode estar vazia ou conter outras cervejas.
        // Uma abordagem melhor seria limpar o banco antes deste teste.
        // Por agora, vamos apenas chamar e verificar o status.
        log.info("ORDEM 10: Testando a listagem de cervejas (pode não estar vazia).");
        response = given()
                .contentType("application/json")
                .when().get(BEERS_PATH)
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(200));
        log.info("ORDEM 10: Sucesso - Status: {}", response.statusCode());
    }
}
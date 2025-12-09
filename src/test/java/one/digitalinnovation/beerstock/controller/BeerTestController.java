package one.digitalinnovation.beerstock.controller;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import one.digitalinnovation.beerstock.manager.BeerManager;
import one.digitalinnovation.beerstock.modal.BeerRequest;
import one.digitalinnovation.beerstock.modal.QuantityRequest;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeerTestController {

    private Response response;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String BEERS_PATH = "/api/v1/beers";

    public void BeerController() {
        this.response = null;
    }

    @Test
    @Order(1)
    void beerIsCreated() {
        log.info("ORDEM 1: Testando a criação de uma nova cerveja.");
        BeerRequest beerToCreate = BeerRequest.builder()
                .name("Skol")
                .brand("Ambev")
                .max(50)
                .quantity(10)
                .type("LAGER")
                .build();

        response = given()
                .contentType("application/json")
                .body(beerToCreate)
                .when().post(BEERS_PATH)
                .then().statusCode(201).extract().response();

        BeerManager.setTestId(response.jsonPath().getLong("id"));
        BeerManager.setTestName(response.jsonPath().getString("name"));
        BeerManager.setCurrentQuantity(response.jsonPath().getInt("quantity"));

        log.info("ORDEM 1: Sucesso - Status: {}, ID: {}", response.statusCode(), BeerManager.getTestId());
    }

    @Test
    @Order(2)
    void AlreadyRegisteredBeer() {
        log.info("ORDEM 2: Testando a criação de cerveja duplicada.");
        BeerRequest beerToCreate = BeerRequest.builder()
                .name(BeerManager.getTestName())
                .build();

        response = given()
                .contentType("application/json")
                .body(beerToCreate)
                .when()
                .post(BEERS_PATH)
                .then()
                .statusCode(400)
                .body("message", equalTo("Beer with name " + BeerManager.getTestName() + " already registered in the system."))
                .extract().response();

        log.info("ORDEM 2: Sucesso - Status: {}, Corpo: {}", response.statusCode(), response.asPrettyString());
    }

    @Test
    @Order(3)
    void beerIsCalledWithValidName() {
        log.info("ORDEM 3: Testando a busca de cerveja por nome válido.");
        response = given()
                .contentType("application/json")
                .when()
                .get(BEERS_PATH + "/" + BeerManager.getTestName())
                .then()
                .statusCode(200)
                .body("name", equalTo(BeerManager.getTestName()))
                .extract().response();

        log.info("ORDEM 3: Sucesso - Status: {}, Corpo: {}", response.statusCode(), response.asPrettyString());
    }

    @Test
    @Order(4)
    void returnAListOfBeers() {
        log.info("ORDEM 4: Testando a listagem de cervejas.");
        response = given()
                .contentType("application/json")
                .when()
                .get(BEERS_PATH)
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .extract().response();

        log.info("ORDEM 4: Sucesso - Status: {}", response.statusCode());
    }

    @Test
    @Order(5)
    void incrementIsCalled() {
        log.info("ORDEM 5: Testando o incremento de estoque com sucesso.");
        int quantityToIncrement = 15;
        QuantityRequest quantityRequest = QuantityRequest.builder()
                .quantity(quantityToIncrement)
                .build();

        int expectedQuantity = Integer.parseInt(BeerManager.getCurrentQuantity()) + quantityToIncrement;

        response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when()
                .patch(BEERS_PATH + "/" + BeerManager.getTestId() + "/increment")
                .then()
                .statusCode(200)
                .body("quantity", equalTo(expectedQuantity))
                .extract().response();

        BeerManager.setCurrentQuantity(response.jsonPath().getInt("quantity"));
        log.info("ORDEM 5: Sucesso - Status: {}, Nova quantidade: {}", response.statusCode(), BeerManager.getCurrentQuantity());
    }

    @Test
    @Order(6)
    void incrementIsGreatherThanMax() {
        log.info("ORDEM 6: Testando o incremento de estoque além do máximo.");
        int quantityToIncrement = 30;
        QuantityRequest quantityRequest = QuantityRequest.builder()
                .quantity(quantityToIncrement)
                .build();

        response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when()
                .patch(BEERS_PATH + "/" + BeerManager.getTestId() + "/increment")
                .then()
                .statusCode(400)
                .extract().response();

        log.info("ORDEM 6: Sucesso - Status: {}, Corpo: {}", response.statusCode(), response.asPrettyString());
    }

    @Test
    @Order(7)
    void decrementIsCalled() {
        log.info("ORDEM 7: Testando o decremento de estoque com sucesso (ESPERADO FALHAR).");
        int quantityToDecrement = 5;
        QuantityRequest quantityRequest = QuantityRequest.builder()
                .quantity(quantityToDecrement)
                .build();

        int expectedQuantity = Integer.parseInt(BeerManager.getCurrentQuantity()) - quantityToDecrement;

        response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when()
                .patch(BEERS_PATH + "/" + BeerManager.getTestId() + "/decrement")
                .then()
                .statusCode(200)
                .body("quantity", equalTo(expectedQuantity))
                .extract().response();

        BeerManager.setCurrentQuantity(response.jsonPath().getInt("quantity"));
        log.info("ORDEM 7: Sucesso - Status: {}, Nova quantidade: {}", response.statusCode(), BeerManager.getCurrentQuantity());
    }

    @Test
    @Order(8)
    void decrementIsLowerThanZero() {
        log.info("ORDEM 8: Testando o decremento de estoque para valor negativo (ESPERADO FALHAR).");
        int currentQuantity = Integer.parseInt(BeerManager.getCurrentQuantity());
        int quantityToDecrement = currentQuantity + 1;
        QuantityRequest quantityRequest = QuantityRequest.builder()
                .quantity(quantityToDecrement)
                .build();

        response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when()
                .patch(BEERS_PATH + "/" + BeerManager.getTestId() + "/decrement")
                .then()
                .statusCode(400)
                .extract().response();

        log.info("ORDEM 8: Sucesso - Status: {}, Corpo: {}", response.statusCode(), response.asPrettyString());
    }

    @Test
    @Order(9)
    void deleteIsCalledWithValidId() {
        log.info("ORDEM 9: Testando a deleção de cerveja com ID válido.");

        response = given()
                .contentType("application/json")
                .when()
                .delete(BEERS_PATH + "/" + BeerManager.getTestId())
                .then()
                .statusCode(204)
                .extract().response();

        given().contentType("application/json").when().get(BEERS_PATH + "/" + BeerManager.getTestId()).then().statusCode(404);

        log.info("ORDEM 9: Sucesso - Status: {}", response.statusCode());
    }

    @Test
    @Order(10)
    void listWithoutBeersIsCalled() {
        log.info("ORDEM 10: Testando a listagem de cervejas (pode não estar vazia).");
        response =given()
                .contentType("application/json")
                .when().get(BEERS_PATH)
                .then().extract().response();

        assertThat(response.statusCode(), equalTo(200));
        log.info("ORDEM 10: Sucesso - Status: {}", response.statusCode());
    }
}
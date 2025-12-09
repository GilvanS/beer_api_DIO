package one.digitalinnovation.beerstock.controller;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import one.digitalinnovation.beerstock.modal.BeerRequest;
import one.digitalinnovation.beerstock.modal.QuantityRequest;
import org.apache.commons.math3.analysis.function.Log;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // CRUCIAL: Garante uma única instância para todos os testes
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Habilita o uso do @Order
public class BeerTestController {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String BEERS_PATH = "/api/v1/beers";

    private Response response;
    private String createdBeerName;
    private Long createdBeerId;

    @Test
    @Order(1)
    void beerIsCreated() {
        log.info("quando POST é chamado, então Uma cerveja é criada");
        BeerRequest beerToCreate = BeerRequest.builder()
                .name("Skol") // Usando um nome único para a suíte de testes
                .brand("Ambev")
                .max(100)
                .quantity(10)
                .type("LAGER")
                .build();

        response = given()
                .contentType("application/json")
                .body(beerToCreate)
                .when()
                .post(BEERS_PATH)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(201));
        assertThat(response.jsonPath().getString("name"), equalTo("Skol"));

        // Salva o estado para os próximos testes usarem
        this.createdBeerName = response.jsonPath().getString("name");
        this.createdBeerId = response.jsonPath().getLong("id"); // Usar getLong() evita o ClassCastException

        log.info("ORDEM 1: Criação - \nStatus: {}, ID: {}, \nCorpo: {}", response.statusCode(), this.createdBeerId, response.asPrettyString());
    }

    @Test
    @Order(2)
    void whenBeerIsAlreadyRegisteredThenAnErrorIsReturned() {
        log.info("Quando a cerveja já está registrada, um erro é retornado");
        BeerRequest beerToCreate = BeerRequest.builder()
                .name(this.createdBeerName) // Usa o nome criado no teste anterior
                .brand("Ambev")
                .max(100)
                .quantity(10)
                .type("LAGER")
                .build();

        response = given()
                .contentType("application/json")
                .body(beerToCreate)
                .log().body()
                .when()
                .post(BEERS_PATH)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(400));
        // CORREÇÃO: A mensagem de erro foi ajustada para ser idêntica à da API
        assertThat(response.jsonPath().getString("message"), equalTo("Beer with name " + this.createdBeerName + " already registered in the system."));

        log.info("ORDEM 2: Duplicidade - \nStatus: {}, \nCorpo: {}", response.statusCode(), response.asPrettyString());
    }

    @Test
    @Order(3)
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() {
        log.info("Quando GET é chamado com um nome válido, o status ok é retornado");
        response = given()
                .contentType("application/json")
                .when()
                .get(BEERS_PATH + "/" + this.createdBeerName) // Usa o nome salvo
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("name"), equalTo(this.createdBeerName));

        log.info("ORDEM 3: Busca por Nome Válido - \nStatus: {}, \nCorpo: {}", response.statusCode(), response.asPrettyString());
    }

    @Test
    @Order(4)
    void whenPATCHIsCalledToIncrementThenOKstatusIsReturned() {
        log.info("Quando PATCH é chamado para incrementar, o status OK é retornado");
        QuantityRequest quantityRequest = QuantityRequest.builder().quantity(10).build();
        int expectedQuantity = 20; // 10 (inicial) + 10 (incremento)

        response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .log().body()
                .when()
                .patch(BEERS_PATH + "/" + this.createdBeerId + "/increment") // Usa o ID salvo
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getInt("quantity"), equalTo(expectedQuantity));

        log.info("ORDEM 4: Incremento - \nStatus: {}, \nCorpo: {}", response.statusCode(), response.asPrettyString());
    }

    @Test
    @Order(5)
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() {
        log.info("Quando DELETE é chamado com um ID válido, o status No Content é retornado");
        response = given()
                .contentType("application/json")
                .when()
                .delete(BEERS_PATH + "/" + this.createdBeerId) // Usa o ID salvo
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(204));

        log.info("ORDEM 5: Deleção - \nStatus: {}", response.statusCode());
    }

    @Test
    @Order(6)
    void whenGETIsCalledAfterDeleteThenNotFoundStatusIsReturned() {
        log.info("Quando GET é chamado após a deleção, o status Not Found é retornado");
        response = given()
                .contentType("application/json")
                .when()
                .get(BEERS_PATH + "/" + this.createdBeerName) // Tenta buscar a cerveja que foi deletada
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(404));

        log.info("ORDEM 6: Busca Pós-Deleção - \nStatus: {}, \nCorpo: {}", response.statusCode(), response.asPrettyString());
    }
}
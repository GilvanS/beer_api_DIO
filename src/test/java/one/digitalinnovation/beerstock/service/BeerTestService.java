package one.digitalinnovation.beerstock.service;

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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Garante uma única instância da classe para todos os testes
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Habilita a ordenação com @Order
public class BeerTestService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String BEERS_PATH = "/api/v1/beers";

    // Variáveis de instância para guardar o estado da cerveja durante os testes
    private Long createdBeerId;
    private String createdBeerName;
    private int currentQuantity;
    private int maxCapacity;

    @Test
    @Order(1)
    void whenPOSTIsCalledThenABeerIsCreated() {
        log.info("ORDEM 1: Testando a criação de uma nova cerveja.");
        BeerRequest beerToCreate = BeerRequest.builder()
                .name("Skol")
                .brand("Ambev")
                .max(50)
                .quantity(10)
                .type("LAGER")
                .build();

        Response response = given()
                .contentType("application/json")
                .body(beerToCreate)
                .when().post(BEERS_PATH)
                .then().extract().response();

        // Salva o estado para os próximos testes
        this.createdBeerId = response.jsonPath().getLong("id");
        this.createdBeerName = response.jsonPath().getString("name");
        this.currentQuantity = response.jsonPath().getInt("quantity");
        this.maxCapacity = response.jsonPath().getInt("max");

        log.info("ORDEM 1: Sucesso - Status: {}, ID: {}", response.statusCode(), this.createdBeerId);
    }

    @Test
    @Order(2)
    void whenAlreadyRegisteredBeerInformedThenAnErrorIsReturned() {
        log.info("ORDEM 2: Testando a criação de cerveja duplicada.");
        BeerRequest beerToCreate = BeerRequest.builder().name(this.createdBeerName).build();

        given()
                .contentType("application/json")
                .body(beerToCreate)
                .when()
                .post(BEERS_PATH)
                .then()
                .statusCode(400)
                .body("message", equalTo("Beer with name " + this.createdBeerName + " already registered in the system."));

        log.info("ORDEM 2: Sucesso - Erro de duplicidade recebido como esperado.");
    }

    @Test
    @Order(3)
    void whenGETIsCalledWithValidNameThenReturnABeer() {
        log.info("ORDEM 3: Testando a busca de cerveja por nome válido.");
        given()
                .contentType("application/json")
                .when()
                .get(BEERS_PATH + "/" + this.createdBeerName)
                .then()
                .statusCode(200)
                .body("name", equalTo(this.createdBeerName));

        log.info("ORDEM 3: Sucesso - Cerveja encontrada.");
    }

    @Test
    @Order(4)
    void whenGETListIsCalledThenReturnAListOfBeers() {
        log.info("ORDEM 4: Testando a listagem de cervejas.");
        given()
                .contentType("application/json")
                .when()
                .get(BEERS_PATH)
                .then()
                .statusCode(200)
                .body("$", not(empty()));

        log.info("ORDEM 4: Sucesso - Lista de cervejas retornada.");
    }

    @Test
    @Order(5)
    void whenPATCHIncrementIsCalledThenIncrementBeerStock() {
        log.info("ORDEM 5: Testando o incremento de estoque com sucesso.");
        int quantityToIncrement = 15;
        QuantityRequest quantityRequest = QuantityRequest.builder().quantity(quantityToIncrement).build();
        int expectedQuantity = this.currentQuantity + quantityToIncrement;

        Response response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when()
                .patch(BEERS_PATH + "/" + this.createdBeerId + "/increment")
                .then()
                .statusCode(200)
                .body("quantity", equalTo(expectedQuantity))
                .extract().response();

        this.currentQuantity = response.jsonPath().getInt("quantity"); // Atualiza a quantidade atual
        log.info("ORDEM 5: Sucesso - Nova quantidade: {}", this.currentQuantity);
    }

    @Test
    @Order(6)
    void whenPATCHIncrementIsGreatherThanMaxThenThrowException() {
        log.info("ORDEM 6: Testando o incremento de estoque além do máximo.");
        int quantityToIncrement = this.maxCapacity; // Garante que vai estourar o limite
        QuantityRequest quantityRequest = QuantityRequest.builder().quantity(quantityToIncrement).build();

        given()
                .contentType("application/json")
                .body(quantityRequest)
                .when()
                .patch(BEERS_PATH + "/" + this.createdBeerId + "/increment")
                .then()
                .statusCode(400);

        log.info("ORDEM 6: Sucesso - Erro de estoque excedido recebido como esperado.");
    }

    @Test
    @Order(7)
    void whenPATCHDecrementIsCalledThenDecrementBeerStock() {
        log.info("ORDEM 7: Testando o decremento de estoque com sucesso (ESPERADO FALHAR).");
        int quantityToDecrement = 5;
        QuantityRequest quantityRequest = QuantityRequest.builder().quantity(quantityToDecrement).build();
        int expectedQuantity = this.currentQuantity - quantityToDecrement;

        Response response = given()
                .contentType("application/json")
                .body(quantityRequest)
                .when()
                .patch(BEERS_PATH + "/" + this.createdBeerId + "/decrement")
                .then()
                .statusCode(200)
                .body("quantity", equalTo(expectedQuantity))
                .extract().response();

        this.currentQuantity = response.jsonPath().getInt("quantity");
        log.info("ORDEM 7: Sucesso - Nova quantidade: {}", this.currentQuantity);
    }

    @Test
    @Order(8)
    void whenPATCHDecrementIsLowerThanZeroThenThrowException() {
        log.info("ORDEM 8: Testando o decremento de estoque para valor negativo (ESPERADO FALHAR).");
        int quantityToDecrement = this.currentQuantity + 1; // Garante que o resultado será negativo
        QuantityRequest quantityRequest = QuantityRequest.builder().quantity(quantityToDecrement).build();

        given()
                .contentType("application/json")
                .body(quantityRequest)
                .when()
                .patch(BEERS_PATH + "/" + this.createdBeerId + "/decrement")
                .then()
                .statusCode(400);

        log.info("ORDEM 8: Sucesso - Erro de estoque negativo recebido como esperado.");
    }

    @Test
    @Order(9)
    void whenDELETEIsCalledWithValidIdThenBeerShouldBeDeleted() {
        log.info("ORDEM 9: Testando a deleção de cerveja com ID válido.");
        given()
                .contentType("application/json")
                .when()
                .delete(BEERS_PATH + "/" + this.createdBeerId)
                .then()
                .statusCode(204);

        // Verifica se a cerveja realmente foi deletada
        given().contentType("application/json").when().get(BEERS_PATH + "/" + this.createdBeerId).then().statusCode(404);
        log.info("ORDEM 9: Sucesso - Cerveja deletada e não encontrada após a deleção.");
    }
}
package one.digitalinnovation.beerstock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Beer API", description = "Manages beer stock")
public interface BeerControllerDocs {

        @Operation(summary = "Beer creation operation")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "201", description = "Success beer creation"),
                    @ApiResponse(responseCode = "400", description = "Missing required fields or wrong field range value.")
    })
    BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException;

    @Operation(summary = "Returns beer found by a given name")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Success beer found in the system"),
                    @ApiResponse(responseCode = "404", description = "Beer with given name not found.")
    })
    BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException;

    @Operation(summary = "Returns a list of all beers registered in the system")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "List of all beers registered in the system"),
    })
    List<BeerDTO> listBeers();

    @Operation(summary = "Delete a beer found by a given valid Id")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "204", description = "Success beer deleted in the system"),
                    @ApiResponse(responseCode = "404", description = "Beer with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws BeerNotFoundException;
}

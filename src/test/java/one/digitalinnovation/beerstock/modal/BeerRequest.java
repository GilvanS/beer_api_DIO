package one.digitalinnovation.beerstock.modal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerRequest {

    @Builder.Default
    private String name = "Skol"; // Valor padrão

    @Builder.Default
    private String brand = "Ambev"; // Valor padrão

    @Builder.Default
    private int max = 50; // Valor padrão

    @Builder.Default
    private int quantity = 10; // Valor padrão

    @Builder.Default
    private String type = "LAGER"; // Valor padrão
}
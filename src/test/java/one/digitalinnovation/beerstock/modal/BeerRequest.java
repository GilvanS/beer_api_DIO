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

    private String name;
    private String brand;
    private int max;
    private int quantity;
    private String type;

}

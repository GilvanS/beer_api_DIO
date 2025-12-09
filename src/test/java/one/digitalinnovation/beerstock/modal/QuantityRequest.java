package one.digitalinnovation.beerstock.modal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class QuantityRequest {

    private int quantity;
}

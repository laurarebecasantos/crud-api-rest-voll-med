package med.voll.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressDto(

        @NotBlank(message = "Street must not be blank")
        String street,

        @NotBlank(message = "Neighborhood must not be blank")
        String neighborhood,


        @NotBlank(message = "Zip code must not be blank")
        @Pattern(regexp = "^[0-9]{8}", message = "Zip code must be 8 digits")
        String zipCode,

        @NotBlank(message = "City must not be blank")
        String city,

        @NotBlank(message = "State must not be blank")
        String state,

        String complement,

        String number) {
}

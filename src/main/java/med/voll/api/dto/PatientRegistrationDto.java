package med.voll.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PatientRegistrationDto(

        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Email must not be blank")
        @Email
        String email,

        @NotBlank(message = "Phone must not be blank")
        String phone,

        @NotBlank(message = "CPF must not be blank")
        @Pattern(regexp = "^[0-9]{11}", message = "CPF must be 11 digits")
        String cpf,

        @Valid
        AddressDto address
) {
}

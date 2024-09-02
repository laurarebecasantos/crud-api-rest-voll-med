package med.voll.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import med.voll.api.model.enums.Speciality;

public record DoctorRegistrationDto(

        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Email must not be blank")
        @Email
        String email,

        @NotBlank(message = "Phone must not be blank")
        String phone,

        @NotBlank(message = "CRM must not be blank")
        @Pattern(regexp = "^[0-9]{6}", message = "CRM must be 6 digits")
        String crm,

        @NotNull(message = "Speciality must not be null")
        Speciality speciality,

        @Valid
        AddressDto address) {
}

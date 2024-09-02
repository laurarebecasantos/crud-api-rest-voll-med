package med.voll.api.dto;

import jakarta.validation.constraints.NotNull;

public record DoctorUpdateDto(

        @NotNull
        Long id,

        String name,

        String email,

        String phone,

        AddressDto address
) {

}

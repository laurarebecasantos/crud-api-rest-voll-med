package med.voll.api.dto;

public record DoctorUpdateDto(
        Boolean active,

        String name,

        String email,

        String phone,

        AddressDto address
) {

}

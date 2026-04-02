package med.voll.api.dto;

public record PatientUpdateDto(
        Boolean active,
        String name,
        String email,
        String phone,
        AddressDto address
) {
}

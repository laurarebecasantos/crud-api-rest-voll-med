package med.voll.api.dto;

public record PatientRegistrationDto(String name, String email, String phone, String CPF, AddressDto address) {
}

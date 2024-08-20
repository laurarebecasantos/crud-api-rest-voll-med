package med.voll.api.dto;

public record AddressDto(String street, String neighborhood, String zipCode, String city,
                         String state, String complement, String number) {
}

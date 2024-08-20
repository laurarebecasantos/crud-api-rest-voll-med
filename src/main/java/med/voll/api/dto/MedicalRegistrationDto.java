package med.voll.api.dto;

import med.voll.api.model.enums.Speciality;

public record MedicalRegistrationDto(String name, String email, String crm, Speciality speciality, AddressDto address) {
}

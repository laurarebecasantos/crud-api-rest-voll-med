package med.voll.api.doctor;

import med.voll.api.address.AddressData;

public record MedicalRegistrationData(String name, String email, String crm, Speciality speciality, AddressData address ) {
}

package med.voll.api.controller;

import med.voll.api.address.AddressData;

public record patientRegistrationData(String name, String email, String phone, String CPF, AddressData address) {
}

package med.voll.api.dto;

import med.voll.api.model.Patient;

public record PatientListingDto(Long id, String name, String email, String phone, String cpf, Boolean active) {

    public PatientListingDto(Patient patient) {
        this(patient.getId(), patient.getName(), patient.getEmail(), patient.getPhone(), patient.getCpf(), patient.getActive());
    }
}

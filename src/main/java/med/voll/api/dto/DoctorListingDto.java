package med.voll.api.dto;

import med.voll.api.model.Doctor;
import med.voll.api.model.enums.Speciality;

public record DoctorListingDto(Long id, String name, String phone, String email, String crm, Speciality speciality, Boolean active) {


    public DoctorListingDto(Doctor doctor) {
        this(doctor.getId(), doctor.getName(), doctor.getPhone(), doctor.getEmail(), doctor.getCrm(), doctor.getSpeciality(), doctor.getActive());
    }
}

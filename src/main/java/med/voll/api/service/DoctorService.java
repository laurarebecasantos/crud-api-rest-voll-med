package med.voll.api.service;

import jakarta.transaction.Transactional;
import med.voll.api.dto.DoctorListingDto;
import med.voll.api.dto.DoctorRegistrationDto;
import med.voll.api.dto.DoctorUpdateDto;
import med.voll.api.model.Doctor;
import med.voll.api.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Transactional
    public Doctor  registerDoctor(DoctorRegistrationDto doctorRegistrationDto) {
        Doctor doctor = new Doctor(doctorRegistrationDto);
        return doctorRepository.save(doctor);
    }

    public List<DoctorListingDto> listDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorListingDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Doctor updateDoctor(Long id, DoctorUpdateDto doctorUpdateDto) {
        Doctor doctor = doctorRepository.getReferenceById(id);
        doctor.updateData(doctorUpdateDto);
        return doctorRepository.save(doctor);
    }

    public void deleteDoctors(Long id) {
        Doctor doctor = doctorRepository.getReferenceById(id);
        doctorRepository.delete(doctor);
    }
}

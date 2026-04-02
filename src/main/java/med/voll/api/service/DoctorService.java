package med.voll.api.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import med.voll.api.dto.DoctorListingDto;
import med.voll.api.dto.DoctorRegistrationDto;
import med.voll.api.dto.DoctorUpdateDto;
import med.voll.api.model.Doctor;
import med.voll.api.model.enums.Speciality;
import med.voll.api.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Transactional
    public Doctor registerDoctor(DoctorRegistrationDto doctorRegistrationDto) {
        Doctor doctor = new Doctor(doctorRegistrationDto);
        return doctorRepository.save(doctor);
    }

    public List<DoctorListingDto> listDoctors() {
        return doctorRepository.findAllByActiveTrue()
                .stream()
                .map(DoctorListingDto::new)
                .collect(Collectors.toList());
    }

    public Page<DoctorListingDto> listDoctorsPaginated(Pageable pageable) {
        return doctorRepository.findAllByActiveTrue(pageable)
                .map(DoctorListingDto::new);
    }

    public Page<DoctorListingDto> listDoctorsFiltered(String name, Speciality speciality, Pageable pageable) {
        return doctorRepository.findByFilters(name, speciality, pageable)
                .map(DoctorListingDto::new);
    }

    @Transactional
    public Doctor updateDoctor(Long id, DoctorUpdateDto doctorUpdateDto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + id));
        doctor.updateData(doctorUpdateDto);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctors(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + id));
        doctorRepository.delete(doctor);
    }

    @Transactional
    public Doctor statusDoctors(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + id));
        doctor.statusInactiveData();
        return doctorRepository.save(doctor);
    }
}

package med.voll.api.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import med.voll.api.dto.PatientListingDto;
import med.voll.api.dto.PatientRegistrationDto;
import med.voll.api.dto.PatientUpdateDto;
import med.voll.api.model.Patient;
import med.voll.api.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Transactional
    public Patient registerPatient(PatientRegistrationDto dto) {
        Patient patient = new Patient(dto);
        return patientRepository.save(patient);
    }

    public PatientListingDto findById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
        return new PatientListingDto(patient);
    }

    public Page<PatientListingDto> listPatients(Pageable pageable) {
        return patientRepository.findAllByActiveTrue(pageable)
                .map(PatientListingDto::new);
    }

    @Transactional
    public Patient updatePatient(Long id, PatientUpdateDto dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
        patient.updateData(dto);
        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);
    }

    @Transactional
    public void inactivatePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
        patient.inactivate();
        patientRepository.save(patient);
    }
}

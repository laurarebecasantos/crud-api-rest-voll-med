package med.voll.api.repository;

import med.voll.api.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Collection<Patient> findAllByActiveTrue();
    Page<Patient> findAllByActiveTrue(Pageable pageable);
}

package med.voll.api.repository;

import med.voll.api.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Collection<Doctor> findAllByActiveTrue();
}

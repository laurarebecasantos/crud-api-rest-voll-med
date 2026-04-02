package med.voll.api.repository;

import med.voll.api.model.Doctor;
import med.voll.api.model.enums.Speciality;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Collection<Doctor> findAllByActiveTrue();

    Page<Doctor> findAllByActiveTrue(Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.active = true " +
           "AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:speciality IS NULL OR d.speciality = :speciality)")
    Page<Doctor> findByFilters(@Param("name") String name,
                               @Param("speciality") Speciality speciality,
                               Pageable pageable);
}

package med.voll.api.repository;

import med.voll.api.model.Appointment;
import med.voll.api.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDateTime date, AppointmentStatus status);

    boolean existsByPatientIdAndAppointmentDateBetweenAndStatusNot(Long patientId, LocalDateTime start, LocalDateTime end, AppointmentStatus status);

    Page<Appointment> findAllByStatus(AppointmentStatus status, Pageable pageable);

    @Query(value = "SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient WHERE a.doctor.id = :doctorId",
           countQuery = "SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId")
    Page<Appointment> findAllByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);

    @Query(value = "SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient WHERE a.patient.id = :patientId",
           countQuery = "SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId")
    Page<Appointment> findAllByPatientId(@Param("patientId") Long patientId, Pageable pageable);

    @Query(value = "SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient",
           countQuery = "SELECT COUNT(a) FROM Appointment a")
    Page<Appointment> findAllWithDoctorAndPatient(Pageable pageable);
}

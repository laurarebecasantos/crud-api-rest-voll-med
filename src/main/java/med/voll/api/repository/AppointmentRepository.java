package med.voll.api.repository;

import med.voll.api.model.Appointment;
import med.voll.api.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDateTime date, AppointmentStatus status);

    boolean existsByPatientIdAndAppointmentDateBetweenAndStatusNot(Long patientId, LocalDateTime start, LocalDateTime end, AppointmentStatus status);

    Page<Appointment> findAllByStatus(AppointmentStatus status, Pageable pageable);

    Page<Appointment> findAllByDoctorId(Long doctorId, Pageable pageable);

    Page<Appointment> findAllByPatientId(Long patientId, Pageable pageable);
}

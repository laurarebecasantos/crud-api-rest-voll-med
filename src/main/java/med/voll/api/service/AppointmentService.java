package med.voll.api.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import med.voll.api.dto.AppointmentCancelDto;
import med.voll.api.dto.AppointmentListingDto;
import med.voll.api.dto.AppointmentScheduleDto;
import med.voll.api.model.Appointment;
import med.voll.api.model.Doctor;
import med.voll.api.model.Patient;
import med.voll.api.model.enums.AppointmentStatus;
import med.voll.api.repository.AppointmentRepository;
import med.voll.api.repository.DoctorRepository;
import med.voll.api.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Transactional
    public Appointment schedule(AppointmentScheduleDto dto) {
        Doctor doctor = doctorRepository.findById(dto.doctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + dto.doctorId()));

        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + dto.patientId()));

        if (!doctor.getActive()) {
            throw new IllegalArgumentException("Cannot schedule appointment with inactive doctor");
        }

        if (!patient.getActive()) {
            throw new IllegalArgumentException("Cannot schedule appointment with inactive patient");
        }

        boolean doctorBusy = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(
                dto.doctorId(), dto.appointmentDate(), AppointmentStatus.CANCELLED);
        if (doctorBusy) {
            throw new IllegalArgumentException("Doctor already has an appointment at this time");
        }

        LocalDateTime startOfDay = dto.appointmentDate().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        boolean patientHasAppointment = appointmentRepository.existsByPatientIdAndAppointmentDateBetweenAndStatusNot(
                dto.patientId(), startOfDay, endOfDay, AppointmentStatus.CANCELLED);
        if (patientHasAppointment) {
            throw new IllegalArgumentException("Patient already has an appointment on this day");
        }

        Appointment appointment = new Appointment(doctor, patient, dto.appointmentDate());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void cancel(Long id, AppointmentCancelDto dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with id: " + id));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Appointment is already cancelled");
        }

        appointment.cancel(dto.reason());
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void complete(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with id: " + id));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new IllegalArgumentException("Only scheduled appointments can be completed");
        }

        appointment.complete();
        appointmentRepository.save(appointment);
    }

    public Page<AppointmentListingDto> listAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(AppointmentListingDto::new);
    }

    public Page<AppointmentListingDto> listByDoctor(Long doctorId, Pageable pageable) {
        return appointmentRepository.findAllByDoctorId(doctorId, pageable).map(AppointmentListingDto::new);
    }

    public Page<AppointmentListingDto> listByPatient(Long patientId, Pageable pageable) {
        return appointmentRepository.findAllByPatientId(patientId, pageable).map(AppointmentListingDto::new);
    }
}

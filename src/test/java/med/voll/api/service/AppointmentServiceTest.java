package med.voll.api.service;

import jakarta.persistence.EntityNotFoundException;
import med.voll.api.dto.AddressDto;
import med.voll.api.dto.AppointmentCancelDto;
import med.voll.api.dto.AppointmentScheduleDto;
import med.voll.api.dto.DoctorRegistrationDto;
import med.voll.api.model.Appointment;
import med.voll.api.model.Doctor;
import med.voll.api.model.Patient;
import med.voll.api.model.enums.AppointmentStatus;
import med.voll.api.model.enums.Speciality;
import med.voll.api.repository.AppointmentRepository;
import med.voll.api.repository.DoctorRepository;
import med.voll.api.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Doctor createDoctor() {
        var addressDto = new AddressDto("Rua A", "Centro", "12345678", "Recife", "PE", null, "100");
        var dto = new DoctorRegistrationDto("Dr. Test", "doc@test.com", "123", "123456", Speciality.CARDIOLOGY, addressDto, true);
        return new Doctor(dto);
    }

    private Patient createPatient() {
        var addressDto = new AddressDto("Rua B", "Centro", "12345678", "Recife", "PE", null, "200");
        var dto = new med.voll.api.dto.PatientRegistrationDto("Patient", "pat@test.com", "123", "12345678901", addressDto);
        return new Patient(dto);
    }

    @Test
    @DisplayName("Should schedule an appointment successfully")
    void scheduleAppointment() {
        var doctor = createDoctor();
        var patient = createPatient();
        var futureDate = LocalDateTime.now().plusDays(1);
        var dto = new AppointmentScheduleDto(1L, 1L, futureDate);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(eq(1L), any(), any())).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndAppointmentDateBetweenAndStatusNot(eq(1L), any(), any(), any())).thenReturn(false);
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = appointmentService.schedule(dto);

        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        verify(appointmentRepository).save(any());
    }

    @Test
    @DisplayName("Should throw when doctor not found")
    void scheduleDoctorNotFound() {
        var dto = new AppointmentScheduleDto(99L, 1L, LocalDateTime.now().plusDays(1));
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.schedule(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw when doctor is inactive")
    void scheduleInactiveDoctor() {
        var doctor = createDoctor();
        doctor.statusInactiveData();
        var patient = createPatient();
        var dto = new AppointmentScheduleDto(1L, 1L, LocalDateTime.now().plusDays(1));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        assertThatThrownBy(() -> appointmentService.schedule(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inactive doctor");
    }

    @Test
    @DisplayName("Should throw when doctor already has appointment at same time")
    void scheduleDoctorBusy() {
        var doctor = createDoctor();
        var patient = createPatient();
        var futureDate = LocalDateTime.now().plusDays(1);
        var dto = new AppointmentScheduleDto(1L, 1L, futureDate);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(eq(1L), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.schedule(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already has an appointment");
    }

    @Test
    @DisplayName("Should throw when patient already has appointment on same day")
    void schedulePatientBusy() {
        var doctor = createDoctor();
        var patient = createPatient();
        var futureDate = LocalDateTime.now().plusDays(1);
        var dto = new AppointmentScheduleDto(1L, 1L, futureDate);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(eq(1L), any(), any())).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndAppointmentDateBetweenAndStatusNot(eq(1L), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.schedule(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already has an appointment on this day");
    }

    @Test
    @DisplayName("Should cancel an appointment")
    void cancelAppointment() {
        var appointment = new Appointment(createDoctor(), createPatient(), LocalDateTime.now().plusDays(1));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        appointmentService.cancel(1L, new AppointmentCancelDto("Patient request"));

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(appointment.getCancelReason()).isEqualTo("Patient request");
    }

    @Test
    @DisplayName("Should throw when cancelling already cancelled appointment")
    void cancelAlreadyCancelled() {
        var appointment = new Appointment(createDoctor(), createPatient(), LocalDateTime.now().plusDays(1));
        appointment.cancel("First cancel");
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancel(1L, new AppointmentCancelDto("Second cancel")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    @DisplayName("Should complete an appointment")
    void completeAppointment() {
        var appointment = new Appointment(createDoctor(), createPatient(), LocalDateTime.now().plusDays(1));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        appointmentService.complete(1L);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }
}

package med.voll.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentScheduleDto(

        @NotNull(message = "Doctor ID is required")
        Long doctorId,

        @NotNull(message = "Patient ID is required")
        Long patientId,

        @NotNull(message = "Appointment date is required")
        @Future(message = "Appointment date must be in the future")
        LocalDateTime appointmentDate
) {
}

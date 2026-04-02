package med.voll.api.dto;

import med.voll.api.model.Appointment;
import med.voll.api.model.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentListingDto(
        Long id,
        String doctorName,
        String doctorCrm,
        String patientName,
        String patientCpf,
        LocalDateTime appointmentDate,
        AppointmentStatus status,
        String cancelReason
) {
    public AppointmentListingDto(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getDoctor().getName(),
                appointment.getDoctor().getCrm(),
                appointment.getPatient().getName(),
                appointment.getPatient().getCpf(),
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getCancelReason()
        );
    }
}

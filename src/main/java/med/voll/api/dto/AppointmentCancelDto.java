package med.voll.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AppointmentCancelDto(

        @NotBlank(message = "Cancel reason is required")
        String reason
) {
}

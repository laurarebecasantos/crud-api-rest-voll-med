package med.voll.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import med.voll.api.model.enums.UserRole;

public record RegisterDto(
        @NotBlank String login,
        @NotBlank String password,
        @NotNull UserRole role
) {
}

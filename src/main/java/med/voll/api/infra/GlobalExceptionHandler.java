package med.voll.api.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageDto> handleBusinessRule(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessageDto(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleValidationError(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors().stream()
                .map(ValidationErrorDto::new)
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }

    public record ErrorMessageDto(String message) {}

    public record ValidationErrorDto(String field, String message) {
        public ValidationErrorDto(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}

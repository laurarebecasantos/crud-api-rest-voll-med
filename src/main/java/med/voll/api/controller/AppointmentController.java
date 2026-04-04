package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.dto.AppointmentCancelDto;
import med.voll.api.dto.AppointmentListingDto;
import med.voll.api.dto.AppointmentScheduleDto;
import med.voll.api.model.Appointment;
import med.voll.api.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping

    public ResponseEntity<AppointmentListingDto> schedule(@Valid @RequestBody AppointmentScheduleDto data,
                                                          UriComponentsBuilder uriBuilder) {
        Appointment saved = appointmentService.schedule(data);
        var uri = uriBuilder.path("/appointments/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(uri).body(new AppointmentListingDto(saved));
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentListingDto>> list(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @PageableDefault(size = 10, sort = "appointmentDate") Pageable pageable) {
        if (doctorId != null) {
            return ResponseEntity.ok(appointmentService.listByDoctor(doctorId, pageable));
        }
        if (patientId != null) {
            return ResponseEntity.ok(appointmentService.listByPatient(patientId, pageable));
        }
        return ResponseEntity.ok(appointmentService.listAll(pageable));
    }

    @PatchMapping("/{id}/cancel")

    public ResponseEntity<Void> cancel(@PathVariable Long id, @Valid @RequestBody AppointmentCancelDto dto) {
        appointmentService.cancel(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")

    public ResponseEntity<Void> complete(@PathVariable Long id) {
        appointmentService.complete(id);
        return ResponseEntity.noContent().build();
    }
}

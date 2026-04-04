package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.dto.PatientListingDto;
import med.voll.api.dto.PatientRegistrationDto;
import med.voll.api.dto.PatientUpdateDto;
import med.voll.api.model.Patient;
import med.voll.api.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping

    public ResponseEntity<PatientListingDto> register(@Valid @RequestBody PatientRegistrationDto data,
                                                      UriComponentsBuilder uriBuilder) {
        Patient saved = patientService.registerPatient(data);
        var uri = uriBuilder.path("/patients/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(uri).body(new PatientListingDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientListingDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PatientListingDto>> list(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(patientService.listPatients(pageable));
    }

    @PutMapping("/{id}")

    public ResponseEntity<PatientListingDto> update(@PathVariable Long id, @Valid @RequestBody PatientUpdateDto dto) {
        Patient updated = patientService.updatePatient(id, dto);
        return ResponseEntity.ok(new PatientListingDto(updated));
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")

    public ResponseEntity<Void> inactivate(@PathVariable Long id) {
        patientService.inactivatePatient(id);
        return ResponseEntity.noContent().build();
    }
}

package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.dto.DoctorListingDto;
import med.voll.api.dto.DoctorRegistrationDto;
import med.voll.api.dto.DoctorUpdateDto;
import med.voll.api.model.Doctor;
import med.voll.api.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    @Transactional
    public ResponseEntity<DoctorListingDto> register(@Valid @RequestBody DoctorRegistrationDto data,
                                                     UriComponentsBuilder uriBuilder) {
        Doctor dataSaved = doctorService.registerDoctor(data);
        var uri = uriBuilder.path("/doctors/{id}").buildAndExpand(dataSaved.getId()).toUri();
        return ResponseEntity.created(uri).body(new DoctorListingDto(dataSaved));
    }

    @GetMapping
    public ResponseEntity<List<DoctorListingDto>> list() {
        return ResponseEntity.ok(doctorService.listDoctors());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DoctorListingDto> update(@PathVariable Long id, @Valid @RequestBody DoctorUpdateDto updateDto) {
        Doctor updateDataSaved = doctorService.updateDoctor(id, updateDto);
        return ResponseEntity.ok(new DoctorListingDto(updateDataSaved));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.deleteDoctors(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<Void> status(@PathVariable Long id) {
        doctorService.statusDoctors(id);
        return ResponseEntity.noContent().build();
    }
}

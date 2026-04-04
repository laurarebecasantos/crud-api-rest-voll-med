package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.dto.DoctorListingDto;
import med.voll.api.dto.DoctorRegistrationDto;
import med.voll.api.dto.DoctorUpdateDto;
import med.voll.api.model.Doctor;
import med.voll.api.model.enums.Speciality;
import med.voll.api.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping

    public ResponseEntity<DoctorListingDto> register(@Valid @RequestBody DoctorRegistrationDto data,
                                                     UriComponentsBuilder uriBuilder) {
        Doctor dataSaved = doctorService.registerDoctor(data);
        var uri = uriBuilder.path("/doctors/{id}").buildAndExpand(dataSaved.getId()).toUri();
        return ResponseEntity.created(uri).body(new DoctorListingDto(dataSaved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorListingDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DoctorListingDto>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Speciality speciality,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(doctorService.listDoctorsFiltered(name, speciality, pageable));
    }

    @PutMapping("/{id}")

    public ResponseEntity<DoctorListingDto> update(@PathVariable Long id, @Valid @RequestBody DoctorUpdateDto updateDto) {
        Doctor updateDataSaved = doctorService.updateDoctor(id, updateDto);
        return ResponseEntity.ok(new DoctorListingDto(updateDataSaved));
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.deleteDoctors(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")

    public ResponseEntity<Void> status(@PathVariable Long id) {
        doctorService.statusDoctors(id);
        return ResponseEntity.noContent().build();
    }
}

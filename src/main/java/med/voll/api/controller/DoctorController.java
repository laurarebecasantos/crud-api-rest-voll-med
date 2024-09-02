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

import java.util.List;

@RestController
@RequestMapping("doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    @Transactional
    public ResponseEntity<Doctor> register(@Valid @RequestBody DoctorRegistrationDto data){
        Doctor dataSaved = doctorService.registerDoctor(data);
        return ResponseEntity.status(200).body(dataSaved);
    }

    @GetMapping
    public List<DoctorListingDto> list() {
        return doctorService.listDoctors();
    }

    @PutMapping("/{id}")
    @Transactional
    public void update(@PathVariable Long id, @Valid @RequestBody DoctorUpdateDto updateDto) {
        Doctor updateDataSaved = doctorService.updateDoctor(id, updateDto);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.deleteDoctors(id);
        return ResponseEntity.noContent().build();
    }
}

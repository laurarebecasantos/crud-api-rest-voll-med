package med.voll.api.service;

import jakarta.persistence.EntityNotFoundException;
import med.voll.api.dto.AddressDto;
import med.voll.api.dto.DoctorRegistrationDto;
import med.voll.api.dto.DoctorUpdateDto;
import med.voll.api.model.Doctor;
import med.voll.api.model.enums.Speciality;
import med.voll.api.repository.DoctorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private AddressDto createAddressDto() {
        return new AddressDto("Rua A", "Centro", "12345678", "Recife", "PE", null, "100");
    }

    private DoctorRegistrationDto createRegistrationDto() {
        return new DoctorRegistrationDto(
                "Dr. Test", "doctor@test.com", "81999999999",
                "123456", Speciality.CARDIOLOGY, createAddressDto(), true
        );
    }

    private Doctor createDoctor() {
        return new Doctor(createRegistrationDto());
    }

    @Test
    @DisplayName("Should register a new doctor")
    void registerDoctor() {
        var dto = createRegistrationDto();
        var doctor = new Doctor(dto);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        var result = doctorService.registerDoctor(dto);

        assertThat(result.getName()).isEqualTo("Dr. Test");
        assertThat(result.getEmail()).isEqualTo("doctor@test.com");
        assertThat(result.getActive()).isTrue();
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Should list only active doctors")
    void listDoctors() {
        var doctor = createDoctor();
        when(doctorRepository.findAllByActiveTrue()).thenReturn(List.of(doctor));

        var result = doctorService.listDoctors();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Dr. Test");
        verify(doctorRepository).findAllByActiveTrue();
    }

    @Test
    @DisplayName("Should return empty list when no active doctors")
    void listDoctorsEmpty() {
        when(doctorRepository.findAllByActiveTrue()).thenReturn(List.of());

        var result = doctorService.listDoctors();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update an existing doctor")
    void updateDoctor() {
        var doctor = createDoctor();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        var updateDto = new DoctorUpdateDto(null, "Dr. Updated", null, null, null);
        var result = doctorService.updateDoctor(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Dr. Updated");
        verify(doctorRepository).findById(1L);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent doctor")
    void updateNonExistentDoctor() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        var updateDto = new DoctorUpdateDto(null, "Dr. Ghost", null, null, null);

        assertThatThrownBy(() -> doctorService.updateDoctor(99L, updateDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Should delete an existing doctor")
    void deleteDoctor() {
        var doctor = createDoctor();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        doctorService.deleteDoctors(1L);

        verify(doctorRepository).delete(doctor);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent doctor")
    void deleteNonExistentDoctor() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.deleteDoctors(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Should inactivate a doctor")
    void statusDoctors() {
        var doctor = createDoctor();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        var result = doctorService.statusDoctors(1L);

        assertThat(result.getActive()).isFalse();
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when inactivating non-existent doctor")
    void statusNonExistentDoctor() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.statusDoctors(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}

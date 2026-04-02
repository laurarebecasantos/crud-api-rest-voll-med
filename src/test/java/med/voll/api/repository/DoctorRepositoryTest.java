package med.voll.api.repository;

import med.voll.api.dto.AddressDto;
import med.voll.api.dto.DoctorRegistrationDto;
import med.voll.api.model.Doctor;
import med.voll.api.model.enums.Speciality;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    private Doctor createAndSaveDoctor(String crm, boolean active) {
        var addressDto = new AddressDto("Rua A", "Centro", "12345678", "Recife", "PE", null, "100");
        var dto = new DoctorRegistrationDto("Dr. Test", "doctor" + crm + "@test.com", "81999999999",
                crm, Speciality.CARDIOLOGY, addressDto, active);
        return doctorRepository.save(new Doctor(dto));
    }

    @Test
    @DisplayName("Should return only active doctors")
    void findAllByActiveTrue() {
        createAndSaveDoctor("111111", true);
        createAndSaveDoctor("222222", true);
        var inactiveDoctor = createAndSaveDoctor("333333", true);
        inactiveDoctor.statusInactiveData();
        doctorRepository.save(inactiveDoctor);

        var activeDoctors = doctorRepository.findAllByActiveTrue();

        assertThat(activeDoctors).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty collection when no active doctors")
    void findAllByActiveTrueEmpty() {
        var doctor = createAndSaveDoctor("444444", true);
        doctor.statusInactiveData();
        doctorRepository.save(doctor);

        var activeDoctors = doctorRepository.findAllByActiveTrue();

        assertThat(activeDoctors).isEmpty();
    }

    @Test
    @DisplayName("Should save and retrieve a doctor by id")
    void saveAndFindById() {
        var doctor = createAndSaveDoctor("555555", true);

        var found = doctorRepository.findById(doctor.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCrm()).isEqualTo("555555");
        assertThat(found.get().getActive()).isTrue();
    }

    @Test
    @DisplayName("Should delete a doctor")
    void deleteDoctor() {
        var doctor = createAndSaveDoctor("666666", true);
        doctorRepository.delete(doctor);

        var found = doctorRepository.findById(doctor.getId());

        assertThat(found).isEmpty();
    }
}

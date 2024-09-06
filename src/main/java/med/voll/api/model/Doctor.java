package med.voll.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import med.voll.api.dto.DoctorRegistrationDto;
import med.voll.api.dto.DoctorUpdateDto;
import med.voll.api.model.enums.Speciality;

@Table(name = "doctors")
@Entity(name = "Doctor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String crm;

    @Enumerated(EnumType.STRING)
    private Speciality speciality;

    @Embedded
    private Address address;

    private Boolean active;

    public Doctor(DoctorRegistrationDto data) {
        this.active = true;
        this.name = data.name();
        this.email = data.email();
        this.phone = data.phone();
        this.crm = data.crm();
        this.speciality = data.speciality();
        this.address = new Address(data.address());
    }

    public void updateData(DoctorUpdateDto doctorUpdateDto) {
        if (doctorUpdateDto.name() != null) {
            this.name = doctorUpdateDto.name();
        }
        if (doctorUpdateDto.phone() != null) {
            this.phone = doctorUpdateDto.phone();
        }
        if (doctorUpdateDto.email() != null) {
            this.email = doctorUpdateDto.email();
        }
        if (doctorUpdateDto.address() != null) {
            this.address.updateAddressDetails(doctorUpdateDto.address());
        }
        if (doctorUpdateDto.active() != true) {
            this.active = doctorUpdateDto.active();
        }
    }

    public  void statusInactiveData() {
        this.active = false;
    }
}

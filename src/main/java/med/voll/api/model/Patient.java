package med.voll.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import med.voll.api.dto.PatientRegistrationDto;
import med.voll.api.dto.PatientUpdateDto;

@Table(name = "patients")
@Entity(name = "Patient")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String cpf;

    @Embedded
    private Address address;

    private Boolean active;

    public Patient(PatientRegistrationDto data) {
        this.active = true;
        this.name = data.name();
        this.email = data.email();
        this.phone = data.phone();
        this.cpf = data.cpf();
        this.address = new Address(data.address());
    }

    public void updateData(PatientUpdateDto dto) {
        if (dto.name() != null) {
            this.name = dto.name();
        }
        if (dto.phone() != null) {
            this.phone = dto.phone();
        }
        if (dto.email() != null) {
            this.email = dto.email();
        }
        if (dto.address() != null) {
            this.address.updateAddressDetails(dto.address());
        }
        if (dto.active() != null) {
            this.active = dto.active();
        }
    }

    public void inactivate() {
        this.active = false;
    }
}

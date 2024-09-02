package med.voll.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import med.voll.api.dto.AddressDto;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String street;
    private String neighborhood;

    @Column(name = "zip_code")
    private String zipCode;
    private String city;
    private String state;
    private String complement;
    private String number;

    public Address(AddressDto data) {
        this.street = data.street();
        this.neighborhood = data.neighborhood();
        this.zipCode = data.zipCode();
        this.city = data.city();
        this.state = data.state();
        this.complement = data.complement();
        this.number = data.number();
    }

    public void updateAddressDetails(AddressDto data){
        if (data.street() != null) {
            this.street = data.street();
        }
        if (data.neighborhood() != null) {
            this.neighborhood = data.neighborhood();
        }
        if (data.zipCode() != null) {
            this.zipCode = data.zipCode();
        }
        if (data.city() != null) {
            this.city = data.city();
        }
        if (data.state() != null) {
            this.state = data.state();
        }
        if (data.complement() != null) {
            this.complement = data.complement();
        }
        if (data.number() != null) {
            this.number = data.number();
        }

    }
}

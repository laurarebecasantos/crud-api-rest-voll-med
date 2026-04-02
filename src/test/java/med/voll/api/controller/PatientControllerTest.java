package med.voll.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import med.voll.api.dto.AddressDto;
import med.voll.api.dto.PatientRegistrationDto;
import med.voll.api.dto.PatientUpdateDto;
import med.voll.api.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();
    }

    private AddressDto createAddressDto() {
        return new AddressDto("Rua B", "Boa Vista", "50000000", "Recife", "PE", null, "200");
    }

    private PatientRegistrationDto createPatientDto(String cpf) {
        return new PatientRegistrationDto("Patient Test", "patient" + cpf + "@test.com", "81988888888", cpf, createAddressDto());
    }

    @Test
    @DisplayName("Should register a patient and return 201")
    @WithMockUser(roles = "RECEPTIONIST")
    void registerPatient() throws Exception {
        var dto = createPatientDto("12345678901");

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Patient Test"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    @DisplayName("Should return 400 for invalid CPF")
    @WithMockUser(roles = "RECEPTIONIST")
    void registerPatientInvalidCpf() throws Exception {
        var dto = new PatientRegistrationDto("Test", "t@t.com", "123", "123", createAddressDto());

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should list patients with pagination")
    @WithMockUser(roles = "RECEPTIONIST")
    void listPatients() throws Exception {
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPatientDto("98765432100"))));

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("Should update a patient")
    @WithMockUser(roles = "RECEPTIONIST")
    void updatePatient() throws Exception {
        var result = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPatientDto("11122233344"))))
                .andReturn();

        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        var updateDto = new PatientUpdateDto(null, "Updated Name", null, null, null);
        mockMvc.perform(put("/patients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("Should deny delete for RECEPTIONIST")
    @WithMockUser(roles = "RECEPTIONIST")
    void deletePatientForbidden() throws Exception {
        mockMvc.perform(delete("/patients/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow delete for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deletePatientAdmin() throws Exception {
        var result = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPatientDto("55566677788"))))
                .andReturn();

        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/patients/" + id))
                .andExpect(status().isNoContent());
    }
}

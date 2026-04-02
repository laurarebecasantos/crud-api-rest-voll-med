package med.voll.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import med.voll.api.dto.AddressDto;
import med.voll.api.dto.DoctorRegistrationDto;
import med.voll.api.dto.DoctorUpdateDto;
import med.voll.api.model.enums.Speciality;
import med.voll.api.repository.DoctorRepository;
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
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @BeforeEach
    void setUp() {
        doctorRepository.deleteAll();
    }

    private AddressDto createAddressDto() {
        return new AddressDto("Rua A", "Centro", "12345678", "Recife", "PE", null, "100");
    }

    private DoctorRegistrationDto createDoctorDto(String crm) {
        return new DoctorRegistrationDto(
                "Dr. Test", "doctor" + crm + "@test.com", "81999999999",
                crm, Speciality.CARDIOLOGY, createAddressDto(), true
        );
    }

    @Test
    @DisplayName("Should register a doctor and return 201")
    @WithMockUser(roles = "ADMIN")
    void registerDoctor() throws Exception {
        var dto = createDoctorDto("123456");

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Dr. Test"))
                .andExpect(jsonPath("$.crm").value("123456"))
                .andExpect(jsonPath("$.speciality").value("CARDIOLOGY"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should return 400 when registering doctor with invalid data")
    @WithMockUser(roles = "ADMIN")
    void registerDoctorWithInvalidData() throws Exception {
        var invalidDto = new DoctorRegistrationDto("", "", "", "", null, null, null);

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should list active doctors with pagination")
    @WithMockUser(roles = "RECEPTIONIST")
    void listActiveDoctors() throws Exception {
        var dto = createDoctorDto("654321");
        mockMvc.perform(post("/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Dr. Test"));
    }

    @Test
    @DisplayName("Should filter doctors by speciality")
    @WithMockUser(roles = "RECEPTIONIST")
    void filterDoctorsBySpeciality() throws Exception {
        mockMvc.perform(post("/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDoctorDto("111111"))));

        mockMvc.perform(get("/doctors").param("speciality", "DERMATOLOGY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        mockMvc.perform(get("/doctors").param("speciality", "CARDIOLOGY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("Should update a doctor and return 200")
    @WithMockUser(roles = "ADMIN")
    void updateDoctor() throws Exception {
        var dto = createDoctorDto("222222");
        var result = mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        var updateDto = new DoctorUpdateDto(null, "Dr. Updated", null, null, null);

        mockMvc.perform(put("/doctors/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Updated"));
    }

    @Test
    @DisplayName("Should delete a doctor and return 204 (ADMIN only)")
    @WithMockUser(roles = "ADMIN")
    void deleteDoctor() throws Exception {
        var dto = createDoctorDto("333333");
        var result = mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/doctors/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should deny delete for RECEPTIONIST role")
    @WithMockUser(roles = "RECEPTIONIST")
    void deleteDoctorForbidden() throws Exception {
        mockMvc.perform(delete("/doctors/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent doctor")
    @WithMockUser(roles = "ADMIN")
    void updateNonExistentDoctor() throws Exception {
        var updateDto = new DoctorUpdateDto(null, "Dr. Ghost", null, null, null);

        mockMvc.perform(put("/doctors/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 403 when unauthenticated")
    void unauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should inactivate a doctor via PATCH")
    @WithMockUser(roles = "ADMIN")
    void inactivateDoctor() throws Exception {
        var dto = createDoctorDto("444444");
        var result = mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/doctors/" + id + "/status"))
                .andExpect(status().isNoContent());
    }
}

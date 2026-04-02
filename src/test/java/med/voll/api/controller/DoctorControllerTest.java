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
                "Dr. Test", "doctor@test.com", "81999999999",
                crm, Speciality.CARDIOLOGY, createAddressDto(), true
        );
    }

    @Test
    @DisplayName("Should register a doctor and return 201")
    void registerDoctor() throws Exception {
        var dto = createDoctorDto("123456");

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Dr. Test"))
                .andExpect(jsonPath("$.email").value("doctor@test.com"))
                .andExpect(jsonPath("$.crm").value("123456"))
                .andExpect(jsonPath("$.speciality").value("CARDIOLOGY"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should return 400 when registering doctor with invalid data")
    void registerDoctorWithInvalidData() throws Exception {
        var invalidDto = new DoctorRegistrationDto("", "", "", "", null, null, null);

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should list active doctors")
    void listActiveDoctors() throws Exception {
        var dto = createDoctorDto("654321");
        mockMvc.perform(post("/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Dr. Test"));
    }

    @Test
    @DisplayName("Should update a doctor and return 200")
    void updateDoctor() throws Exception {
        var dto = createDoctorDto("111111");
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
    @DisplayName("Should delete a doctor and return 204")
    void deleteDoctor() throws Exception {
        var dto = createDoctorDto("222222");
        var result = mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/doctors/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should inactivate a doctor via PATCH and return 204")
    void inactivateDoctor() throws Exception {
        var dto = createDoctorDto("333333");
        var result = mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/doctors/" + id + "/status"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent doctor")
    void updateNonExistentDoctor() throws Exception {
        var updateDto = new DoctorUpdateDto(null, "Dr. Ghost", null, null, null);

        mockMvc.perform(put("/doctors/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent doctor")
    void deleteNonExistentDoctor() throws Exception {
        mockMvc.perform(delete("/doctors/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update doctor active status to false via PUT")
    void updateDoctorActiveStatus() throws Exception {
        var dto = createDoctorDto("444444");
        var result = mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        var updateDto = new DoctorUpdateDto(false, null, null, null, null);

        mockMvc.perform(put("/doctors/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}

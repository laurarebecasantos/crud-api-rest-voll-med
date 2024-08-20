package med.voll.api.controller;

import med.voll.api.dto.PatientRegistrationDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("patient")
public class PatientController {

    @PostMapping
    public void register(@RequestBody PatientRegistrationDto data) {
        System.out.println("Reiceved data:" + data);
    }
}

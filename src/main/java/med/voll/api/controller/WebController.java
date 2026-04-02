package med.voll.api.controller;

import med.voll.api.dto.DoctorListingDto;
import med.voll.api.dto.PatientListingDto;
import med.voll.api.model.enums.Speciality;
import med.voll.api.service.AppointmentService;
import med.voll.api.service.DoctorService;
import med.voll.api.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/web/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("doctorCount", doctorService.listDoctors().size());
        return "dashboard";
    }

    @GetMapping("/web/doctors")
    public String doctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Speciality speciality,
            @PageableDefault(size = 10, sort = "name") Pageable pageable,
            Model model) {
        var doctors = doctorService.listDoctorsFiltered(name, speciality, pageable);
        model.addAttribute("doctors", doctors);
        model.addAttribute("specialities", Speciality.values());
        model.addAttribute("nameFilter", name);
        model.addAttribute("specialityFilter", speciality);
        return "doctors";
    }

    @GetMapping("/web/doctors/new")
    public String newDoctor(Model model) {
        model.addAttribute("specialities", Speciality.values());
        return "doctor-form";
    }

    @GetMapping("/web/patients")
    public String patients(@PageableDefault(size = 10, sort = "name") Pageable pageable, Model model) {
        var patients = patientService.listPatients(pageable);
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/web/patients/new")
    public String newPatient() {
        return "patient-form";
    }

    @GetMapping("/web/appointments")
    public String appointments(@PageableDefault(size = 10, sort = "appointmentDate") Pageable pageable, Model model) {
        var appointments = appointmentService.listAll(pageable);
        model.addAttribute("appointments", appointments);
        return "appointments";
    }

    @GetMapping("/web/appointments/new")
    public String newAppointment(Model model) {
        model.addAttribute("doctors", doctorService.listDoctors());
        return "appointment-form";
    }
}

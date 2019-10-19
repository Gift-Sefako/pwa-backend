package com.disco.backend.patient;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PatientController {


    @Autowired
    PatientRepository patientRepository;

    private StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();


    @GetMapping("/patients")
    public List<Patient> getAllPatients() {

        List<Patient> users = patientRepository.findAll();
        for (Patient user : users) {
            user.setPassword("");
        }
        return users;
    }

    @GetMapping("/getPatient")
    public Optional<Patient> getPatient(@RequestParam("id") Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        System.out.println(id);
        return patient;
    }

    @ResponseBody
    @PostMapping("/addPatient")
    public ResponseEntity createPatient(@RequestBody Patient patient) {
        if (patientRepository.findById(patient.getId()).isPresent()) {
            return  ResponseEntity.ok("Patient already exists");
        }

        String encrypted = encrypt(patient.getPassword());
        patient.setPassword(encrypted);
        patientRepository.save(patient);

        List<Patient> patients = getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @ResponseBody
    @PutMapping("/updatePatient")
    public ResponseEntity updatePatient(@RequestBody Patient patient) {
        if (patientRepository.findById(patient.getId()).isPresent()) {
            patientRepository.save(patient);
            return ResponseEntity.ok("Successfully updated patient: ".concat(patient.getName()));
        }

        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity login(@RequestParam("email") String email, @RequestParam("password") String password) {
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isPresent()) {
            if (encryptor.checkPassword(password, patient.get().getPassword())) {
                Patient loggedIn = patient.get();
                loggedIn.setPassword("");
                return ResponseEntity.ok(loggedIn);
            }
        }
        return ResponseEntity.status(401).body("Unable to login, incorrect username/password combination");
    }

    @DeleteMapping("/deletePatient")
    public ResponseEntity deletePatient(@RequestParam("id") Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            patientRepository.delete(patient.get());
            return ResponseEntity.ok(getAllPatients());
        }

        return ResponseEntity.status(401).body("Cannot delete: Unable to find patient");
    }

    private String encrypt(String password) {
        return encryptor.encryptPassword(password);
    }
}

package com.disco.backend.doctor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DoctorController {

    @ResponseBody
    @PostMapping("/requestPatientConsent")
    public ResponseEntity requestConsent(@RequestBody RequestConsentDTO requestConsentDTO) {
        return ResponseEntity.ok(requestConsentDTO);
    }
}

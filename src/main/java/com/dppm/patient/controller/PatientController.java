package com.dppm.patient.controller;

import com.dppm.patient.dto.PatientRequests.ActiveStatusRequest;
import com.dppm.patient.dto.PatientRequests.CreatePatientRequest;
import com.dppm.patient.dto.PatientRequests.NotificationPreferencesRequest;
import com.dppm.patient.dto.PatientRequests.TimezoneRequest;
import com.dppm.patient.dto.PatientRequests.UpdatePatientRequest;
import com.dppm.patient.dto.PatientRequests.WhatsappNumberRequest;
import com.dppm.patient.dto.PatientResponse;
import com.dppm.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) { this.patientService = patientService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PatientResponse> register(@Valid @RequestBody CreatePatientRequest request) {
        return patientService.register(request).map(PatientResponse::from);
    }

    @GetMapping
    public Flux<PatientResponse> findAll() {
        return patientService.findAll().map(PatientResponse::from);
    }
    @GetMapping("/{id}")
    public Mono<PatientResponse> getById(@PathVariable String id) {
        return patientService.getById(id).map(PatientResponse::from);
    }

    @PutMapping("/{id}")
    public Mono<PatientResponse> updateProfile(@PathVariable String id, @Valid @RequestBody UpdatePatientRequest request) {
        return patientService.updateProfile(id, request).map(PatientResponse::from);
    }

    @PatchMapping("/{id}/whatsapp-number")
    public Mono<PatientResponse> updateWhatsappNumber(@PathVariable String id, @Valid @RequestBody WhatsappNumberRequest request) {
        return patientService.updateWhatsappNumber(id, request).map(PatientResponse::from);
    }

    @PatchMapping("/{id}/timezone")
    public Mono<PatientResponse> updateTimezone(@PathVariable String id, @Valid @RequestBody TimezoneRequest request) {
        return patientService.updateTimezone(id, request).map(PatientResponse::from);
    }

    @PutMapping("/{id}/notification-preferences")
    public Mono<PatientResponse> updateNotificationPreferences(@PathVariable String id,
            @Valid @RequestBody NotificationPreferencesRequest request) {
        return patientService.updateNotificationPreferences(id, request).map(PatientResponse::from);
    }

    @PatchMapping("/{id}/status")
    public Mono<PatientResponse> updateStatus(@PathVariable String id, @Valid @RequestBody ActiveStatusRequest request) {
        return patientService.updateActiveStatus(id, request).map(PatientResponse::from);
    }
}

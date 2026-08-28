package com.dppm.patient.service;

import com.dppm.patient.PatientMsApplication;
import com.dppm.patient.dto.PatientRequests.ActiveStatusRequest;
import com.dppm.patient.dto.PatientRequests.CreatePatientRequest;
import com.dppm.patient.dto.PatientRequests.NotificationPreferencesRequest;
import com.dppm.patient.dto.PatientRequests.TimezoneRequest;
import com.dppm.patient.dto.PatientRequests.UpdatePatientRequest;
import com.dppm.patient.dto.PatientRequests.WhatsappNumberRequest;
import com.dppm.patient.entity.Patient;
import io.netty.util.AsyncMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PatientService {
    Mono<Patient> register(CreatePatientRequest request);
    Mono<Patient> getById(String id);
    Mono<Patient> updateProfile(String id, UpdatePatientRequest request);
    Mono<Patient> updateWhatsappNumber(String id, WhatsappNumberRequest request);
    Mono<Patient> updateTimezone(String id, TimezoneRequest request);
    Mono<Patient> updateNotificationPreferences(String id, NotificationPreferencesRequest request);
    Mono<Patient> updateActiveStatus(String id, ActiveStatusRequest request);

    Flux<Patient> findAll();
}

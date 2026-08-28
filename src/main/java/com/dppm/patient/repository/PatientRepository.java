package com.dppm.patient.repository;

import com.dppm.patient.entity.Patient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PatientRepository extends ReactiveCrudRepository<Patient, String> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByEmailAndIdNot(String email, String id);
    Mono<Boolean> existsByWhatsappNumber(String whatsappNumber);
    Mono<Boolean> existsByWhatsappNumberAndIdNot(String whatsappNumber, String id);
}

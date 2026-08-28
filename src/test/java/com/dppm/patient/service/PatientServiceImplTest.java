package com.dppm.patient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dppm.patient.dto.PatientRequests.ActiveStatusRequest;
import com.dppm.patient.dto.PatientRequests.CreatePatientRequest;
import com.dppm.patient.entity.Patient;
import com.dppm.patient.exceptions.DuplicatePatientException;
import com.dppm.patient.exceptions.PatientNotFoundException;
import com.dppm.patient.repository.PatientRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    private PatientServiceImpl patientService;

    @BeforeEach
    void setUp() {
        patientService = new PatientServiceImpl(patientRepository);
    }

    @Test
    void register_savesNormalisedPatientWhenEmailAndWhatsappAreAvailable() {
        CreatePatientRequest request = new CreatePatientRequest(" Ada ", " Lovelace ", " ADA@EXAMPLE.COM ",
                LocalDate.of(1990, 1, 1), " Female ", "+919876543210", "Asia/Kolkata", null, false, null);
        when(patientRepository.existsByEmail("ada@example.com")).thenReturn(Mono.just(false));
        when(patientRepository.existsByWhatsappNumber("+919876543210")).thenReturn(Mono.just(false));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(patientService.register(request))
                .assertNext(patient -> {
                    assertEquals("Ada", patient.getFirstName());
                    assertEquals("Lovelace", patient.getLastName());
                    assertEquals("ada@example.com", patient.getEmail());
                    assertEquals("Asia/Kolkata", patient.getTimezone());
                    assertTrue(patient.isWhatsappNotificationsEnabled());
                    assertFalse(patient.isEmailNotificationsEnabled());
                    assertFalse(patient.isSmsNotificationsEnabled());
                    assertTrue(patient.isActive());
                })
                .verifyComplete();

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void register_returnsConflictWhenEmailAlreadyExists() {
        CreatePatientRequest request = new CreatePatientRequest("Ada", "Lovelace", "ada@example.com",
                null, null, null, null, null, null, null);
        when(patientRepository.existsByEmail("ada@example.com")).thenReturn(Mono.just(true));

        DuplicatePatientException exception = assertThrows(DuplicatePatientException.class,
                () -> patientService.register(request).block());

        assertEquals("A patient with this email already exists", exception.getMessage());

        verify(patientRepository).existsByEmail("ada@example.com");
        verify(patientRepository, never()).existsByWhatsappNumber(any());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void getById_returnsNotFoundWhenRepositoryIsEmpty() {
        when(patientRepository.findById("99")).thenReturn(Mono.empty());

        StepVerifier.create(patientService.getById("99"))
                .expectError(PatientNotFoundException.class)
                .verify();
    }

    @Test
    void findAll_returnsNotFoundWhenRepositoryIsEmpty() {
        when(patientRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(patientService.findAll())
                .expectError(PatientNotFoundException.class)
                .verify();
    }

    @Test
    void updateActiveStatus_changesStatusAndSavesPatient() {
        Patient patient = patient("42", true);
        when(patientRepository.findById("42")).thenReturn(Mono.just(patient));
        when(patientRepository.save(patient)).thenReturn(Mono.just(patient));

        StepVerifier.create(patientService.updateActiveStatus("42", new ActiveStatusRequest(false)))
                .assertNext(updated -> assertFalse(updated.isActive()))
                .verifyComplete();

        verify(patientRepository).save(patient);
    }

    private Patient patient(String id, boolean active) {
        Patient patient = new Patient();
        patient.setId(Long.valueOf(id));
        patient.setActive(active);
        return patient;
    }
}

package com.dppm.patient.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dppm.patient.dto.PatientRequests.CreatePatientRequest;
import com.dppm.patient.entity.Patient;
import com.dppm.patient.service.PatientService;
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
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    private PatientController patientController;

    @BeforeEach
    void setUp() {
        patientController = new PatientController(patientService);
    }

    @Test
    void register_delegatesToServiceAndReturnsPatientResponse() {
        CreatePatientRequest request = new CreatePatientRequest("Ada", "Lovelace", "ada@example.com",
                LocalDate.of(1990, 1, 1), null, null, null, null, null, null);
        Patient patient = patient(1L, "Ada", "Lovelace");
        when(patientService.register(request)).thenReturn(Mono.just(patient));

        StepVerifier.create(patientController.register(request))
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals("Ada", response.firstName());
                    assertEquals("Lovelace", response.lastName());
                })
                .verifyComplete();

        verify(patientService).register(request);
    }

    @Test
    void findAll_mapsEveryPatientToResponse() {
        when(patientService.findAll()).thenReturn(Flux.just(patient(1L, "Ada", "Lovelace"), patient(2L, "Grace", "Hopper")));

        StepVerifier.create(patientController.findAll())
                .expectNextMatches(response -> response.id().equals(1L) && response.firstName().equals("Ada"))
                .expectNextMatches(response -> response.id().equals(2L) && response.firstName().equals("Grace"))
                .verifyComplete();

        verify(patientService).findAll();
    }

    @Test
    void getById_delegatesToService() {
        Patient patient = patient(42L, "Ada", "Lovelace");
        when(patientService.getById("42")).thenReturn(Mono.just(patient));

        StepVerifier.create(patientController.getById("42"))
                .assertNext(response -> assertEquals("Ada", response.firstName()))
                .verifyComplete();

        verify(patientService).getById("42");
    }

    private Patient patient(Long id, String firstName, String lastName) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setEmail(firstName.toLowerCase() + "@example.com");
        patient.setActive(true);
        return patient;
    }
}

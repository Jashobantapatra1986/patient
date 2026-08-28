package com.dppm.patient.service;

import com.dppm.patient.dto.PatientRequests.ActiveStatusRequest;
import com.dppm.patient.dto.PatientRequests.CreatePatientRequest;
import com.dppm.patient.dto.PatientRequests.NotificationPreferencesRequest;
import com.dppm.patient.dto.PatientRequests.TimezoneRequest;
import com.dppm.patient.dto.PatientRequests.UpdatePatientRequest;
import com.dppm.patient.dto.PatientRequests.WhatsappNumberRequest;
import com.dppm.patient.entity.Patient;
import com.dppm.patient.exceptions.DuplicatePatientException;
import com.dppm.patient.exceptions.PatientNotFoundException;
import com.dppm.patient.repository.PatientRepository;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.dppm.patient.constants.PatientConstants.DEFAULT_TIMEZONE;
import static com.dppm.patient.constants.PatientConstants.WHATSAPP_NO;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) { this.patientRepository = patientRepository; }

    @Override
    public Mono<Patient> register(CreatePatientRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String email = normalizedEmail(request.email());
        String whatsappNumber = normalizePhone(request.whatsappNumber());
        String timezone = validatedTimezone(request.timezone());
        Patient patient = new Patient();
        patient.setFirstName(request.firstName().trim());
        patient.setLastName(request.lastName().trim());
        patient.setEmail(email);
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(trimToNull(request.gender()));
        patient.setWhatsappNumber(whatsappNumber);
        patient.setTimezone(timezone);
        patient.setWhatsappNotificationsEnabled(boolOrDefault(request.whatsappNotificationsEnabled(), true));
        patient.setEmailNotificationsEnabled(boolOrDefault(request.emailNotificationsEnabled(), true));
        patient.setSmsNotificationsEnabled(boolOrDefault(request.smsNotificationsEnabled(), false));
        patient.setActive(true);
        patient.setCreatedDate(now);
        patient.setUpdatedDate(now);

        return ensureEmailAvailable(email, null).then(ensureWhatsappAvailable(whatsappNumber, null))
                .then(Mono.defer(() -> patientRepository.save(patient)));
    }

    @Override
    public Mono<Patient> getById(String id) {
        return patientRepository.findById(id).switchIfEmpty(Mono.error(new PatientNotFoundException(id)));
    }

    @Override
    public Mono<Patient> updateProfile(String id, UpdatePatientRequest request) {
        String email = normalizedEmail(request.email());
        return getById(id).flatMap(patient -> ensureEmailAvailable(email, id).thenReturn(patient)).flatMap(patient -> {
            patient.updateProfile(request.firstName().trim(), request.lastName().trim(), email,
                    request.dateOfBirth(), trimToNull(request.gender()), request.whatsappNumber());
            return patientRepository.save(patient);
        });
    }

    @Override
    public Mono<Patient> updateWhatsappNumber(String id, WhatsappNumberRequest request) {
        String phone = normalizePhone(request.whatsappNumber());
        return getById(id).flatMap(patient -> ensureWhatsappAvailable(phone, id).thenReturn(patient)).flatMap(patient -> {
            patient.setWhatsappNumber(phone);
            return patientRepository.save(patient);
        });
    }

    @Override
    public Mono<Patient> updateTimezone(String id, TimezoneRequest request) {
        String timezone = validatedTimezone(request.timezone());
        return getById(id).flatMap(patient -> {
            patient.setTimezone(timezone);
            return patientRepository.save(patient);
        });
    }

    @Override
    public Mono<Patient> updateNotificationPreferences(String id, NotificationPreferencesRequest request) {
        return getById(id).flatMap(patient -> {
            patient.setWhatsappNotificationsEnabled(request.whatsappEnabled());
            patient.setEmailNotificationsEnabled(request.emailEnabled());
            patient.setSmsNotificationsEnabled(request.smsEnabled());
            return patientRepository.save(patient);
        });
    }

    @Override
    public Mono<Patient> updateActiveStatus(String id, ActiveStatusRequest request) {
        return getById(id).flatMap(patient -> {
            patient.setActive(request.active());
            return patientRepository.save(patient);
        });
    }

    @Override
    public Flux<Patient> findAll() {
        return patientRepository.findAll()
                .switchIfEmpty(Flux.error(new PatientNotFoundException()));
    }

    private Mono<Void> ensureEmailAvailable(String email, String currentPatientId) {
        Mono<Boolean> exists = currentPatientId == null ? patientRepository.existsByEmail(email) : patientRepository.existsByEmailAndIdNot(email, currentPatientId);
        return exists.flatMap(found -> found ? Mono.<Void>error(new DuplicatePatientException("email")) : Mono.empty());
    }

    private Mono<Void> ensureWhatsappAvailable(String phone, String currentPatientId) {
        if (phone == null) return Mono.empty();
        Mono<Boolean> exists = currentPatientId == null ? patientRepository.existsByWhatsappNumber(phone) : patientRepository.existsByWhatsappNumberAndIdNot(phone, currentPatientId);
        return exists.flatMap(found -> found ? Mono.<Void>error(new DuplicatePatientException(WHATSAPP_NO)) : Mono.empty());
    }

    private String validatedTimezone(String value) {
        String timezone = trimToNull(value);
        if (timezone == null) return DEFAULT_TIMEZONE;
        try { return ZoneId.of(timezone).getId(); }
        catch (DateTimeException ex) { throw new IllegalArgumentException("timezone must be a valid IANA timezone, for example Asia/Kolkata"); }
    }

    private String normalizedEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }
    private String normalizePhone(String phone) { return trimToNull(phone); }
    private String trimToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private boolean boolOrDefault(Boolean value, boolean defaultValue) { return value == null ? defaultValue : value; }
}

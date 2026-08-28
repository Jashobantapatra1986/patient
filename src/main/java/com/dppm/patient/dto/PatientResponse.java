package com.dppm.patient.dto;

import com.dppm.patient.entity.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponse(Long id, String firstName, String lastName, String email, LocalDate dateOfBirth,
                              String gender, String whatsappNumber, String timezone,
                              boolean whatsappNotificationsEnabled, boolean emailNotificationsEnabled,
                              boolean smsNotificationsEnabled, boolean active, LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient.getId(), patient.getFirstName(), patient.getLastName(), patient.getEmail(),
                patient.getDateOfBirth(), patient.getGender(), patient.getWhatsappNumber(), patient.getTimezone(),
                patient.isWhatsappNotificationsEnabled(), patient.isEmailNotificationsEnabled(),
                patient.isSmsNotificationsEnabled(), patient.isActive(), patient.getCreatedDate(), patient.getUpdatedDate());
    }
}

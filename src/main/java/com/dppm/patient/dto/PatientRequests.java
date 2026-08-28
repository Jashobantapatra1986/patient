package com.dppm.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public final class PatientRequests {
    private PatientRequests() { }

    public record CreatePatientRequest(
            @NotBlank String firstName, @NotBlank String lastName, @NotBlank @Email String email,
            @Past LocalDate dateOfBirth, String gender,
            @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "must be a valid international phone number") String whatsappNumber,
            String timezone, Boolean whatsappNotificationsEnabled, Boolean emailNotificationsEnabled,
            Boolean smsNotificationsEnabled) { }

    public record UpdatePatientRequest(
            @NotBlank String firstName, @NotBlank String lastName, @NotBlank @Email String email, @NotBlank String whatsappNumber,
            @Past LocalDate dateOfBirth, String gender) { }

    public record WhatsappNumberRequest(
            @NotBlank @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "must be a valid international phone number") String whatsappNumber) { }

    public record TimezoneRequest(@NotBlank String timezone) { }

    public record NotificationPreferencesRequest(@NotNull Boolean whatsappEnabled, @NotNull Boolean emailEnabled,
                                                 @NotNull Boolean smsEnabled) { }

    public record ActiveStatusRequest(@NotNull Boolean active) { }
}

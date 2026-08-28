package com.dppm.patient.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("patients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient extends BaseEntity {

    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("email")
    private String email;
    @Column("date_of_birth")
    private LocalDate dateOfBirth;
    @Column("gender")
    private String gender;
    @Column("whatsapp_number")
    private String whatsappNumber;
    @Column("timezone")
    private String timezone;
    @Column("whatsapp_notifications_enabled")
    private boolean whatsappNotificationsEnabled;
    @Column("email_notifications_enabled")
    private boolean emailNotificationsEnabled;
    @Column("sms_notifications_enabled")
    private boolean smsNotificationsEnabled;
    private boolean active;

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getWhatsappNumber() { return whatsappNumber; }
    public String getTimezone() { return timezone; }
    public boolean isWhatsappNotificationsEnabled() { return whatsappNotificationsEnabled; }
    public boolean isEmailNotificationsEnabled() { return emailNotificationsEnabled; }
    public boolean isSmsNotificationsEnabled() { return smsNotificationsEnabled; }
    public boolean isActive() { return active; }

    public void updateProfile(String firstName, String lastName, String email, LocalDate dateOfBirth, String gender, String whatsappNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.whatsappNumber = whatsappNumber; // Keep the existing whatsappNumber
    }

}

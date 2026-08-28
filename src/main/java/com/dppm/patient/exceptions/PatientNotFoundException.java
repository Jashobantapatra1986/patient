package com.dppm.patient.exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException() {
        super("No patients were found");
    }

    public PatientNotFoundException(String id) {
        super("Patient with id '" + id + "' was not found");
    }
}

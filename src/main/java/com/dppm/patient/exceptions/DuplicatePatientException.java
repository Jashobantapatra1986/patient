package com.dppm.patient.exceptions;

public class DuplicatePatientException extends RuntimeException {
    public DuplicatePatientException(String field) {
        super("A patient with this " + field + " already exists");
    }
}

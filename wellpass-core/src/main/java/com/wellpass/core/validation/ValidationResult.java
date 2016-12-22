package com.wellpass.core.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationResult {
    public List<String> errorMessages;
    public boolean isValid;

    public ValidationResult() {
        isValid = true;
    }

    public void addErrorMessage(String errorMessage) {
        if (errorMessages == null) {
            errorMessages = new ArrayList<>();
        }
        errorMessages.add(errorMessage);
    }

    public boolean hasErrorMessages() {
        return errorMessages != null;
    }

    public String getErrorMessages() {
        return errorMessages == null ? null : errorMessages.stream().collect(Collectors.joining(", "));
    }
}

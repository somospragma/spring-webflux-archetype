package com.scheme.reactive.scheme_reactive.domain.request;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

public class FindUserRequest {

    @Length(max = 20, message = "Number identification max 20")
    @NotNull(message = "Identification number is required.")
    private String identificationNumber;

    @Length(max = 2, message = "Type identification max 2")
    @NotNull(message = "Identification type is required.")
    private String identificationType;

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    @Override
    public String toString() {
        return "FindUserRequest{" + "identificationNumber='" + identificationNumber + '\'' + ", identificationType='" + identificationType
                + '\'' + '}';
    }
}
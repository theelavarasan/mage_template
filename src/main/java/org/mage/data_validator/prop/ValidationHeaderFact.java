package org.mage.data_validator.prop;

import java.util.List;

public class ValidationHeaderFact {
    private List<String> headers;

    private String validationMessage;

    private List<String> missingHeaders;

    private List<String> additionalHeaders;

    public ValidationHeaderFact(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getMissingHeaders() {
        return missingHeaders;
    }

    public void setMissingHeaders(List<String> missingHeaders) {
        this.missingHeaders = missingHeaders;
    }

    public List<String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public void setAdditionalHeaders(List<String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
    }
}

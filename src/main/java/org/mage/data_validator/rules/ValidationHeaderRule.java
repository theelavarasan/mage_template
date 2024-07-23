package org.mage.data_validator.rules;

import org.mage.data_validator.prop.ValidationHeaderFact;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class ValidationHeaderRule {
    private List<String> requiredHeaders;
    private List<String> missingHeaders;
    private List<String> additionalHeaders;

    public ValidationHeaderRule(List<String> requiredHeaders) {
        this.requiredHeaders = requiredHeaders.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public List<String> getRequiredHeaders() {
        return requiredHeaders;
    }

    public List<String> getMissingHeaders(List<String> headers) {
        List<String> lowerCaseHeaders = headers.stream()
                .map(header -> header.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        List<String> missingHeaders = requiredHeaders.stream()
                .filter(requiredHeader -> !lowerCaseHeaders.contains(requiredHeader))
                .collect(Collectors.toList());

        return missingHeaders;
    }

    public List<String> getAdditionalHeaders(List<String> headers) {
        List<String> lowerCaseHeaders = headers.stream()
                .map(header -> header.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        List<String> additionalHeaders = lowerCaseHeaders.stream()
                .filter(header -> !requiredHeaders.contains(header))
                .collect(Collectors.toList());

        return additionalHeaders;
    }

}

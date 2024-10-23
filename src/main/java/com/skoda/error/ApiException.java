package com.skoda.error;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ApiException extends RuntimeException {
    String message;
}

package com.skoda.error;

import lombok.Value;

@Value
public class ApiException extends RuntimeException {
    String message;
}

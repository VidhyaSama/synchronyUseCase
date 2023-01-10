package com.synchrony.userapp.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Custom Exception class.
 * 
 * @author seneca
 */

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RecordNotFoundException extends Exception {

    private String message;
}

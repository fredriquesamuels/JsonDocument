package org.tect.platform.document.errors;

/**
 * Exception to indicate the usage of class type that the PI does not support.
 */
public class UnsupportedTypeError extends RuntimeException {

    public UnsupportedTypeError(Class<?> aClass) {
        super(aClass.getName());
    }
}
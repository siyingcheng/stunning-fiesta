package com.simon.stunningfiesta.system.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String objectName, Object id) {
        super(String.format("Could not find %s with Id %s :(", objectName, id));
    }
}

package org.tect.platform.document;

public interface QueryFieldName {
    String getName();
    boolean isReservedField();
    boolean isPresent();
}

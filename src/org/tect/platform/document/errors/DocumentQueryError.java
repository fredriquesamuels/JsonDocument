package org.tect.platform.document.errors;

import org.tect.platform.document.QueryCondition;

public final class DocumentQueryError extends RuntimeException {
    public DocumentQueryError(QueryCondition condition) {
        super(String.format("Query format error."));
    }
}
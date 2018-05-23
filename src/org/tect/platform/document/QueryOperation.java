package org.tect.platform.document;

public interface QueryOperation {
    String generateString(QueryCondition condition, QueryContext context);
    String getName();
}

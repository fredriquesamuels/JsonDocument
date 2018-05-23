package org.tect.platform.document;

import org.tect.platform.document.errors.DocumentQueryError;

abstract class BaseQueryOperation implements QueryOperation {

    @Override
    public final String generateString(QueryCondition condition, QueryContext context) {
        String generate = generate(condition, context);
        if(generate==null) {
            throw new DocumentQueryError(condition);
        }
        return generate;
    }

    protected final String getFieldName(QueryCondition c) {
        return c.getFieldName().getName();
    }

    protected abstract String generate(QueryCondition condition, QueryContext context);
}
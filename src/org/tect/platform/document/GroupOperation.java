package org.tect.platform.document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GroupOperation implements QueryOperation {
    AND(new AndOperation()),
    OR(new OrOperation());

    private final QueryOperation q;

    GroupOperation(QueryOperation q) {
        this.q = q;
    }

    @Override
    public String generateString(QueryCondition condition, QueryContext context) {
        return q.generateString(condition, context);
    }

    @Override
    public String getName() {
        return this.name();
    }


    private static class AndOperation implements QueryOperation {

        @Override
        public String getName() {
            return GroupOperation.AND.name();
        }

        @Override
        public String generateString(QueryCondition condition, QueryContext context) {
            QueryCondition[] values = (QueryCondition[]) condition.getValue();
            if(values.length==0) return "";
            List<String> qsList = Arrays.stream(values).map(v -> v.generateString(context)).collect(Collectors.toList());
            return String.format("(%s)", String.join(" ) AND (", qsList ));
        }
    }

    private static class OrOperation implements QueryOperation {

        @Override
        public String getName() {
            return GroupOperation.OR.name();
        }

        @Override
        public String generateString(QueryCondition condition, QueryContext context) {
            QueryCondition[] values = (QueryCondition[]) condition.getValue();
            if(values.length==0) return "";
            List<String> qsList = Arrays.stream(values).map(v -> v.generateString(context)).collect(Collectors.toList());
            return String.format("(%s)", String.join(" ) OR (", qsList ));
        }
    }

}
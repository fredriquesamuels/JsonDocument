package org.tect.platform.document;

import java.util.function.Supplier;

public enum CompareOperation implements QueryOperation {
    EQUALS(new EqualsOperation()),
    LESS_THAN(new LessThanOperation()),
    GREATER_THAN(new GreaterThanOperation());

    private final QueryOperation q;

    CompareOperation(QueryOperation q) {
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

    private static class EqualsOperation extends BaseQueryOperation {

        @Override
        public String generate(QueryCondition condition, QueryContext context) {

            switch (condition.getType()) {
                case TEXT:
                    return createFieldHbl(condition, context, "text", AttributeType.TEXT);
                case NUMBER:
                    return createFieldHbl(condition, context, "number", AttributeType.NUMBER);
                case DECIMAL:
                    return createFieldHbl(condition, context, "decimal", AttributeType.DECIMAL);
                case DATE:
                    return createFieldHbl(condition, context, "date", AttributeType.DATE);
                case BOOLEAN:
                    return bool(condition, context);
                case REFERENCE:
                    return reference(condition, context);
            }
            return null;
        }

        @Override
        public String getName() {
            return CompareOperation.EQUALS.name();
        }

        private String reference(QueryCondition condition, QueryContext context) {
            return new FieldHblQueryBuilder(condition, context, null, AttributeType.REFERENCE, context.generateAttributeName())
                    .setValueConditionSupplier(() -> {
                        DocumentReference value = (DocumentReference) condition.getValue();

                        String idVar = context.generateVar();
                        context.setParam(idVar, value.getId());

                        String typeNameVar = context.generateVar();
                        context.setParam(typeNameVar, value.getType());

                        String attributeName = context.generateAttributeName();
                        return String.format("( %1$s.number = :%2$s AND %1$s.text = :%3$s )", attributeName, idVar, typeNameVar);
                    })
                    .build();
        }

        private String bool(QueryCondition condition, QueryContext context) {
            String attributeName = context.generateAttributeName();
            return new FieldHblQueryBuilder(condition, context, null, AttributeType.BOOLEAN, attributeName)
                    .setValueConditionSupplier(() -> {
                        Boolean value = (Boolean) condition.getValue();

                        String var = context.generateVar();
                        context.setParam(var, value);

                        return String.format("( %1$s.bool = :%2$s)", attributeName, var);
                    })
                    .build();
        }

        private String createFieldHbl(QueryCondition condition, QueryContext context, String column, AttributeType columnType) {
            String attributeName = context.generateAttributeName();
            Supplier<String> valueConditionSupplier = () -> {
                String valueVar = context.generateVar();
                context.setParam(valueVar, condition.getValue());
                return String.format("%1$s.%2$s = :%3$s", attributeName, column, valueVar);
            };

            return new FieldHblQueryBuilder(condition, context, column, columnType, attributeName)
                    .setValueConditionSupplier(valueConditionSupplier)
                    .build();
        }
    }


    private static class GreaterThanOperation extends BaseQueryOperation {

        @Override
        public String generate(QueryCondition condition, QueryContext context) {

            switch (condition.getType()) {
                case TEXT:
                    return createFieldHbl(condition, context, "text", AttributeType.TEXT, context.generateAttributeName());
                case NUMBER:
                    return createFieldHbl(condition, context, "number", AttributeType.NUMBER, context.generateAttributeName());
                case DECIMAL:
                    return createFieldHbl(condition, context, "decimal", AttributeType.DECIMAL, context.generateAttributeName());
                case DATE:
                    return createFieldHbl(condition, context, "date", AttributeType.DATE, context.generateAttributeName());
            }
            return null;
        }

        @Override
        public String getName() {
            return CompareOperation.GREATER_THAN.name();
        }

        private String createFieldHbl(QueryCondition condition, QueryContext context, String column, AttributeType columnType, String attributeName) {
            Supplier<String> valueConditionSupplier = () -> {
                String valueVar = context.generateVar();
                context.setParam(valueVar, condition.getValue());
                return String.format("%1$s.%2$s > :%3$s", attributeName, column, valueVar);
            };

            return new FieldHblQueryBuilder(condition, context, column, columnType, attributeName)
                    .setValueConditionSupplier(valueConditionSupplier)
                    .build();
        }
    }

    private static class LessThanOperation extends BaseQueryOperation {

        @Override
        public String generate(QueryCondition condition, QueryContext context) {

            switch (condition.getType()) {
                case TEXT:
                    return createFieldHbl(condition, context, "text", AttributeType.TEXT, context.generateAttributeName());
                case NUMBER:
                    return createFieldHbl(condition, context, "number", AttributeType.NUMBER, context.generateAttributeName());
                case DECIMAL:
                    return createFieldHbl(condition, context, "decimal", AttributeType.DECIMAL, context.generateAttributeName());
                case DATE:
                    return createFieldHbl(condition, context, "date", AttributeType.DATE, context.generateAttributeName());
            }
            return null;
        }

        @Override
        public String getName() {
            return CompareOperation.LESS_THAN.name();
        }

        private String createFieldHbl(QueryCondition condition, QueryContext context, String column, AttributeType columnType, String attributeName) {
            Supplier<String> valueConditionSupplier = () -> {
                String valueVar = context.generateVar();
                context.setParam(valueVar, condition.getValue());
                return String.format("%1$s.%2$s < :%3$s", attributeName, column, valueVar);
            };

            return new FieldHblQueryBuilder(condition, context, column, columnType, attributeName)
                    .setValueConditionSupplier(valueConditionSupplier)
                    .build();
        }
    }
}
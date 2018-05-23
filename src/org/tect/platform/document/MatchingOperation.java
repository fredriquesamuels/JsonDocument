package org.tect.platform.document;

import java.util.Date;
import java.util.function.Supplier;

public enum MatchingOperation implements QueryOperation {
    IN(new IsInOperation()),
    LIKE(new LikeOperation());

    private final QueryOperation q;

    MatchingOperation(QueryOperation q) {
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


    private static class IsInOperation extends BaseQueryOperation {


        @Override
        public String getName() {
            return MatchingOperation.IN.name();
        }

        @Override
        public String generate(QueryCondition condition, QueryContext context) {

            switch (condition.getType()) {
                case NUMBER:
                    return createFieldHbl(condition, context, "number", AttributeType.NUMBER, context.generateAttributeName());
                case TEXT:
                    return createFieldHbl(condition, context, "text", AttributeType.TEXT, context.generateAttributeName());
                case DECIMAL:
                    return createFieldHbl(condition, context, "decimal", AttributeType.DECIMAL, context.generateAttributeName());
                case DATE:
                    return date(condition, context);
//                case ARRAY:
//                    Object[] values = (Object[]) condition.getValue();
//                    List<QueryCondition> attributeList = Arrays.stream(values)
//                            .map(o -> AttributeFactory.createForValue(0, null, o, null))
//                            .map(a ->  {
//                                switch (a.type()) {
//                                    case TEXT:
//                                        return compare(condition.getFieldName().getName(),
//                                                CompareOperation.EQUALS, (String)a.value());
//                                    case NUMBER:
//                                        return compare(condition.getFieldName().getName(),
//                                                CompareOperation.EQUALS, (Long)a.value());
//                                    case DECIMAL:
//                                        return compare(condition.getFieldName().getName(),
//                                                CompareOperation.EQUALS, (Double)a.value());
//                                    case REFERENCE:
//                                        return QueryCondition.compare(condition.getFieldName().getName(), (DocumentReference)a.value());
//                                }
//                                return null;
//                            })
//                            .collect(Collectors.toList());
//
//                    String s = QueryCondition.orGroup(attributeList).generateString(context).trim();
//                    return String.format("%1$s.inList=true AND (%2$s)", context.generateAttributeName(), s==null || !s.isEmpty() ? s : "false");
            }
            return null;
        }

        private String date(QueryCondition condition, QueryContext context) {
            if(condition.getFieldName().isReservedField()) {
                String attributeName = context.generateAttributeName();
                return new FieldHblQueryBuilder(condition, context, null, null, attributeName)
                        .setReveredConditionSupplier(() -> {
                            Date[] dates = (Date[]) condition.getValue();
                            String fromVar = context.generateVar();
                            context.setParam(fromVar, dates[0]);

                            String toVar = context.generateVar();
                            context.setParam(toVar, dates[1]);

                            QueryFieldName fieldName = condition.getFieldName();
                            return String.format("%1$s >= (:%2$s) AND %1$s <= (:%3$s)", fieldName.getName(), fromVar, toVar);
                        })
                        .build();
            }
            return createFieldHbl(condition, context, "date", AttributeType.DATE, context.generateAttributeName());
        }

        private String createFieldHbl(QueryCondition condition, QueryContext context, String column, AttributeType columnType, String attributeName) {
            Supplier<String> valueConditionSupplier = () -> {
                String valueVar = context.generateVar();
                context.setParam(valueVar, condition.getValue());
                return String.format("%1$s.%2$s in (:%3$s)", attributeName, column, valueVar);
            };

            Supplier<String> reservedSupplier = () -> {
                String valueVar = context.generateVar();
                context.setParam(valueVar, condition.getValue());

                QueryFieldName fieldName = condition.getFieldName();
                return String.format("%1$s in (:%2$s)", fieldName.getName(), valueVar);
            };

            return new FieldHblQueryBuilder(condition, context, column, columnType, attributeName)
                    .setValueConditionSupplier(valueConditionSupplier)
                    .setReveredConditionSupplier(reservedSupplier)
                    .build();
        }
    }



    private static class LikeOperation extends BaseQueryOperation {

        @Override
        public String generate(QueryCondition condition, QueryContext context) {

            switch (condition.getType()) {
                case TEXT:
                    return createFieldHbl(condition, context, "text", AttributeType.TEXT, context.generateAttributeName());
            }
            return null;
        }

        @Override
        public String getName() {
            return MatchingOperation.LIKE.name();
        }

        private String createFieldHbl(QueryCondition condition, QueryContext context, String column, AttributeType columnType, String attributeName) {
            Supplier<String> valueConditionSupplier = () -> {
                String valueVar = context.generateVar();
                context.setParam(valueVar, condition.getValue());
                return String.format("%1$s.%2$s LIKE :%3$s", attributeName, column, valueVar);
            };

            return new FieldHblQueryBuilder(condition, context, column, columnType, attributeName)
                    .setValueConditionSupplier(valueConditionSupplier)
                    .build();
        }
    }
}
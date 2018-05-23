package org.tect.platform.document;

import java.util.function.Supplier;

class FieldHblQueryBuilder {

    private final QueryCondition condition;
    private final QueryContext context;
    private final String column;
    private final AttributeType columnType;
    private final String attributeName;
    private Supplier<String> valueConditionSupplier;
    private Supplier<String> reveredConditionSupplier;

    public FieldHblQueryBuilder(QueryCondition condition, QueryContext context, String column,
                                AttributeType columnType, String attributeName) {
        this.condition = condition;
        this.context = context;
        this.column = column;
        this.columnType = columnType;
        this.attributeName = attributeName;

        setValueConditionSupplier(() -> {
            String valueVar = context.generateVar();
            context.setParam(valueVar, condition.getValue());
            return String.format("%1$s.%2$s = :%3$s", attributeName, column, valueVar);
        });
    }

    public final String build() {
        QueryFieldName fieldName = condition.getFieldName();
        if(fieldName.isReservedField()) {
            return reveredConditionSupplier.get();
        }

        String fieldNameCondition = getFieldNameCondition(context, fieldName);

        String typeVar = context.generateVar();
        context.setParam(typeVar, columnType);
        String typeCondition = String.format("%1$s.type = :%2$s", attributeName, typeVar);

        return String.format("(doc.id in (select %1$s.documentId from AttributeHbm %1$s where %2$s AND %3$s %4$s))", attributeName, typeCondition, valueConditionSupplier.get(), fieldNameCondition);
    }

    public FieldHblQueryBuilder setValueConditionSupplier(Supplier<String> valueConditionSupplier) {
        this.valueConditionSupplier = valueConditionSupplier;
        return this;
    }

    public FieldHblQueryBuilder setReveredConditionSupplier(Supplier<String> reveredConditionSupplier) {
        this.reveredConditionSupplier = reveredConditionSupplier;
        return this;
    }

    private String getFieldNameCondition(QueryContext context, QueryFieldName fieldName) {
        String fieldNameCondition = "";

        if(fieldName.isPresent()) {
            String fieldNameVar = context.generateVar();
            context.setParam(fieldNameVar, fieldName.getName());
            fieldNameCondition = String.format("AND %s.name = :%s", attributeName, fieldNameVar);
        }
        return fieldNameCondition;
    }
}
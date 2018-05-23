package org.tect.platform.document;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class QueryCondition {

    static final String DOC_VAR = "doc";
    static final String ATTRIBUTE_VAR = "a";

    public abstract void populate(JsonElement element);

    public static QueryOperation getOperationFromString(String s) {
        try {
            return CompareOperation.valueOf(s);
        } catch (Exception ex) {
            try {
                return MatchingOperation.valueOf(s);
            } catch (Exception ex2) {
                return GroupOperation.valueOf(s);
            }
        }
    }


    QueryCondition() {
    }

    abstract QueryFieldName getFieldName();
    abstract QueryOperation getOperation();
    abstract Object getValue();
    abstract AttributeType getType();
    abstract String generateString(QueryContext queryContext);

    public boolean isAttributeCondition() {
        return true;
    }

    public static QueryCondition compare(String fieldName, CompareOperation operation, String value) {
        return new BaseCondition(new UserFieldName(fieldName), operation, value, AttributeType.TEXT);
    }

    public static QueryCondition compare(String fieldName, CompareOperation operation, long value) {
        return new BaseCondition(new UserFieldName(fieldName), operation, value, AttributeType.NUMBER);
    }

    public static QueryCondition compare(String fieldName, CompareOperation operation, double value) {
        return new BaseCondition(new UserFieldName(fieldName), operation, value, AttributeType.DECIMAL);
    }

    public static QueryCondition compare(String fieldName, CompareOperation operation, Date value) {
        return new BaseCondition(new UserFieldName(fieldName), operation, value, AttributeType.DATE);
    }

    public static QueryCondition compare(String fieldName, DocumentReference value) {
        return new BaseCondition(new UserFieldName(fieldName),  CompareOperation.EQUALS, value, AttributeType.REFERENCE);
    }

    public static QueryCondition isTrue(String fieldName) {
        return new BaseCondition(new UserFieldName(fieldName),  CompareOperation.EQUALS, true, AttributeType.BOOLEAN);
    }

    public static QueryCondition isFalse(String fieldName) {
        return new BaseCondition(new UserFieldName(fieldName),  CompareOperation.EQUALS, false, AttributeType.BOOLEAN);
    }

    public static QueryCondition isIn(String fieldName, Long[] value) {
        return new BaseCondition(new UserFieldName(fieldName),  MatchingOperation.IN, value, AttributeType.NUMBER);
    }

    public static QueryCondition isIn(String fieldName, String[] value) {
        return new BaseCondition(new UserFieldName(fieldName), MatchingOperation.IN, value, AttributeType.TEXT);
    }

    public static QueryCondition isIn(String fieldName, Double[] value) {
        return new BaseCondition(new UserFieldName(fieldName), MatchingOperation.IN, value, AttributeType.DECIMAL);
    }

    public static QueryCondition contains(String fieldName, Object[] value) {
        return new BaseCondition(new UserFieldName(fieldName), MatchingOperation.IN, value, AttributeType.ARRAY);
    }

    public static QueryCondition documentIdIn(Long ... ids) {
        return new BaseCondition(ReservedFieldName.DOC_ID, MatchingOperation.IN, ids, AttributeType.NUMBER);
    }

    public static QueryCondition documentIdIn(List<Long> ids) {
        return new BaseCondition(ReservedFieldName.DOC_ID, MatchingOperation.IN, ids.toArray(new Long[0]), AttributeType.NUMBER);
    }

    public static QueryCondition documentTypeIn(String... types) {
        String[] value = Arrays.stream(types).collect(Collectors.toList()).toArray(new String[0]);
        return new BaseCondition(ReservedFieldName.DOC_TYPE, MatchingOperation.IN, value, AttributeType.TEXT);
    }

    public static QueryCondition isLike(String fieldName, String value) {
        return new BaseCondition(new UserFieldName(fieldName), MatchingOperation.LIKE, value, AttributeType.TEXT);
    }

    public static QueryCondition orGroup(QueryCondition...value) {
        return new BaseCondition(null, GroupOperation.OR, value, null);
    }

    public static QueryCondition orGroup(List<QueryCondition> values) {
        return orGroup(values.toArray(new QueryCondition[]{}));
    }

    public static QueryCondition andGroup(QueryCondition...value) {
        return new BaseCondition(null, GroupOperation.AND, value, null);
    }

    public static QueryCondition andGroup(List<QueryCondition> values) {
        return andGroup(values.toArray(new QueryCondition[]{}));
    }

    private static class BaseCondition extends QueryCondition {
        private final QueryFieldName field;
        private final QueryOperation operation;
        private final Object value;
        private final AttributeType type;

        public BaseCondition(QueryFieldName field, QueryOperation operation, Object value, AttributeType type) {
            this.field = field;
            this.operation = operation;
            this.value = value;
            this.type = type;
        }

        @Override
        public void populate(JsonElement element) {


            if(this.field!=null) {
                JsonElement fieldEl = element.createElement("field");
                fieldEl.set("name", this.field.getName());
                fieldEl.set("is_reserved", this.field.isReservedField());
                fieldEl.set("is_present", this.field.isPresent());
            }

            element.set("operation", operation.getName());
            if (value!=null && value.getClass().isArray()) {
                JsonArray elementArray = element.createArray("value");
                Object[] valueArray = (Object[]) this.value;
                for (Object obj : valueArray) {
                    if(obj instanceof QueryCondition) {
                        QueryCondition c = (QueryCondition) obj;
                        c.populate(elementArray.createElement());
                    } else {
                        ((JsonArrayImpl)elementArray).addObject(obj);
                    }
                }
            } else {
                ((ElementImpl)element).setObject("value", value);
            }

            if(type!=null) {
                element.set("type", type.name());
            }
        }

        @Override
        public QueryFieldName getFieldName() {
            return field;
        }

        @Override
        public QueryOperation getOperation() {
            return operation;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public AttributeType getType() {
            return type;
        }

        @Override
        String generateString(QueryContext queryContext) {
            return operation.generateString(this, queryContext);
        }
    }

    private static class UserFieldName implements QueryFieldName {
        private final String fieldName;

        public UserFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public boolean isPresent() {
            return fieldName!=null;
        }

        @Override
        public String getName() {
            return fieldName;
        }

        @Override
        public boolean isReservedField() {
            return false;
        }
    }
}

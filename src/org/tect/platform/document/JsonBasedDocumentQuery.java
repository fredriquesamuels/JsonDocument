package org.tect.platform.document;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JsonBasedDocumentQuery extends DocumentQuery {

    public JsonBasedDocumentQuery(JsonElement element) {

        Consumer<JsonElement> elementConsumer = persistedElement -> JsonBasedDocumentQuery.this.addCondition(create(persistedElement));
        Consumer<JsonAttribute> attributeConsumer = persistedAttribute -> persistedAttribute.getElementValue().ifPresent(elementConsumer);
        Consumer<JsonArray> arrayConsumer = persistedArray -> persistedArray.forEach(attributeConsumer);
        element.getArrayValue("attribute_conditions").ifPresent(arrayConsumer);

        this.setOffset(element.getNumberValue("offset").get().intValue());
        this.setMaxResults(element.getNumberValue("max_results").get().intValue());
        this.setDocumentAttributeGrouping(GroupOperation.valueOf(element.getTextValue("grouping_operation").get()));
    }


    private static QueryCondition create(JsonElement jsonElement) {
        String operation = jsonElement.getTextValue("operation").get();
        JsonElement field = jsonElement.getElementValue("field").orElse(null);
        String type = jsonElement.getTextValue("type").orElse(null);
        AttributeType attributeType = type!=null ? AttributeType.valueOf(type) : null;
        Object value = Optional.ofNullable(jsonElement.get("value")).flatMap(jsonAttribute -> Optional.ofNullable(jsonAttribute.value())).orElse(null);

        QueryOperation operationEnum = QueryCondition.getOperationFromString(operation);

        String name = field!=null ? field.getTextValue("name").orElse(null) : null;
        boolean isReservedName = field!=null ? field.getBoolValue("is_reserved").orElse(false) : false;
        switch (operationEnum.getName()) {
            case "EQUALS":
            case "LESS_THAN":
            case "GREATER_THAN":
                CompareOperation compareOperation = CompareOperation.valueOf(operationEnum.getName());
                switch (attributeType) {
                    case TEXT:
                        return QueryCondition.compare(name, compareOperation, (String)value);
                    case NUMBER:
                        return QueryCondition.compare(name, compareOperation, (Long)value);
                    case DECIMAL:
                        return QueryCondition.compare(name, compareOperation, (Double)value);
                    case DATE:
                        return QueryCondition.compare(name, compareOperation, (Date)value);
                    case BOOLEAN:
                        if((Boolean) value) {
                            return QueryCondition.isTrue(name);
                        }
                        return QueryCondition.isFalse(name);
                    case REFERENCE:
                        return QueryCondition.compare(name, (DocumentReference) value);
                }
                break;
            case "IN":
                switch (attributeType) {
                    case TEXT:
                        if(isReservedName) {
                            if(name.equals(ReservedFieldName.DOC_TYPE.getName())) {
                                return QueryCondition.documentTypeIn( ((JsonArray) value)
                                        .stream()
                                        .map(a -> a.getTextValue().get()).collect(Collectors.toList())
                                        .toArray(new String[]{}));
                            }
                        } else {
                            return QueryCondition.isIn(name, ((JsonArray) value)
                                    .stream()
                                    .map(a -> a.getTextValue().get()).collect(Collectors.toList())
                                    .toArray(new String[]{}));
                        }
                    case NUMBER:
                        if(isReservedName) {
                            if(name.equals(ReservedFieldName.DOC_ID.getName())) {
                                return QueryCondition.documentIdIn( ((JsonArray) value)
                                        .stream()
                                        .map(a -> a.getNumberValue().get()).collect(Collectors.toList())
                                        .toArray(new Long[]{}));
                            }
                        } else {
                            return QueryCondition.isIn(name, ((JsonArray) value)
                                    .stream()
                                    .map(a -> a.getNumberValue().get()).collect(Collectors.toList())
                                    .toArray(new Long[]{}));
                        }
                    case DECIMAL:
                        return QueryCondition.isIn(name, ((JsonArray) value)
                                .stream()
                                .map(a -> a.getDecimalValue().get()).collect(Collectors.toList())
                                .toArray(new Double[]{}));
                }
                break;
            case "LIKE":
                switch (attributeType) {
                    case TEXT:
                        return QueryCondition.isLike(name, (String) value);
                }
                break;
            case "OR":
                return QueryCondition.orGroup(((JsonArray) value).stream()
                            .map( attr -> create(attr.getElementValue().get()))
                            .collect(Collectors.toList()));
            case "AND":
                return QueryCondition.andGroup(((JsonArray) value).stream()
                        .map( attr -> create(attr.getElementValue().get()))
                        .collect(Collectors.toList()));
        }
        throw new RuntimeException("Query Logic not valid!!!");
    }
}

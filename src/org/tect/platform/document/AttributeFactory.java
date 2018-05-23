package org.tect.platform.document;

import org.tect.platform.document.errors.UnsupportedTypeError;

import java.util.Collection;
import java.util.Date;

final class AttributeFactory {

    private AttributeFactory() {
    }

    static AttributeImpl createForValue(JsonDocument document, Object value, ParentAttribute parent) {
        AttributeImpl impl = createForValue(document.createId(), document, value, parent);

        if(impl==null) {
            throw new UnsupportedTypeError(value.getClass());
        }
        return impl;
    }

    static Object createValue(AttributeHbm hbm) {

        switch (hbm.type) {
            case TEXT:
                return hbm.text;
            case NUMBER:
                return hbm.number;
            case DECIMAL:
                return hbm.decimal;
            case DATE:
                return hbm.date;
            case BOOLEAN:
                return hbm.bool;
            case REFERENCE:
                return new DocumentReference(hbm.number, hbm.text);
        }
        return null;
    }

    static AttributeImpl createForValue(long id, JsonDocument document, Object value, ParentAttribute parent) {
        if(value==null) {
            return null;
        }
        if(isClass(value, String.class)) {
            return new BasicAttributeImpl(id, document, AttributeType.TEXT, value, parent);
        }

        if(isClass(value, Long.class)) {
            return new BasicAttributeImpl(id, document, AttributeType.NUMBER, value, parent);
        }

        if(isClass(value, Integer.class)) {
            return new BasicAttributeImpl(id, document, AttributeType.NUMBER, ((Integer)value).longValue(), parent);
        }

        if(isClass(value, Short.class)) {
            return new BasicAttributeImpl(id, document, AttributeType.NUMBER, ((Short)value).longValue(), parent);
        }

        if(isClass(value, Double.class)) {
            return new BasicAttributeImpl(id, document, AttributeType.DECIMAL, value, parent);
        }

        if(isClass(value, Float.class)) {
            return new BasicAttributeImpl(id, document, AttributeType.DECIMAL, ((Float)value).doubleValue(), parent);
        }

        if(isClass(value, Boolean.class)) {
            return new BasicAttributeImpl(id, document, AttributeType.BOOLEAN, value, parent);
        }

        if(value instanceof Date) {
            return new BasicAttributeImpl(id, document, AttributeType.DATE, value, parent);
        }

        if(isClass(value, JsonDocument.class)) {
            JsonDocument d = (JsonDocument) value;
            DocumentReference reference = d.getDocumentReference();
            return createReference(id, document, parent, reference);
        }

        if(isClass(value, DocumentReference.class)) {
            return createReference(id, document, parent, (DocumentReference) value);
        }

        if(value instanceof Collection) {
            return new JsonArrayImpl(id, document, (Collection)value, parent);
        }
        return null;
    }

    private static AttributeImpl createReference(long id, JsonDocument document, ParentAttribute parent, DocumentReference reference) {
        if(!reference.isPersisted()) {
            throw new IllegalArgumentException("Cannot create a reference to an unsaved document.");
        }
        return new BasicAttributeImpl(id, document, AttributeType.REFERENCE, reference, parent);
    }

    private static boolean isClass(Object value, Class<?> ... types) {
        for(Class<?> c : types) {
            if(value.getClass().equals(c)) {
                return true;
            }
        }
        return false;
    }


    private static class BasicAttributeImpl extends AttributeImpl {
        BasicAttributeImpl(long id, JsonDocument document, AttributeType type, Object value, ParentAttribute parent) {
            super(id, document, type, value, parent);
        }
    }
}

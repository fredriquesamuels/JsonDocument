package org.tect.platform.document;

import java.util.Date;
import java.util.Optional;

class AttributeImpl<T> implements JsonAttribute {

    private final JsonDocument document;
    private final ParentAttribute parent;
    private final AttributeType type;
    private final long id;
    private long persistedId=-1;

    protected final T value;

    AttributeImpl(JsonDocument document, AttributeType type, T value, ParentAttribute parent) {
        this(document.createId(), document, type, value, parent);
    }

    AttributeImpl(long id, JsonDocument document, AttributeType type, T value, ParentAttribute parent) {
        this.document = document;
        this.parent = parent;
        this.id = id;
        this.type = type;
        this.value = value;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isPersisted() {
        return persistedId>0;
    }

    @Override
    public AttributeType type() {
        return type;
    }

    @Override
    public JsonDocument getDocument() {
        return document;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public void delete() {
        parent.delete(this);
    }

    @Override
    public long getPersistedId() {
        return persistedId;
    }

    final void setPersistedId(long persistedId) {
        if(this.persistedId>0) {
            return;
        }
        this.persistedId = persistedId;
    }

    @Override
    public Object toObject() {
        return value();
    }

    @Override
    public Optional<String> getTextValue() {
        return getTypedValue(AttributeType.TEXT, String.class);
    }

    @Override
    public Optional<Long> getNumberValue() {
        return getTypedValue(AttributeType.NUMBER, Long.class);
    }

    @Override
    public Optional<Double> getDecimalValue() {
        return getTypedValue(AttributeType.DECIMAL, Double.class);
    }

    @Override
    public Optional<Boolean> getBoolValue() {
        return getTypedValue(AttributeType.BOOLEAN, Boolean.class);
    }

    @Override
    public Optional<Date> getDateValue() {
        return getTypedValue(AttributeType.DATE, Date.class);
    }

    @Override
    public Optional<DocumentReference> getReferenceValue() {
        return getTypedValue(AttributeType.REFERENCE, DocumentReference.class);
    }

    @Override
    public Optional<JsonArray> getArrayValue() {
        if(isType(AttributeType.ARRAY)) {
            return Optional.of((JsonArray)this);
        }
        return Optional.empty();
    }

    @Override
    public Optional<JsonElement> getElementValue() {
        if(isType(AttributeType.ELEMENT)) {
            return Optional.ofNullable((JsonElement)this);
        }
        return Optional.empty();
    }

    void populate(Object object) {
    }

    private <T> Optional<T> getTypedValue(AttributeType type, Class<T> aClass) {
        return isType(type) ? value(aClass) : Optional.empty() ;
    }

    private <T> Optional<T> value(Class<T> aClass) {
        return Optional.ofNullable(aClass.cast(value()));
    }

    private boolean isType(AttributeType type) {
        return this.type.equals(type);
    }
}

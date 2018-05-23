package org.tect.platform.document;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class JsonArrayImpl extends AttributeImpl<ArrayList<AttributeImpl>> implements JsonArray, ParentAttribute {

    JsonArrayImpl(long id, JsonDocument document, Collection value, ParentAttribute parent) {
        super(id, document, AttributeType.ARRAY, new ArrayList<>(), parent);
        value.stream().forEach( o -> addObject(o));
    }

    JsonArrayImpl(JsonDocument document, Collection value, ParentAttribute parent) {
        this(document.createId(), document, value, parent);
    }


    JsonAttribute addObject(Object value) {
        return internalAdd(AttributeFactory.createForValue(getDocument(), value, this));
    }

    private AttributeImpl internalAdd(AttributeImpl attribute) {
        if(attribute==null) {
            return null;
        }
        this.value.add(attribute);
        return attribute;
    }


    JsonAttribute setObject(int index, Object value) {
        deleteCurrent(index);
        if(value==null) {
            return null;
        }
        AttributeImpl attribute = AttributeFactory.createForValue(getDocument(), value, this);
        if(this.value.size()==index) {
            this.value.add(attribute);
            return attribute;
        }
        return this.value.set(index, attribute);
    }

    @Override
    public JsonAttribute add(String value) {
        return addObject(value);
    }

    @Override
    public JsonAttribute add(Long value) {
        return addObject(value);
    }

    @Override
    public JsonAttribute add(Double value) {
        return addObject(value);
    }

    @Override
    public JsonAttribute add(Date value) {
        return addObject(value);
    }

    @Override
    public JsonAttribute add(Boolean value) {
        return addObject(value);
    }

    @Override
    public JsonAttribute add(DocumentReference value) {
        return addObject(value);
    }

    @Override
    public JsonAttribute set(int index, String value) {
        return setObject(index, value);
    }

    @Override
    public JsonAttribute set(int index, Long value) {
        return setObject(index, value);
    }

    @Override
    public JsonAttribute set(int index, Double value) {
        return setObject(index, value);
    }

    @Override
    public JsonAttribute set(int index, Boolean value) {
        return setObject(index, value);
    }

    @Override
    public JsonAttribute set(int index, Date value) {
        return setObject(index, value);
    }

    @Override
    public JsonAttribute set(int index, DocumentReference value) {
        return setObject(index, value);
    }

    @Override
    public final JsonAttribute get(int index) {
        return value.get(index);
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public boolean forEach(Consumer<JsonAttribute> consumer) {
        this.value.forEach(consumer);
        return !this.value.isEmpty();
    }

    @Override
    public final JsonElement createElement() {
        return (JsonElement) internalAdd(new ElementImpl(getDocument(), this));
    }

    @Override
    public final JsonArray createArray() {
        return (JsonArray) internalAdd(new JsonArrayImpl(getDocument(), Collections.emptyList(), this));
    }

    @Override
    public void delete(JsonAttribute attribute) {
        AttributeImpl entry = this.value
                .stream()
                .filter(a -> a.getId() == attribute.getId())
                .findFirst()
                .orElse(null);

        if(entry==null) {
            return;
        }

        this.value.remove(entry);

        if(attribute.isPersisted()) {
            getDocument().addToDeleteQueue((AttributeImpl) attribute);
        }
    }

    @Override
    public void delete() {
        clear();
        super.delete();
    }

    @Override
    public void clear() {
        new LinkedList<>(this.value).forEach(AttributeImpl::delete);
    }

    @Override
    public Stream<JsonAttribute> stream() {
        List<JsonAttribute> l = this.value.stream().collect(Collectors.toList());
        return l.stream();
    }

    @Override
    public Optional<String> getTextValue(int index) {
        return getOptionalValue(index, a -> a.getTextValue());
    }

    @Override
    public Optional<Long> getNumberValue(int index) {
        return getOptionalValue(index, a -> a.getNumberValue());
    }

    @Override
    public Optional<Double> getDecimalValue(int index) {
        return getOptionalValue(index, a -> a.getDecimalValue());
    }

    @Override
    public Optional<Boolean> getBoolValue(int index) {
        return getOptionalValue(index, a -> a.getBoolValue());
    }

    @Override
    public Optional<Date> getDateValue(int index) {
        return getOptionalValue(index, a -> a.getDateValue());
    }

    @Override
    public Optional<DocumentReference> getReferenceValue(int index) {
        return Optional.empty();
    }

    @Override
    public Optional<JsonArray> getArrayValue(int index) {
        return getOptionalValue(index, a -> a.getArrayValue());
    }

    @Override
    public Optional<JsonElement> getElementValue(int index) {
        return getOptionalValue(index, a -> a.getElementValue());
    }

    @Override
    public Object value() {
        return this;
    }

    @Override
    public Object toObject() {
        ArrayList<Object> list = new ArrayList<>();
        forEach( v -> list.add(v.toObject()));
        return list;
    }

    @Override
    void populate(Object object) {
        if(object instanceof Collection) {
            Collection<Object> map = (Collection) object;
            map.forEach( (v) -> {
                if(v instanceof Map) {
                    ((AttributeImpl)createElement()).populate(v);
                } else if (v instanceof Collection) {
                    ((AttributeImpl)createArray()).populate(v);
                } else {
                    addObject(v);
                }
            });
        }
    }

    private <T> Optional<T> getOptionalValue(int index, Function<JsonAttribute, Optional<T>> f) {
        return f.apply(get(index));
    }

    AttributeImpl add(long id, Object value) {
        return internalAdd(AttributeFactory.createForValue(id, getDocument(), value, this));
    }

    final List<AttributeImpl> getAttributes() {
        return value;
    }

    final JsonArrayImpl createArray(long groupId, Consumer<JsonArrayImpl> isNew) {
        AttributeImpl attribute = findAttributeById(groupId);
        if (attribute == null) {
            JsonArrayImpl array = (JsonArrayImpl) internalAdd(new JsonArrayImpl(groupId, getDocument(), Collections.emptyList(), this));
            isNew.accept(array);
            return array;
        }
        return (JsonArrayImpl) attribute;
    }

    final ElementImpl createElement(long groupId, Consumer<ElementImpl> isNew) {
        AttributeImpl attribute = findAttributeById(groupId);
        if (attribute == null) {
            ElementImpl element = (ElementImpl) internalAdd(new ElementImpl(groupId, getDocument(), this));
            isNew.accept(element);
            return element;
        }
        return (ElementImpl) attribute;
    }

    private void deleteCurrent(int index) {
        if(this.value.size() > index) {
            JsonAttribute current = this.value.get(index);
            if(current!=null) {
                current.delete();
            }
        }
    }

    private AttributeImpl findAttributeById(long groupId) {
        return this.value.stream()
                .filter(v -> v.getId()==groupId)
                .findFirst()
                .orElse(null);
    }
}

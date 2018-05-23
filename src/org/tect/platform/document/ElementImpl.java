package org.tect.platform.document;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tect.platform.document.errors.ElementGetKeyCannotBeNestedJsanQuerySyntax;
import org.tect.platform.jsan.token.JSANTokenizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class ElementImpl extends AttributeImpl<LinkedHashMap<String, AttributeImpl>> implements JsonElement, ParentAttribute {


    public static final String PERSISTE_ID_JSON_KEY = "__persisted_id__";

    ElementImpl(JsonDocument document, ParentAttribute parent) {
        this(document!=null?document.createId():0, document, parent);
    }

    ElementImpl(long id, JsonDocument document, ParentAttribute parent) {
        super(id, document, AttributeType.ELEMENT, new LinkedHashMap<>(), parent);
    }


    JsonAttribute setObject(String key, Object value) {
        if(value==null) {
            return null;
        }
        return internalSet(key, () -> AttributeFactory.createForValue(getDocument(), value, this));
    }

    @Override
    public JsonAttribute set(String key, String value) {
        return setObject(key, value);
    }

    @Override
    public JsonAttribute set(String key, Long value) {
        return setObject(key, value);
    }

    @Override
    public JsonAttribute set(String key, Date value) {
        return setObject(key, value);
    }

    @Override
    public JsonAttribute set(String key, Double value) {
        return setObject(key, value);
    }

    @Override
    public JsonAttribute set(String key, DocumentReference value) {
        return setObject(key, value);
    }

    @Override
    public JsonAttribute set(String key, Boolean value) {
        return setObject(key, value);
    }

    @Override
    public JsonAttribute get(String key) {
        return this.value.get(key);
    }

    @Override
    public JsonElement createElement(String key) {
        return internalSet(key, () -> new ElementImpl(getDocument(), this));
    }

    @Override
    public JsonArray createArray(String key) {
        return internalSet(key, () -> new JsonArrayImpl(getDocument(), Collections.emptyList(), this));
    }

    @Override
    public void clear() {
        new LinkedList<>(this.value.values()).forEach(AttributeImpl::delete);
    }

    @Override
    public final Object value() {
        return this;
    }

    @Override
    public void forEach(BiConsumer<String, JsonAttribute> consumer) {
        this.value.forEach(consumer);
    }


    @Override
    public Object toObject() {
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
        forEach((s, v) -> hashMap.put(s, v.toObject()));

        if(isPersisted()) {
            hashMap.put(PERSISTE_ID_JSON_KEY, getPersistedId());
        }

        return hashMap;
    }

    @Override
    public void populate(Object object) {
        if(object instanceof Map) {
            populateFromMap((Map<String, Object>) object);
        }

        if(object instanceof JsonDocument) {
            populateFromMap(JsonUtils.parseToMap(((JsonDocument) object).toJson()));
        }

        if(object instanceof ElementImpl) {
            populateFromMap((Map<String, Object>) ((ElementImpl) object).toObject());
        }
    }

    @Override
    public int size() {
        return value.size();
    }

    private void populateFromMap(Map<String, Object> map) {
        map.forEach( (k, v) -> {
            if (JsonDocument.PERSISTE_ID_JSON_KEY.equals(k)) {
                return;
            }

            if(v instanceof Map) {
                ((AttributeImpl)createElement(k)).populate(v);
            } else if (v instanceof Collection) {
                ((AttributeImpl)createArray(k)).populate(v);
            } else {
                setObject(k, v);
            }
        });
    }

    private <T extends AttributeImpl> T internalSet(String key, Supplier<T> supplier) {
        checkNotQuerySyntax(key);
        deleteExisting(key);

        T attribute = supplier.get();
        if(attribute==null) {
            return null;
        }
        this.value.put(key, attribute);
        return attribute;
    }

    @Override
    public Optional<String> getTextValue(String key) {
        return getForType(key, a -> a.getTextValue());
    }

    @Override
    public Optional<Long> getNumberValue(String key) {
        return getForType(key, a -> a.getNumberValue());
    }

    @Override
    public Optional<Double> getDecimalValue(String key) {
        return getForType(key, a -> a.getDecimalValue());
    }

    @Override
    public Optional<Boolean> getBoolValue(String key) {
        return getForType(key, a -> a.getBoolValue());
    }

    @Override
    public Optional<Date> getDateValue(String key) {
        return getForType(key, a -> a.getDateValue());
    }

    @Override
    public Optional<DocumentReference> getReferenceValue(String key) {
        return getForType(key, a -> a.getReferenceValue());
    }

    @Override
    public Optional<JsonArray> getArrayValue(String key) {
        return getForType(key, a -> a.getArrayValue());
    }

    @Override
    public Optional<JsonElement> getElementValue(String key) {
        return getForType(key, a ->  a.getElementValue());
    }

    @Override
    public void delete(JsonAttribute attribute) {
        Map.Entry<String, AttributeImpl> entry = this.value.entrySet()
                .stream()
                .filter(es -> es.getValue().getId() == attribute.getId())
                .findFirst()
                .orElse(null);

        if (entry == null) {
            return;
        }

        this.value.remove(entry.getKey());

        if (attribute.isPersisted()) {
            getDocument().addToDeleteQueue((AttributeImpl) attribute);
        }
    }

    @Override
    public void delete() {
        clear();
        super.delete();
    }

    private <T> Optional<T> getForType(String key, Function<JsonAttribute, Optional<T>> getter) {
        JsonAttribute jsonAttribute = get(key);
        if(jsonAttribute==null) {
            return Optional.ofNullable(null);
        }
        return getter.apply(jsonAttribute);
    }

    Set<Map.Entry<String, AttributeImpl>> getAttributes() {
        return value.entrySet();
    }

    JsonArrayImpl createArray(String key, long groupId, Consumer<JsonArrayImpl> isNew) {
        AttributeImpl attribute = findAttributeById(groupId);
        if (attribute == null) {
            JsonArrayImpl array = internalSet(key, () -> new JsonArrayImpl(groupId, getDocument(), Collections.emptyList(), this));
            isNew.accept(array);
            return array;
        }
        return (JsonArrayImpl) attribute;
    }

    ElementImpl createElement(String key, long groupId, Consumer<ElementImpl> isNew) {
        AttributeImpl attribute = findAttributeById(groupId);
        if (attribute == null) {
            ElementImpl element = internalSet(key, () -> new ElementImpl(groupId, getDocument(), this));
            isNew.accept(element);
            return element;
        }
        return (ElementImpl) attribute;
    }

    private void deleteExisting(String key) {
        JsonAttribute current = value.get(key);
        if (current != null) {
            current.delete();
        }
    }

    private void checkNotQuerySyntax(String key) {
        int size = new JSANTokenizer().tokenize(key, Collections.emptyList()).size();
        if (size > 1) {
            throw new ElementGetKeyCannotBeNestedJsanQuerySyntax();
        }
    }

    private AttributeImpl findAttributeById(long groupId) {
        return this.value.values()
                .stream()
                .filter(v -> v.getId() == groupId)
                .findFirst()
                .orElse(null);
    }

    AttributeImpl set(long id, String name, Object value) {
        return internalSet(name, () -> AttributeFactory.createForValue(id, getDocument(), value, this));
    }



}

package org.tect.platform.document;

import org.tect.platform.document.errors.FirstTokenMustHaveANameError;
import org.tect.platform.jsan.token.JSANToken;
import org.tect.platform.jsan.token.JSANTokenizer;
import org.tect.platform.jsan.token.JSANTokens;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * A Document object representing a JSON data structure.
 */
public class JsonDocument extends ElementImpl {

    public static final String DEFAULT_TYPE = "org.tect.platform.document.PersistedDocument";

    private long attributeIdSeed=0;
    private final ConcurrentLinkedQueue<AttributeImpl> deleteQueue;
    private String type;

    /**
     * Create a new {@link JsonDocument} with the type of {@link #DEFAULT_TYPE}.
     */
    public JsonDocument() {
        this(DEFAULT_TYPE);
    }

    /**
     * Create a new {@link JsonDocument} with the given type.
     * @param type The user type.
     */
    public JsonDocument(String type) {
        super(null, null);
        this.type = type;
        deleteQueue = new ConcurrentLinkedQueue<>();
    }


    final JsonAttribute setObject(String key, Object value) {
        JSANTokens tokens = new JSANTokenizer().tokenize(key, Collections.emptyList());
        if(tokens.size()==0) {
            return null;
        }

        if(tokens.size()==1) {
            return setSingleTokenValue(value, tokens);
        }

        JsonAttribute parentAttr = this;
        JSANToken parentToken;
        int i=0;
        do {
            parentToken = tokens.get(i);
            String name = parentToken.getName();

            if(name!=null && !name.isEmpty()) {
                parentAttr = ((ElementImpl)parentAttr).get(name);
            }

            if(parentToken.isList()) {
                parentAttr = ((JsonArrayImpl)parentAttr).get(getIndex(parentToken));
            }

            i++;
        } while((i+1) < tokens.size() && parentAttr!=null);

        if(parentAttr==null) {
            throw new RuntimeException("Unknown attribute reference! " + key);
        }


        JSANToken token = tokens.get(i);

        if(parentToken.isObject()) {
            ElementImpl p = (ElementImpl) parentAttr;

            if(token.isList()) {
                JsonArrayImpl a = (JsonArrayImpl) p.get(token.getName());
                return a.setObject(getIndex(token), value);
            }
            return p.setObject(token.getName(), value);
        }

        JsonArrayImpl a = (JsonArrayImpl) parentAttr;
        return a.setObject(getIndex(token), value);
    }

    @Override
    public final JsonAttribute get(String key) {
        JSANTokens tokens = new JSANTokenizer().tokenize(key, Collections.emptyList());

        if(tokens.size()==0) {
            return null;
        }

        if(tokens.size()==1) {
            JSANToken token = tokens.get(0);
            JsonAttribute attribute = super.get(token.getName());

            if(token.isList()) {
                return ((JsonArrayImpl)attribute).get(getIndex(token));
            }
            return attribute;
        }

        JsonAttribute attr = this;
        int i=0;
        do {
            JSANToken token = tokens.get(i);
            String name = token.getName();

            assertFirstTokenConstraints(i, token);
            if(name!=null && !name.isEmpty()) {
                attr = ((ElementImpl)attr).get(name);
            }

            if(token.isList()) {
                attr = ((JsonArrayImpl)attr).get(getIndex(token));
            }
            i++;
        } while(i<tokens.size() && attr!=null);

        return attr;
    }

    @Override
    public final JsonDocument getDocument() {
        return this;
    }

    @Override
    public final void delete() {

    }

    public final DocumentReference getDocumentReference() {
        return new DocumentReference(this);
    }

    /**
     * @return Get the document type.
     */
    public final String getType() {
        return type;
    }

    /**
     * @return The json representation of this document.
     */
    public final String toJson() {
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) toObject();
        return JsonUtils.writeToString(map);
    }

    public final void populateFromJson(String json) {
        Map<String, Object> objectMap = JsonUtils.parseToMap(json);
        Long o = (Long) objectMap.get(PERSISTE_ID_JSON_KEY);
        if(o!=null) {
            this.setPersistedId(o);
        }

        super.populate(objectMap);
    }

    final synchronized long createId() {
        return ++attributeIdSeed;
    }

    private JsonAttribute setSingleTokenValue(Object value, JSANTokens tokens) {
        JSANToken token = tokens.get(0);

        if(token.isList()) {
            String name = token.getName();
            JsonArrayImpl array = getArrayForModification(name);
            return array.setObject(getIndex(token), value);
        }

        return super.setObject(token.getName(), value);
    }

    private JsonArrayImpl getArrayForModification(String name) {
        JsonAttribute attribute = get(name);
        if(!AttributeType.ARRAY.equals(attribute.type())) {
            super.createArray(name);
        }
        return (JsonArrayImpl) get(name);
    }

    private int getIndex(JSANToken token) {
        Long groupId = token.getGroupId();
        if(groupId==null) {
            throw new RuntimeException("Array index not provided!");
        }
        return groupId.intValue();
    }

    private void assertFirstTokenConstraints(int index, JSANToken token) {
        if(token.getName()==null && index==0) {
            throw new FirstTokenMustHaveANameError();
        }
    }

    @Override
    public String toString() {
        return toJson();
    }

    long getAttributeIdSeed() {
        return attributeIdSeed;
    }

    void setAttributeIdSeed(long attributeIdSeed) {
        this.attributeIdSeed = attributeIdSeed;
    }

    void addToDeleteQueue(AttributeImpl attribute) {
        this.deleteQueue.add(attribute);
    }

    void clearDeleteList() {
        this.deleteQueue.clear();
    }

    void forEachDelete(Consumer<AttributeImpl> c) {
        deleteQueue.forEach(c);
    }
}

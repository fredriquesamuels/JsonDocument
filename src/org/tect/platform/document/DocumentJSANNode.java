package org.tect.platform.document;

import org.tect.platform.jsan.JSANAttribute;
import org.tect.platform.jsan.JSANGroupAttribute;
import org.tect.platform.jsan.JSANKeyValueFactory;
import org.tect.platform.jsan.JSANNode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DocumentJSANNode extends JSANNode {

    private final JsonDocument document;

    public DocumentJSANNode(JsonDocument document) {
        this.document = document;
    }

    @Override
    public List<JSANAttribute> getAttributes() {
        Set<Map.Entry<String, AttributeImpl>> attributes = document.getAttributes();
        return attributes.stream().map( entry -> {
            String key = entry.getKey();
            AttributeImpl attribute = entry.getValue();
            return createJSANAttribute(key, attribute);
        }).collect(Collectors.toList());
    }

    private static JSANAttribute createJSANAttribute(String key, AttributeImpl attribute) {
        if(attribute.type()== AttributeType.ARRAY) {
            return new ArrayJSAN(key, attribute);
        }

        if(attribute.type()==AttributeType.ELEMENT) {
            return new ElementJSAN(key, attribute);
        }

        return new JSANValue(key, attribute);
    }

    static class Factory implements JSANKeyValueFactory<AttributeHbm> {

        private long documentId;

        public Factory(long documentId) {
            this.documentId = documentId;
        }

        @Override
        public AttributeHbm create(String name, Object value, List<Long> groupIds) {
            return new AttributeHbm(name, (AttributeImpl)value, groupIds, documentId);
        }
    }

    private static class ArrayJSAN extends JSANGroupAttribute {
        private String key;
        private JsonArrayImpl attribute;

        public ArrayJSAN(String key, AttributeImpl attribute) {
            super(attribute.getId());
            this.key = key;
            this.attribute = (JsonArrayImpl) attribute;
        }

        @Override
        public List<JSANAttribute> getAttributes() {
            return attribute.getAttributes()
                    .stream()
                    .map( a -> createJSANAttribute(null, a))
                    .collect(Collectors.toList());
        }

        @Override
        public boolean isArray() {
            return true;
        }

        @Override
        public boolean isElement() {
            return false;
        }

        @Override
        public String getName() {
            return key;
        }

        @Override
        public Object getValue() {
            return null;
        }
    }

    private static class ElementJSAN extends JSANGroupAttribute {
        private String key;
        private ElementImpl attribute;

        public ElementJSAN(String key, AttributeImpl attribute) {
            super(attribute.getId());
            this.key = key;
            this.attribute = (ElementImpl) attribute;
        }

        @Override
        public List getAttributes() {
            return attribute.getAttributes()
                .stream()
                .map( entry -> createJSANAttribute(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isElement() {
            return true;
        }

        @Override
        public String getName() {
            return key;
        }

        @Override
        public Object getValue() {
            return null;
        }
    }

    private static class JSANValue implements JSANAttribute {
        private final String key;
        private final AttributeImpl attribute;

        public JSANValue(String key, AttributeImpl attribute) {
            super();
            this.key = key;
            this.attribute = attribute;
        }

        @Override
        public String getName() {
            return key;
        }

        @Override
        public Object getValue() {
            return attribute;
        }
    }
}

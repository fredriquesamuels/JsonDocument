package org.tect.platform.document;

import org.tect.platform.jsan.ParsedJSANGroup;
import org.tect.platform.jsan.ParsedJSANNode;

import java.util.function.Consumer;

public class DocumentJSANReader implements ParsedJSANNode {
    private JsonDocument document;

    public DocumentJSANReader(JsonDocument document) {
        this.document = document;
    }

    @Override
    public ParsedJSANGroup addArray(String name, long groupId) {
        return new ArrayGroup(document.createArray(name, groupId,a -> {}));
    }

    @Override
    public ParsedJSANGroup addObject(String name, long groupId) {
        return new ElementGroup(document.createElement(name, groupId, a -> {}));
    }

    @Override
    public void setValue(String name, Object value) {
        AttributeHbm hbm = (AttributeHbm) value;
        AttributeImpl a = document.set(hbm.attributeId, name, AttributeFactory.createValue(hbm));
        a.setPersistedId(hbm.getId());
    }

    @Override
    public long getGroupId() {
        return 0;
    }

    private class ArrayGroup implements ParsedJSANGroup {
        private JsonArrayImpl array;

        public ArrayGroup(JsonArrayImpl array) {
            this.array = array;
        }

        @Override
        public ParsedJSANGroup addArray(String name, long groupId) {
            return new ArrayGroup(array.createArray(groupId, a -> {}));
        }

        @Override
        public ParsedJSANGroup addObject(String name, long groupId) {
            return new ElementGroup(array.createElement(groupId, a -> {}));
        }

        @Override
        public void setValue(String name, Object value) {
            AttributeHbm hbm = (AttributeHbm) value;
            AttributeImpl a = array.add(hbm.attributeId, AttributeFactory.createValue(hbm));
            a.setPersistedId(hbm.getId());
        }

        @Override
        public long getGroupId() {
            return array.getId();
        }
    }

    private class ElementGroup implements ParsedJSANGroup {
        private ElementImpl element;

        public ElementGroup(ElementImpl element) {
            this.element = element;
        }

        @Override
        public ParsedJSANGroup addArray(String name, long groupId) {
            return new ArrayGroup(element.createArray(name, groupId,a -> {}));
        }

        @Override
        public ParsedJSANGroup addObject(String name, long groupId) {
            return new ElementGroup(element.createElement(name, groupId, a -> {}));
        }

        @Override
        public void setValue(String name, Object value) {
            AttributeHbm hbm = (AttributeHbm) value;
            AttributeImpl a = element.set(hbm.attributeId, name, AttributeFactory.createValue(hbm));
            a.setPersistedId(hbm.getId());
        }

        @Override
        public long getGroupId() {
            return element.getId();
        }
    }

    class PopulateAttribute implements Consumer<AttributeImpl> {

        private AttributeImpl attribute;

        public PopulateAttribute(AttributeImpl attribute) {
            this.attribute = attribute;
        }

        @Override
        public void accept(AttributeImpl attribute) {

        }
    }
}

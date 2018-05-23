package org.tect.platform.document;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class PersistedDocumentTest {

    private JsonDocument document;

    @Before
    public void setUp() throws Exception {
        document = new JsonDocument();
    }

    @Test
    public void isNotPersistedByDefault() {
        assertFalse(document.isPersisted());
    }

    @Test
    public void setTextValue() {
        //when
        String key = "attrName";
        String value = "value";
        JsonAttribute a = document.set(key, value);

        //then
        assertNotNull(a);
        assertEquals(value, document.get(key).value());
        assertEquals(a.type(), AttributeType.TEXT);
    }

    @Test
    public void setNumberValue() {
        //when
        String key = "attrName";
        Long value = 5L;
        JsonAttribute a = document.set(key, value);

        //then
        assertNotNull(a);
        assertEquals(value, document.get(key).value());
        assertEquals(a.type(), AttributeType.NUMBER);
    }

    @Test
    public void setDecimalValue() {
        //when
        String key = "attrName";
        Double value = 5.0;
        JsonAttribute a = document.set(key, value);

        //then
        assertNotNull(a);
        assertEquals(value, document.get(key).value());
        assertEquals(a.type(), AttributeType.DECIMAL);
    }

    @Test
    public void setBooleanValue() {
        //when
        String key = "attrName";
        Boolean value = false;
        JsonAttribute a = document.set(key, value);

        //then
        assertNotNull(a);
        assertEquals(value, document.get(key).value());
        assertEquals(a.type(), AttributeType.BOOLEAN);
    }

    @Test
    public void setDateValue() {
        //when
        String key = "attrName";
        Date value = new Date(1517144291826L);
        JsonAttribute a = document.set(key, value);

        //then
        assertNotNull(a);
        assertEquals(value.getTime(), document.getDateValue(key).get().getTime());
        assertEquals(a.type(), AttributeType.DATE);
    }

    @Test
    public void setDocumentReference() {
        //given
        JsonDocument persistedDocument = new JsonDocument();
        persistedDocument.setPersistedId(2L);

        //when
        String key = "attrName";
        JsonAttribute a = document.set(key, persistedDocument.getDocumentReference());

        //then
        assertNotNull(a);

        DocumentReference reference = (DocumentReference) document.get(key).value();

        assertEquals(2L, reference.getId());
        assertEquals(JsonDocument.DEFAULT_TYPE, reference.getType());
        assertEquals(a.type(), AttributeType.REFERENCE);
    }

    @Test
    public void setElementValue_UsingAPI_GetUsingJsanKey() {
        //given
        String key = "el.attr";
        String value = "value";

        //when
        document.createElement("el")
                .set("attr", value);

        //then
        JsonAttribute a = document.get(key);

        assertNotNull(a);
        assertEquals(value, a.value());
        assertEquals(a.type(), AttributeType.TEXT);
    }

    @Test
    public void setElementValue_UsingJsan_GetUsingJsanKey() {
        //given
        String key = "el.attr";
        String value = "value";

        //when
        document.createElement("el");
        document.set(key, value);

        //then
        JsonAttribute a = document.get(key);

        assertNotNull(a);
        assertEquals(value, a.value());
        assertEquals(a.type(), AttributeType.TEXT);
    }

    @Test
    public void setArrayValue() {
        //given
        String key = "attr";
        String value = "value";

        //when
        document.createArray(key);

        //then
        JsonAttribute a =  document.get(key);
        ((JsonArray)a).add(value);

        assertNotNull(a);
        assertEquals(a.type(), AttributeType.ARRAY);
        assertEquals(value, ((JsonArray) a).get(0).value());
    }

    @Test
    public void setArrayValue_UsingCreate() {
        //given
        String key = "attr";
        String value = "value";

        //when
        document.createArray(key).add(value);

        //then
        JsonAttribute a = document.get("attr");
        assertNotNull(a);
        assertEquals(a.type(), AttributeType.ARRAY);
        assertEquals(value, ((JsonArray) a).get(0).value());
    }

    @Test
    public void setArrayValue_ByIndex() {
        //given
        String key = "attr";

        JsonArray array = document.createArray(key);
        array.add("value1");
        array.add("value2");

        //when
        String value3 = "value3";
        array.set(1, value3);

        //then
        JsonAttribute a = document.get("attr");
        assertNotNull(a);
        assertEquals(a.type(), AttributeType.ARRAY);
        assertEquals(value3, ((JsonArray) a).get(1).value());
    }

    @Test
    public void setArrayValue_ByIndex_UsingJsan() {
        //given
        String key = "attr";

        JsonArray array = document.createArray(key);
        array.add("value1");
        array.add("value2");

        //when
        String value3 = "value3";
        document.set("attr[1]", value3);

        //then
        {
            JsonAttribute a = document.get("attr");
            assertEquals(value3, ((JsonArray) a).get(1).value());
        }
        {
            JsonAttribute a = document.get("attr[1]");
            assertEquals(value3, a.value());
        }
    }

    @Test
    public void nestedArrays() {
        //given
        JsonArray array = document.createArray("a1").createArray();
        array.add("value");

        //then
        JsonAttribute a = document.get("a1[0][0]");
        assertEquals("value", a.value());
    }

    @Test
    public void elementsInArrays() {
        //given
        JsonArray array = document.createArray("a1");
        JsonElement element = array.createElement();
        element.set("name", "josh");

        //then
        JsonAttribute a = document.get("a1[0].name");
        assertEquals("josh", a.value());
    }

    @Test
    public void deleteAttribute() {
        //given
        JsonAttribute attribute = document.set("attr", "value");
        ((AttributeImpl)attribute).setPersistedId(3L);

        //when
        attribute.delete();

        //then
        TestConsumer consumer = new TestConsumer();
        document.forEachDelete(consumer);
        assertNull(document.get("attr"));
        assertNotNull(consumer.attribute);
    }

    class TestConsumer implements Consumer<AttributeImpl> {

        private AttributeImpl attribute;

        @Override
        public void accept(AttributeImpl attribute) {
            this.attribute = attribute;
        }
    }
}
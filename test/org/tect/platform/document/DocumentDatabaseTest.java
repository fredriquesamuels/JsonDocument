package org.tect.platform.document;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class DocumentDatabaseTest extends BaseDaoTest {

    @Test
    public void saveDocument() {
        //given
        assertFalse(document.isPersisted());

        //when
        dao.saveDocument(document);

        //then
        assertTrue(document.isPersisted());
    }

    @Test
    public void testAll()  {


        {
            JsonDocument d = new JsonDocument();
            d.set("key", "John");
            d.set("key2", "Summers");
            dao.saveDocument(d);
        }

        {
            JsonDocument d = new JsonDocument();
            d.set("key", "John");
            dao.saveDocument(d);
        }

        {
            JsonDocument d = new JsonDocument();
            d.set("key", "John");
            d.set("key2", "Summerson");
            dao.saveDocument(d);
        }

        {
            JsonDocument d = new JsonDocument();
            d.set("key", 5L);
            dao.saveDocument(d);
        }

        {
            JsonDocument d = new JsonDocument();
            d.set("key", 5.5);
            dao.saveDocument(d);
        }
        assertEquals(1,1);

    }

    @Test
    public void saveAttribute() {
        //given
        JsonAttribute a = document.set("key", "value");
        assertFalse(a.isPersisted());

        //when
        dao.saveDocument(document);

        //then
        assertTrue(a.isPersisted());
    }

    @Test
    public void readDocument() {
        //given
        dao.saveDocument(document);

        //when
        JsonDocument read = dao.getDocument(document.getPersistedId());
        assertNotNull(read);
        assertEquals(document.getType(), read.getType());
    }

    @Test
    public void readText() {
        testSaveAndReadAttribute("value");
    }

    @Test
    public void readNumber() {
        testSaveAndReadAttribute(5L);
    }

    @Test
    public void readDecimal() {
        testSaveAndReadAttribute(5.5);
    }

    @Test
    public void readBoolean() {
        testSaveAndReadAttribute(false);
    }

    @Test
    public void readDate() {
        Date value = new Date(1517144291826L);
        JsonAttribute attribute = saveAndRead(value);
        assertEquals(value.getTime(), ((Date)attribute.value()).getTime());
    }

    @Test
    public void readReference() {
        DocumentReference reference = new DocumentReference(3, JsonDocument.DEFAULT_TYPE);
        DocumentReference read = (DocumentReference) saveAndRead(reference).value();
        assertEquals(3L, read.getId());
        assertEquals(JsonDocument.DEFAULT_TYPE, read.getType());
    }

    @Test
    public void readList() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("value");

        JsonAttribute attribute = saveAndRead(objects);

        JsonArray array = (JsonArray) attribute.value();
        assertEquals("value", array.get(0).value().toString());
    }

    @Test
    public void readElement() {
        String key = "key";
        document.createElement(key).set("name", "Josh");

        JsonAttribute attribute = saveAndReadKey(key);
        JsonElement element = (JsonElement) attribute.value();
        assertEquals("Josh", element.get("name").value().toString());
    }


    @Test
    public void deleteAttribute() {
        //given
        document.set("a1", "value1");
        JsonAttribute a2 = document.set("a2", "value2");
        dao.saveDocument(document);

        assertTrue(a2.isPersisted());

        //when
        a2.delete();
        dao.saveDocument(document);

        //then
        JsonDocument read = dao.getDocument(this.document.getPersistedId());
        JsonAttribute a = read.get("a2");
        assertNull(a);
    }


    private void testSaveAndReadAttribute(Object value) {
        JsonAttribute attribute = saveAndRead(value);
        assertTrue(attribute.value().equals(value));
    }

    private JsonAttribute saveAndRead(Object value) {
        return saveAndRead(value, document);
    }

    private JsonAttribute saveAndRead(Object value, JsonDocument d) {
        //given
        String key = "key";
        d.setObject(key,value);

        //when
        return saveAndReadKey(key);
    }

    private JsonAttribute saveAndReadKey(String key) {
        dao.saveDocument(document);
        JsonDocument read = dao.getDocument(document.getPersistedId());
        JsonAttribute attribute = read.get(key);
        assertNotNull(read.get(key));
        return attribute;
    }



}

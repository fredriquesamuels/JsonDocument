package org.tect.platform.document;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DoumentJsonTest {

    private static final long TIME_IN_MILLI = 1517144291826L;
    private static final String ATTR_NAME = "attr_test";
    private static final long parentId = 4L;

    private JsonDocument document;

    @Before
    public void setUp() throws Exception {
        document = new JsonDocument();
    }

    @Test
    public void textToJson() {
        //given
        String json = createTextJson();

        //then
        assertEquals("{\"attr_test\":\"text\"}", json);
    }

    private String createTextJson() {
        document.set(ATTR_NAME, "text");

        //when
        return document.toJson();
    }

//    @Test
//    public void textFromJson() {
//        //given
//        String json = createTextJson();
//
//        //
//        new PersistedDocument().populateFromJson(json);
//        PersistedDocument.fromJson();
//
//        //then
//        NodeTestUtils.assertText(PersistedDocument.fromJson(json), ATTR_NAME, textNode);
//    }

//    @Test
//    public void numberToJson() {
//        //given
//        NodeTestUtils.createNumberNode(document, ATTR_NAME);
//
//        //when
//        String json = document.toJson();
//
//        //then
//        assertEquals("{\"attr_test\":5}", json);
//    }
//
//    @Test
//    public void numberFromJson() {
//        //given
//        Long value = NodeTestUtils.createNumberNode(document, ATTR_NAME);
//        String json = document.toJson();
//
//        //then
//        NodeTestUtils.assertNumber(PersistedDocument.fromJson(json), ATTR_NAME, value);
//    }
//
//    @Test
//    public void decimalToJson() {
//        //given
//        NodeTestUtils.createDecimalNode(document, ATTR_NAME);
//
//        //when
//        String json = document.toJson();
//
//        //then
//        assertEquals("{\"attr_test\":5.5}", json);
//    }
//
//    @Test
//    public void decimalFromJson() {
//        //given
//        Double value = NodeTestUtils.createDecimalNode(document, ATTR_NAME);
//        String json = document.toJson();
//
//        //then
//        NodeTestUtils.assertDecimal(PersistedDocument.fromJson(json), ATTR_NAME, value);
//    }
//
//    @Test
//    public void boolToJson() {
//        //given
//        NodeTestUtils.createBoolNode(document, ATTR_NAME);
//
//        //when
//        String json = document.toJson();
//
//        //then
//        assertEquals("{\"attr_test\":false}", json);
//    }
//
//    @Test
//    public void boolFromJson() {
//        //given
//        boolean value = NodeTestUtils.createBoolNode(document, ATTR_NAME);
//        String json = document.toJson();
//
//        //then
//        NodeTestUtils.assertBoolean(PersistedDocument.fromJson(json), ATTR_NAME, value);
//    }
//
//    @Test
//    public void dateToJson() {
//        //given
//        NodeTestUtils.createDate(document, ATTR_NAME);
//
//        //when
//        String json = document.toJson();
//
//        //then
//        assertEquals("{\"attr_test\":{\"document.type\":\"document.date\",\"value\":1517144291826}}", json);
//    }
//
//    @Test
//    public void dateFromJson() {
//        //given
//        UtcDate value = NodeTestUtils.createDate(document, ATTR_NAME);
//        String json = document.toJson();
//
//        //then
//        NodeTestUtils.assertDate(PersistedDocument.fromJson(json), ATTR_NAME, value);
//    }
//
//    @Test
//    public void nodeRefToJson() {
//        //given
//        NodeTestUtils.createNodeRefNode(document, ATTR_NAME);
//
//        //when
//        String json = document.toJson();
//
//        //then
//        assertEquals("{\"attr_test\":{\"document.type\":\"document.noderef\",\"value\":\"my.package:MyType:3\"}}", json);
//    }
//
//    @Test
//    public void nodeRefFromJson() {
//        //given
//        PersistedDocument value = NodeTestUtils.createNodeRefNode(document, ATTR_NAME);
//        String json = document.toJson();
//
//        //then
//        NodeTestUtils.assertNodeRef(PersistedDocument.fromJson(json), ATTR_NAME, value.getRef());
//    }
//
//    @Test
//    public void listToJson() {
//        //given
//        NodeTestUtils.createListNode(document, ATTR_NAME);
//
//        //when
//        String json = document.toJson();
//
//        //then
//        assertEquals("{\"attr_test\":[\"text\",23]}", json);
//    }
//
//
//    @Test
//    public void listFromJson() {
//        //given
//        NodeTestUtils.createListNode(document, ATTR_NAME);
//        String json = document.toJson();
//
//        //then
//        NodeTestUtils.assertList(PersistedDocument.fromJson(json), ATTR_NAME);
//    }
//
//    @Test
//    public void nestedNodeToJson() {
//        //given
//        NodeTestUtils.createNestedNode(document, ATTR_NAME);
//
//        //when
//        String json = document.toJson();
//
//        //then
//        assertEquals("{\"attr_test\":{\"name\":\"text\",\"age\":23}}", json);
//    }
//
//
//    @Test
//    public void nestedNodeFromJson() {
//        //given
//        NodeTestUtils.createNestedNode(document, ATTR_NAME);
//        String json = document.toJson();
//
//        //then
//        NodeTestUtils.assertNestedNode(PersistedDocument.fromJson(json), ATTR_NAME);
//    }
}

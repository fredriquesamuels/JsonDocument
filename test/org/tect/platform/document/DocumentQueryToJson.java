package org.tect.platform.document;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class DocumentQueryToJson {


    private DocumentQuery documentQuery;

    @Before
    public void setUp() throws Exception {
        this.documentQuery = new DocumentQuery();
    }

    @Test
    public void basicQuery() {
        JsonDocument document = documentQuery.toDocument();
        assertEquals("{\"attribute_conditions\":[],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}", document.toJson());
    }

    @Test
    public void compareStringToJson() {
        //when
        documentQuery.addCondition(QueryCondition.compare("x", CompareOperation.EQUALS, "text"));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":\"text\",\"type\":\"TEXT\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void compareStringFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.compare("x", CompareOperation.EQUALS, "text"));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(CompareOperation.EQUALS, condition.getOperation());
    }

    @Test
    public void compareLongFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.compare("x", CompareOperation.GREATER_THAN, 5L));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(CompareOperation.GREATER_THAN, condition.getOperation());
    }

    @Test
    public void compareLong() {
        //when
        documentQuery.addCondition(QueryCondition.compare("x", CompareOperation.EQUALS, 55L));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":55,\"type\":\"NUMBER\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void compareDouble() {
        //when
        documentQuery.addCondition(QueryCondition.compare("x", CompareOperation.EQUALS, 5.5));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":5.5,\"type\":\"DECIMAL\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void compareDoubleFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.compare("x", CompareOperation.LESS_THAN, 5.5));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(CompareOperation.LESS_THAN, condition.getOperation());
    }

    @Test
    public void compareDate() {
        //when
        documentQuery.addCondition(QueryCondition.compare("x", CompareOperation.EQUALS, new Date(1517144291826L)));

        //then
        JsonDocument document = documentQuery.toDocument();
//        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":{\"millisecondsSinceEpoc\":1517144291826,\"hour\":12,\"second\":11,\"minute\":58,\"year\":2018,\"month\":1,\"day\":28},\"type\":\"DATE\"}],\"doc_conditions\":[]}";
//        assertEquals(expected, document.toJson());
    }

    @Test
    public void compareDateFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.compare("x", CompareOperation.EQUALS, new Date(1517144291826L)));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(CompareOperation.EQUALS, condition.getOperation());
        assertTrue(condition.getValue() instanceof Date);
        assertEquals(1517144291826L, ((Date)condition.getValue()).getTime());
    }



    @Test
    public void compareDocumentReference() {
        //when
        DocumentReference documentReference = new DocumentReference(3L, JsonDocument.DEFAULT_TYPE);
        documentQuery.addCondition(QueryCondition.compare("x",  documentReference));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":{\"id\":3,\"type\":\"org.tect.platform.document.PersistedDocument\",\"persisted\":true},\"type\":\"REFERENCE\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void compareDocRefFromJson() {
        //given
        DocumentReference documentReference = new DocumentReference(3L, JsonDocument.DEFAULT_TYPE);
        documentQuery.addCondition(QueryCondition.compare("x",  documentReference));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(CompareOperation.EQUALS, condition.getOperation());
        assertTrue(condition.getValue() instanceof DocumentReference);
    }

    @Test
    public void compareIsTrue() {
        //when
        documentQuery.addCondition(QueryCondition.isTrue("x"));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":true,\"type\":\"BOOLEAN\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void compareIsTrueFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.isTrue("x"));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(CompareOperation.EQUALS, condition.getOperation());
        assertTrue((Boolean)condition.getValue());
    }

    @Test
    public void compareIsFalse() {
        //when
        documentQuery.addCondition(QueryCondition.isFalse("x"));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":false,\"type\":\"BOOLEAN\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void compareIsFalseFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.isFalse("x"));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(CompareOperation.EQUALS, condition.getOperation());
        assertFalse((Boolean)condition.getValue());
    }

    @Test
    public void inLongList() {
        //when
        documentQuery.addCondition(QueryCondition.isIn("x", new Long[]{5L, 6L}));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"IN\",\"value\":[5,6],\"type\":\"NUMBER\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void inLongListFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.isIn("x", new Long[]{5L, 6L}));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(MatchingOperation.IN, condition.getOperation());
        assertTrue(condition.getValue() instanceof Long[]);
        assertEquals(2, ((Long[])condition.getValue()).length);
    }

    @Test
    public void inStringList() {
        //when
        documentQuery.addCondition(QueryCondition.isIn("x", new String[]{"a", "b"}));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"IN\",\"value\":[\"a\",\"b\"],\"type\":\"TEXT\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void inStringListFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.isIn("x", new String[]{"a", "b"}));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(MatchingOperation.IN, condition.getOperation());
        assertTrue(condition.getValue() instanceof String[]);
        assertEquals(2, ((String[])condition.getValue()).length);
    }

    @Test
    public void inDoubleList() {
        //when
        documentQuery.addCondition(QueryCondition.isIn("x", new Double[]{5.5, 4.7}));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"IN\",\"value\":[5.5,4.7],\"type\":\"DECIMAL\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void inDoubleListFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.isIn("x", new Double[]{5.5, 4.7}));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(MatchingOperation.IN, condition.getOperation());
        assertTrue(condition.getValue() instanceof Double[]);
        assertEquals(2, ((Double[])condition.getValue()).length);
    }

    @Test
    public void docIdIn() {
        //when
        documentQuery.addCondition(QueryCondition.documentIdIn(3L));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"doc.id\",\"is_reserved\":true,\"is_present\":true},\"operation\":\"IN\",\"value\":[3],\"type\":\"NUMBER\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void docIdInFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.documentIdIn(3L));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("doc.id", condition.getFieldName().getName());
        assertTrue(condition.getFieldName().isReservedField());
        assertEquals(MatchingOperation.IN, condition.getOperation());
        assertTrue(condition.getValue() instanceof Long[]);
        assertEquals(1, ((Long[])condition.getValue()).length);
    }

    @Test
    public void docTypeIn() {
        //when
        documentQuery.addCondition(QueryCondition.documentTypeIn(JsonDocument.DEFAULT_TYPE));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"doc.type\",\"is_reserved\":true,\"is_present\":true},\"operation\":\"IN\",\"value\":[\"org.tect.platform.document.PersistedDocument\"],\"type\":\"TEXT\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void docTypeInFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.documentTypeIn(JsonDocument.DEFAULT_TYPE));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("doc.type", condition.getFieldName().getName());
        assertTrue(condition.getFieldName().isReservedField());
        assertEquals(MatchingOperation.IN, condition.getOperation());
        assertTrue(condition.getValue() instanceof String[]);
        assertEquals(1, ((String[])condition.getValue()).length);
    }

    @Test
    public void isLike() {
        //when
        documentQuery.addCondition(QueryCondition.isLike("x", "%x%"));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"LIKE\",\"value\":\"%x%\",\"type\":\"TEXT\"}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void isLikeFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.isLike("x", "%x%"));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals("x", condition.getFieldName().getName());
        assertEquals(MatchingOperation.LIKE, condition.getOperation());
        assertTrue(condition.getValue() instanceof String);
    }

    @Test
    public void orGroup() {
        //when
        documentQuery.addCondition(QueryCondition.orGroup(
                QueryCondition.compare("x", CompareOperation.EQUALS, 5),
                QueryCondition.compare("x", CompareOperation.EQUALS, 6)
        ));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"operation\":\"OR\",\"value\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":5,\"type\":\"NUMBER\"},{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":6,\"type\":\"NUMBER\"}]}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }

    @Test
    public void orGroupFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.orGroup(
                QueryCondition.compare("x", CompareOperation.EQUALS, 5),
                QueryCondition.compare("x", CompareOperation.EQUALS, 6)
        ));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals(GroupOperation.OR, condition.getOperation());
        assertTrue(condition.getValue() instanceof QueryCondition[]);
        assertEquals(2, ((QueryCondition[])condition.getValue()).length);
    }

    @Test
    public void andGroup() {
        //when
        documentQuery.addCondition(QueryCondition.andGroup(
                QueryCondition.compare("x", CompareOperation.EQUALS, 5),
                QueryCondition.compare("x", CompareOperation.EQUALS, 6)
        ));

        //then
        JsonDocument document = documentQuery.toDocument();
        String expected = "{\"attribute_conditions\":[{\"operation\":\"AND\",\"value\":[{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":5,\"type\":\"NUMBER\"},{\"field\":{\"name\":\"x\",\"is_reserved\":false,\"is_present\":true},\"operation\":\"EQUALS\",\"value\":6,\"type\":\"NUMBER\"}]}],\"doc_conditions\":[],\"offset\":0,\"max_results\":1000,\"grouping_operation\":\"AND\"}";
        assertEquals(expected, document.toJson());
    }



    @Test
    public void andGroupFromJson() {
        //given
        documentQuery.addCondition(QueryCondition.andGroup(
                QueryCondition.compare("x", CompareOperation.EQUALS, 5),
                QueryCondition.compare("x", CompareOperation.EQUALS, 6)
        ));

        //when
        DocumentQuery from = DocumentQuery.fromElement(documentQuery.toDocument());

        //then
        List<QueryCondition> attributeConditions = from.getAttributeConditions();
        QueryCondition condition = attributeConditions.get(0);
        assertEquals(GroupOperation.AND, condition.getOperation());
        assertTrue(condition.getValue() instanceof QueryCondition[]);
        assertEquals(2, ((QueryCondition[])condition.getValue()).length);
    }

}

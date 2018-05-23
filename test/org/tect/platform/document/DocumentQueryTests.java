package org.tect.platform.document;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DocumentQueryTests extends BaseDaoTest {

    @Test
    public void byId() {
        //given
        JsonDocument d1 = new JsonDocument();
        dao.saveDocument(d1);

        JsonDocument d2 = new JsonDocument();
        dao.saveDocument(d2);

        //when
        DocumentQuery q = new DocumentQuery();
        q.documentIdIn(d2.getPersistedId());

        //then
        List<JsonDocument> documents = dao.query(q);
        Assert.assertEquals(1, documents.size());
        Assert.assertEquals(d2.getPersistedId(), documents.get(0).getPersistedId());
    }

    @Test
    public void testEquals() {
        //given
        JsonDocument d1 = new JsonDocument();
        d1.set("v1", "John");
        dao.saveDocument(d1);

        JsonDocument d2 = new JsonDocument();
        d2.set("v1", "John2");
        dao.saveDocument(d2);

        //when
        DocumentQuery q = new DocumentQuery();
        q.addCondition(QueryCondition.compare("v1", CompareOperation.EQUALS, "John"));


        List<JsonDocument> documents = dao.query(q);
        Assert.assertEquals(1, documents.size());
    }

    @Test
    public void testAnd() {
        //given
        JsonDocument d1 = new JsonDocument();
        d1.set("v1", "John");
        d1.set("v2", "Summers");
        dao.saveDocument(d1);

        JsonDocument d2 = new JsonDocument();
        d2.set("v1", "John");
        d2.set("v2", "Summeton");
        dao.saveDocument(d2);

        //when
        DocumentQuery q = new DocumentQuery();
        q.addCondition(QueryCondition.compare("v1", CompareOperation.EQUALS, "John"));
        q.addCondition(QueryCondition.compare("v2", CompareOperation.EQUALS, "Summers"));


        List<JsonDocument> documents = dao.query(q);
        Assert.assertEquals(1, documents.size());
    }
}

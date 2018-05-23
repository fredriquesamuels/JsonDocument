package org.tect.platform.document;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocumentQueryStringTests {

    private DocumentQuery query;
    private QueryContext context;

    @Before
    public void setUp() throws Exception {
        this.query = new DocumentQuery();
        this.context = new QueryContext();
    }

    @Test
    public void testDefault() {
        assertQuery("From DocumentHbm doc ");
    }

    @Test
    public void docIdIn() {
        //given
        query.documentIdIn(5L);

        //then
        assertQuery("From DocumentHbm doc WHERE (doc.id in (:var1))");
    }

    @Test
    public void docType() {
        //given
        query.documentTypeIn("someType");

        //then
        assertQuery("From DocumentHbm doc WHERE (doc.type in (:var1))");
    }



    @Test
    public void attribute_Equals() {
        //given
        query.addCondition(QueryCondition.compare("v1", CompareOperation.EQUALS, "r"));

        //then
        assertQuery("From DocumentHbm doc WHERE   ((doc.id in (select a.documentId from AttributeHbm a where a.type = :var2 AND a.text = :var3 AND a.name = :var1)))");
    }

    @Test
    public void attribute_and() {
        //given
        query.addCondition(QueryCondition.andGroup(
                QueryCondition.compare("v1", CompareOperation.EQUALS, "r"),
                QueryCondition.compare("v2", CompareOperation.EQUALS, "e")));

        //then
        assertQuery("From DocumentHbm doc WHERE   (((doc.id in (select a.documentId from AttributeHbm a where a.type = :var2 AND a.text = :var3 AND a.name = :var1)) ) AND ((doc.id in (select a.documentId from AttributeHbm a where a.type = :var5 AND a.text = :var6 AND a.name = :var4))))");
    }

    private void assertQuery(String expected) {
        assertEquals(expected, query.generateHblQuery().getQueryString());
    }
}

package org.tect.platform.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DocumentQuery {

    private final List<QueryCondition> attributeConditions;
    private final List<QueryCondition> docConditions;
    private int maxResults = 1000;
    private int offset = 0;
    private GroupOperation documentAttributeGroupingOperation = GroupOperation.AND;

    public static final String DOC_TYPE = "org.tect.platform.document.query";

    public DocumentQuery() {
        this.attributeConditions = new ArrayList<>();
        this.docConditions = new ArrayList<>();
    }

    public final DocumentQuery addCondition(QueryCondition condition) {
        this.attributeConditions.add(condition);
        return this;
    }

    public final int getMaxResults() {
        return maxResults;
    }

    public final int getOffset() {
        return offset;
    }

    public final boolean containsAttributeConditions() {
        return attributeConditions.stream()
                .filter(c -> c.isAttributeCondition())
                .count() > 0;
    }

    public final DocumentQuery setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    public final DocumentQuery setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public final DocumentQuery documentTypeIn(String ...types) {
        docConditions.add(QueryCondition.documentTypeIn(types));
        return this;
    }

    public final DocumentQuery documentIdIn(Long ... ids) {
        docConditions.add(QueryCondition.documentIdIn(ids));
        return this;
    }

    public final DocumentQuery setDocumentAttributeGrouping(GroupOperation operation) {
        this.documentAttributeGroupingOperation = operation;
        return this;
    }

    final DocumentHibernateQuery generateHblQuery() {
        StringBuilder builder = createBaseHbl(this);
        QueryContext queryContext = new QueryContext();

        buildHblConditions(this, builder, queryContext);

        String hql = builder.toString();
        Map<String, Object> paramsMap = queryContext.getParamsMap();

        printHql(hql, paramsMap);

        return new DocumentHibernateQuery() {
            @Override
            public Map<String, Object> getParamsMap() {
                return paramsMap;
            }

            @Override
            public String getQueryString() {
                return hql;
            }
        };
    }


    private void printHql(String hql, Map<String, Object> paramsMap) {
        String debug = hql;
        for(Map.Entry<String, Object> s : paramsMap.entrySet()) {
            debug = debug.replace(":" + s.getKey(), "'" + s.getValue().toString() + "'");
        }
    }


    GroupOperation getDocumentAttributeGroupingOperation() {
        return documentAttributeGroupingOperation;
    }

    List<QueryCondition> getAttributeConditions() {
        return attributeConditions;
    }

    List<QueryCondition> getDocConditions() {
        return docConditions;
    }


    private void buildHblConditions(DocumentQuery q, StringBuilder builder, QueryContext queryContext) {

        // process doc fields
        QueryCondition[] docFieldConditions = q.getDocConditions()
                .toArray(new QueryCondition[]{});
        String docFieldQueryString = QueryCondition.andGroup(docFieldConditions)
                .generateString(queryContext);
        boolean hasDocFieldConditions = docFieldQueryString != null && !docFieldQueryString.isEmpty();


        //process attribute conditions
        QueryCondition[] conditions = q.getAttributeConditions()
                .toArray(new QueryCondition[]{});
        String attrQueryString = QueryCondition.andGroup(conditions)
                .generateString(queryContext);
        boolean hasAttrConditions = attrQueryString != null && !attrQueryString.isEmpty();


        if(hasDocFieldConditions || hasAttrConditions) {
            builder.append("WHERE ");

            if(hasDocFieldConditions) {
                builder.append(docFieldQueryString);
            }


            /**
             select doc.id from document doc where
             doc.id in (select a.document_id from attribute a where (a.name='key' and a.text='John2')) and
             doc.id in (select a.document_id from attribute a where a.name='key2' and a.text="Summerson");
             */

            if(hasAttrConditions) {
                String s = String.format(" %1$s %2$s",
                        hasDocFieldConditions ? q.getDocumentAttributeGroupingOperation().name() : "",
                        attrQueryString);
                builder.append(s);
            }
        }
    }

    private StringBuilder createBaseHbl(DocumentQuery q) {
        return new StringBuilder(String.format("From DocumentHbm doc "));
    }

    public JsonDocument toDocument() {
        JsonDocument persistedDocument = new JsonDocument(DOC_TYPE);

        JsonArray attributeConditionsArray = persistedDocument.createArray("attribute_conditions");
        for (QueryCondition c :attributeConditions) {
            JsonElement element = attributeConditionsArray.createElement();
            c.populate(element);
        }

        JsonArray docConditionsArray = persistedDocument.createArray("doc_conditions");
        for (QueryCondition c : docConditions) {
            JsonElement element = docConditionsArray.createElement();
            c.populate(element);
        }

        persistedDocument.set("offset", Long.valueOf(getOffset()));
        persistedDocument.set("max_results", Long.valueOf(getMaxResults()));
        persistedDocument.set("grouping_operation", getDocumentAttributeGroupingOperation().name());

        return persistedDocument;
    }

    public static DocumentQuery fromElement(JsonElement document) {
        return new JsonBasedDocumentQuery(document);
    }
}

package org.tect.platform.document;

import org.hibernate.Query;
import org.tect.platform.document.hibernate.AbstractHibernateDatabase;
import org.tect.platform.document.hibernate.DbCredentials;
import org.tect.platform.jsan.JSANMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentDatabase extends AbstractHibernateDatabase {

    public DocumentDatabase(DbCredentials credentials) {
        super(credentials, DocumentHbm.class, AttributeHbm.class);
        new SchemaUpdater(credentials).update();
    }

    public void saveDocument(JsonDocument document) {
        DocumentHbm hbm = createDocumentHbm(document);
        saveDocument(document, hbm);

        deletePendingAttributes(document);

        List<AttributeHbm> keyValues = createAttributeHbm(document, hbm);
        saveAttributes(keyValues);
    }

    public void deleteDocument(JsonDocument document) {
        document.clear();
        deletePendingAttributes(document);
        deleteObject(createDocumentHbm(document));
    }

    public JsonDocument getDocument(long id) {
        DocumentHbm hibernate = get(id, DocumentHbm.class);
        if(hibernate==null) {
            return null;
        }
        populateAttributes(hibernate);
        return hibernate.toDocument();

    }

    public final List<JsonDocument> query(DocumentQuery q) {
        return query(q, JsonDocument.class);
    }


    /**
     * Save json document.
     *
     * @param json The json to be saved.
     * @return The id of the document is the database.
     */
    public final long saveJson(String json) {
        JsonDocument document = new JsonDocument();
        document.populateFromJson(json);
        saveDocument(document);
        return document.getPersistedId();
    }

    public final String queryUniqueJson(DocumentQuery q) {
        JsonDocument document = queryUnique(q);
        if(document==null) return null;
        return document.toJson();
    }

    public final List<String> queryJson(DocumentQuery q) {
        return query(q).stream().map(d -> d.toJson()).collect(Collectors.toList());
    }

    public final void updateJson(long id, String json) {
        DocumentQuery q = new DocumentQuery().addCondition(QueryCondition.documentIdIn(id));
        JsonDocument document = queryUnique(q);
        if(document==null) return;
        document.clear();
        document.populateFromJson(json);
        saveDocument(document);
    }

    public final <T extends JsonDocument> List<T> query(DocumentQuery q, Class<T> aClass) {
        DocumentHibernateQuery hq = q.generateHblQuery();
        Function<DocumentHbm, T> transformer = getNodeHibernateTransformer(aClass);

        String hql = hq.getQueryString();
        Map<String, Object> paramsMap = hq.getParamsMap();

        int maxResults = q.getMaxResults();
        int offset = q.getOffset();
        return queryNodes(hql, paramsMap, maxResults, offset, transformer);
    }

    public final JsonDocument queryUnique(DocumentQuery q) {
        return queryUnique(q, JsonDocument.class);
    }

    public final <T extends JsonDocument> T queryUnique(DocumentQuery q, Class<T> aClass) {
        List<T> query = query(q, aClass);
        if(query.size()>1) {
            throw new NonUniqueObjectException(query.size());
        }
        return query.stream().findFirst().orElse(null);
    }

    public final <T extends JsonDocument> List<T> queryNodes(String hql, Map<String, Object> paramsMap,
                                                             int maxResult, int offset, Class<T> aClass) {
        return queryNodes(hql, paramsMap, maxResult, offset, getNodeHibernateTransformer(aClass));
    }

    <T extends JsonDocument> List<T> queryNodes(String hql, Map<String, Object> paramsMap,
                                                int maxResult, int offset,
                                                Function<DocumentHbm, T> transformer) {
        List<Object> results = runNodesHbl(hql, paramsMap, maxResult, offset);

        if(results==null) {
            return Collections.emptyList();
        }

        return createHibernateNodeStream(results)
                .map(transformer::apply)
                .collect(Collectors.toList());

    }

    private DocumentHbm populateAttributes(DocumentHbm hibernate) {
        List<AttributeHbm> attributes = getObjectList(session -> {
            Query query = session.createQuery("from AttributeHbm a where a.documentId = :var ");
            query.setParameter("var", hibernate.getId());
            return query;
        });
        Optional.ofNullable(attributes).ifPresent(al -> hibernate.setAttributes(al));
        return hibernate;
    }

    private void saveAttributes(List<AttributeHbm> keyValues) {
        keyValues.forEach(kv -> {
            upsert(kv);
            kv.attribute.setPersistedId(kv.getId());
        });
    }

    private List<AttributeHbm> createAttributeHbm(JsonDocument document, DocumentHbm hbm) {
        return (List<AttributeHbm>) new JSANMapper(new DocumentJSANNode.Factory(hbm.getId()))
                .hideGroupIds()
                .toKeyValues(new DocumentJSANNode(document));
    }

    private void saveDocument(JsonDocument document, DocumentHbm hbm) {
        upsert(hbm);
        document.setPersistedId(hbm.getId());
    }

    private DocumentHbm createDocumentHbm(JsonDocument document) {
        DocumentHbm hbm = new DocumentHbm();
        hbm.update(document);
        return hbm;
    }

    private void deletePendingAttributes(JsonDocument document) {
        document.forEachDelete( a -> {
            AttributeHbm hbm = get(a.getPersistedId(), AttributeHbm.class);
            deleteObject(hbm);
        });
        document.clearDeleteList();
    }

    private Stream<DocumentHbm> createHibernateNodeStream(List<Object> results) {
        Function<Object, DocumentHbm> toHibernate = o -> {
            if(o.getClass().isArray()) {
                return (DocumentHbm) ((Object[])o)[0];
            }
            return (DocumentHbm) o;
        };

        List<DocumentHbm> DocumentHbmList = results.stream()
                .map(toHibernate)
                .map(h -> populateAttributes(h))
                .collect(Collectors.toList());
        TreeSet<DocumentHbm> treeSet = new TreeSet<>(Comparator.comparing(DocumentHbm::getId));
        treeSet.addAll(DocumentHbmList);
        return treeSet.stream();
    }

    <T extends JsonDocument> Function<DocumentHbm, T> getNodeHibernateTransformer(Class<T> aClass) {
        return hibernate -> {
            if (aClass.equals(JsonDocument.class)) {
                return aClass.cast(hibernate.toDocument());
            }
            return hibernate.toDocument(aClass);
        };
    }

    private class NonUniqueObjectException extends RuntimeException {
        public NonUniqueObjectException(int size) {
            super(String.format("Expect maximum of 1 result got [%d] instead.", size));
        }
    }

    private List<Object> runNodesHbl(String hql, Map<String, Object> paramsMap, int maxResult, int offset) {

        return getObjectList(session -> {
            Query query = session.createQuery(hql);
            query.setMaxResults(maxResult);
            query.setFirstResult(offset);
            paramsMap.forEach((s, o) -> {
                if(o.getClass().isArray()) {
                    query.setParameterList(s, (Object[]) o);
                } else {
                    query.setParameter(s, o);
                }
            });
            return query;
        });
    }

}

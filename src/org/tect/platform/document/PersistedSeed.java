package org.tect.platform.document;

import java.util.List;

public class PersistedSeed {
    private static final String SEED_ATTRIBUTE = "seed";

    private DocumentDatabase dao;
    private String type;

    public PersistedSeed(DocumentDatabase dao, String type) {
        this.dao = dao;
        this.type = type;
    }

    public synchronized final long generate() {
        DocumentQuery readQuery = new DocumentQuery().documentTypeIn(type);
        List<JsonDocument> documents = dao.query(readQuery);
        if(documents.isEmpty()) {
            JsonDocument document = createNewSeed(dao, type);
            return generateNewSeed(document);
        }
        return generateNewSeed(documents.get(0));
    }

    private long generateNewSeed(JsonDocument document) {
        long newSeed = document.getNumberValue(SEED_ATTRIBUTE).get()+1;
        document.set(SEED_ATTRIBUTE, newSeed);
        dao.saveDocument(document);
        return newSeed;
    }

    private static JsonDocument createNewSeed(DocumentDatabase dao, String type) {
        JsonDocument Document = new JsonDocument(type);
        Document.set(SEED_ATTRIBUTE, 0L);
        dao.saveDocument(Document);
        return Document;
    }


}

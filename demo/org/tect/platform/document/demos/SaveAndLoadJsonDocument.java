package org.tect.platform.document.demos;

import org.tect.platform.document.DocumentDatabase;
import org.tect.platform.document.DocumentQuery;
import org.tect.platform.document.JsonDocument;
import org.tect.platform.document.QueryCondition;
import org.tect.platform.document.hibernate.DbCredentials;
import org.tect.platform.document.hibernate.DbTarget;

import java.util.List;

public class SaveAndLoadJsonDocument  {

    public static void main(String[] args) {
        /**
         * Create new credentials for the database.
         */
        DbCredentials credentials = DbCredentials.builder(DemoUtils.DB_URL, DbTarget.DRIVER_SQLITE).build();

        /**
         * Create a connection to the database.
         */
        DocumentDatabase database = new DocumentDatabase(credentials);

        /**
         * Create a new document for testing.
         */
        JsonDocument document = new JsonDocument();
        document.set("name", "Peter");

        /**
         * Print current state to output.
         */
        System.out.println();
        System.out.println("Document ID? : " +  document.getPersistedId());
        System.out.println("Name : " +  document.getTextValue("name").get());
        System.out.println("Is Saved? : " +  document.isPersisted());

        /**
         * Save document
         */
        System.out.println();
        System.out.println("Saving...");
        database.saveDocument(document);
        System.out.println("Done saving");

        /**
         * Check the document after saving.
         */
        System.out.println();
        System.out.println("Document ID? : " +  document.getPersistedId());
        System.out.println("Is Saved? : " +  document.isPersisted());

        /**
         * Read the document from the database
         */
        DocumentQuery query = new DocumentQuery()
                .addCondition(QueryCondition.documentIdIn(document.getPersistedId()));
        JsonDocument read = database.queryUnique(query);
        List<JsonDocument> reads = database.query(query);

        /**
         * Print document read from storage.
         */
        System.out.println();
        System.out.println("Document ID? : " +  read.getPersistedId());
        System.out.println("Name : " +  document.getTextValue("name").get());
        System.out.println("Is Saved? : " +  read.isPersisted());


    }
}

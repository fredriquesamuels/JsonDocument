package org.tect.platform.document.demos;

import org.tect.platform.document.CompareOperation;
import org.tect.platform.document.DocumentDatabase;
import org.tect.platform.document.DocumentQuery;
import org.tect.platform.document.QueryCondition;
import org.tect.platform.document.hibernate.DbCredentials;
import org.tect.platform.document.hibernate.DbTarget;

import java.util.List;

public class SaveAndQueryJsonString {
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
         * Save json.
         */
        String json = "{\"name\" :\"Mike\", \"age\": 24 }";
        System.out.println("Saving JSON string : " + json);
        long id = database.saveJson(json);

        /**
         * Create a query to find documents with a specific name.
         */
        DocumentQuery query = new DocumentQuery();
        query.addCondition(QueryCondition.compare("name", CompareOperation.EQUALS, "Mike"));

        /**
         * Query and print results.
         */
        List<String> results = database.queryJson(query);

        System.out.println("Query results: " + json);
        results.forEach(s -> System.out.println(s));

        /**
         * Update the json document
         */
        database.updateJson(id, "{\"name\" :\"Mike\", \"age\": 31 }");

        /**
         * Query and print results.
         */
        List<String> updatedResults = database.queryJson(query);

        System.out.println("Updated query results: " + json);
        updatedResults.forEach(s -> System.out.println(s));
    }
}

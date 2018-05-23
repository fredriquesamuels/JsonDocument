package org.tect.platform.document.demos;

import org.tect.platform.document.JsonDocument;

public class JsonStrings {
    public static void main(String[] args) {
        /**
         * Test JSON
         */
        String json = "{ \"id\" : 2, \"names\" : [\"Bobby\", \"Kevin\"] }" ;

        /**
         * Create a new document.
         */
        JsonDocument document = new JsonDocument();

        /**
         * Update document with json data
         */
        document.populateFromJson(json);

        /**
         * Print data.
         */
        System.out.println("ID : " + document.getNumberValue("id").get());
        System.out.println("Names : " + document.getNumberValue("id").get());
    }
}

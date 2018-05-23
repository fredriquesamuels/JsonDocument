package org.tect.platform.document;

import org.junit.After;
import org.junit.Before;
import org.tect.platform.document.hibernate.DbCredentials;
import org.tect.platform.document.hibernate.DbTarget;

import java.io.File;

public class BaseDaoTest {

    static final String DB_FILE = "documents-test.db";

    protected JsonDocument document;
    protected DocumentDatabase dao;

    @Before
    public void setUp() throws Exception {
        document = new JsonDocument();
        dao = new DocumentDatabase(new DocumentDatabaseTest.TestCredentials());
    }

    @After
    public void tearDown() throws Exception {
        new File(DB_FILE).delete();
    }

    public static class TestCredentials extends DbCredentials {

        @Override
        public String getUrl() {
            return "jdbc:sqlite:"+DB_FILE;
        }

        @Override
        public String getUser() {
            return "";
        }

        @Override
        public String getPassword() {
            return "";
        }

        @Override
        public String getDriver() {
            return DbTarget.DRIVER_SQLITE.getDriverName();
        }
    }
}

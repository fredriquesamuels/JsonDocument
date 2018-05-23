package org.tect.platform.document;


import org.tect.platform.document.hibernate.DbCredentials;
import org.tect.platform.document.hibernate.DbTarget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class SchemaUpdater {

    private final Connection connection;
    private final DbCredentials credentials;

    public SchemaUpdater(DbCredentials credentials) {
        this.credentials = credentials;
        this.connection = getConnection(credentials);
    }

    public void update() {
        if(!tableExists("version")) {
            createCleanTables();
        }
    }

    private void createCleanTables() {
        InputStream stream = null;
        Statement statement = null;
        try {
            stream = getSqlStream();
            String sql = readInputStreamAsString(stream);
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(stream);
            close(statement);
        }
    }

    private void close(Statement statement) {
        if(statement!=null) {
            try {
                statement.close();
            } catch (SQLException e) {
                //ignore
            }
        }
    }

    private void close(InputStream stream) {
        if(stream!=null) {
            try {
                stream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private InputStream getSqlStream() {
        InputStream stream = null;
        if(DbTarget.DRIVER_SQLITE.getDriverName().equals(credentials.getDriver())) {
            stream = SchemaUpdater.class.getResourceAsStream("/org/tect/platform/document/schema/sqlite_v1.ddl");
        } else if(DbTarget.DRIVER_MYSQL.getDriverName().equals(credentials.getDriver())) {
            stream = SchemaUpdater.class.getResourceAsStream("/org/tect/platform/document/schema/mysql_v1.ddl");
        }
        return stream;
    }

    public static String readInputStreamAsString(InputStream var0) {
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            long l = 0L;
            String s;
            for(bufferedReader = new BufferedReader(new InputStreamReader(var0, "UTF-8")); (s = bufferedReader.readLine()) != null; builder.append(s)) {
                ++l;
                if (l > 1L) {
                    builder.append('\n');
                }
            }
            return builder.toString();
        } catch (IOException var10) {
            var10.printStackTrace();
        } finally {
            close(bufferedReader);
        }

        return null;
    }

    private boolean tableExists(String tableName) {
        try {
            ResultSet resultSet = null;

            try {
                DatabaseMetaData metaData = this.connection.getMetaData();
                resultSet = metaData.getTables(this.connection.getCatalog(), "", tableName, (String[])null);
                if (resultSet == null) {
                    return false;
                } else {
                    do {
                        if (!resultSet.next()) {
                            return false;
                        }
                    } while(!tableName.equalsIgnoreCase(resultSet.getString("TABLE_NAME")));

                    return true;
                }
            } finally {
                close(resultSet);
            }
        } catch (SQLException var9) {
            throw new RuntimeException(var9);
        }
    }

    private static void close(AutoCloseable resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
        } catch (Exception e) {
        }
    }

    private Connection getConnection(DbCredentials credentials) {
        Connection connection;
        try {
            Class.forName(credentials.getDriver());
            connection = DriverManager.getConnection(credentials.getUrl(), credentials.getUser(), credentials.getPassword());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return connection;
    }

    private final int getCurrentVersion(Connection connection) {
        String sql = "select version from version";
        Statement statement = null;
        ResultSet resultSet = null;

        int version;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet == null || !resultSet.next()) {
                throw new RuntimeException("Couldn't determine current version");
            }
            version = resultSet.getInt("version");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(resultSet);
            close(statement);
        }

        return version;
    }
}

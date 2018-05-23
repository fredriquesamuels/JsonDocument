package org.tect.platform.document.hibernate;

public enum DbTarget {
    DRIVER_MYSQL("com.mysql.jdbc.Driver"),
    DRIVER_SQLITE("org.sqlite.JDBC");

    private final String driverName;

    DbTarget(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }

}

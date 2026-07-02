package com.zrlog.plugin.message;

public class DbPropertiesResponse {

    private String dbProperties;

    public DbPropertiesResponse() {
    }

    public DbPropertiesResponse(String dbProperties) {
        this.dbProperties = dbProperties;
    }

    public String getDbProperties() {
        return dbProperties;
    }

    public void setDbProperties(String dbProperties) {
        this.dbProperties = dbProperties;
    }
}

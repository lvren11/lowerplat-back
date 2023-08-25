package com.ecnu.center.param;

import com.ecnu.center.entity.Databasetablefield;

public class DatatablefiledParam {
    private String userId;
    private String tableId;
    private String tablename;
    private Databasetablefield databasetablefield;

    @Override
    public String toString() {
        return "DatatablefiledParam{" +
                "userId='" + userId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", tablename='" + tablename + '\'' +
                ", databasetablefield=" + databasetablefield +
                '}';
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTablename() {
        return tablename;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTableId() {
        return tableId;
    }

    public Databasetablefield getDatabasetablefield() {
        return databasetablefield;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public void setDatabasetablefield(Databasetablefield databasetablefield) {
        this.databasetablefield = databasetablefield;
    }
}

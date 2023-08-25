package com.ecnu.center.param;

import com.ecnu.center.entity.Databasetablefield;

import java.util.List;

public class DatatableParam {
    private String userId;

    private String tablename;

    private List<Databasetablefield> databasetablefields;

    private String indexlist;
    private String primaryKey;
    private boolean autoIncrementId;

    @Override
    public String toString() {
        return "DatatableParam{" +
                "userId='" + userId + '\'' +
                ", tablename='" + tablename + '\'' +
                ", databasetablefields=" + databasetablefields +
                ", indexlist='" + indexlist + '\'' +
                ", primaryKey='" + primaryKey + '\'' +
                ", autoIncrementId=" + autoIncrementId +
                '}';
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getIndexlist() {
        return indexlist;
    }

    public boolean isAutoIncrementId() {
        return autoIncrementId;
    }

    public void setIndexlist(String indexlist) {
        this.indexlist = indexlist;
    }

    public void setAutoIncrementId(boolean autoIncrementId) {
        this.autoIncrementId = autoIncrementId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTablename() {
        return tablename;
    }

    public List<Databasetablefield> getDatabasetablefields() {
        return databasetablefields;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public void setDatabasetablefields(List<Databasetablefield> databasetablefields) {
        this.databasetablefields = databasetablefields;
    }
}

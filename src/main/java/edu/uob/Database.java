package edu.uob;

import java.io.File;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<Table> list = new ArrayList<Table>();
    private String databaseName;


    public Database() {
    }

    public void setDatabaseName(String dbName){
        databaseName = dbName;
    }

    public String getDatabaseName(){
        return databaseName;
    }

    public List<Table> getTableList(){
        return list;
    }

}


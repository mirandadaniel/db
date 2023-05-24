

package edu.uob;

//Potential classes:

        import java.sql.SQLOutput;
        import java.util.*;

public class UseQuery extends DBCommand {

    String databaseName;
    String queryType;
    String tableName;
    String[] conditions;
    String[] literals;
    String[] columns;
    String[] command;


    public UseQuery(String[] inputCommand) {
        command = inputCommand;

    }

    public boolean tokeniseUse(){
        if(!checkCommandLength(command)){
            return false;
        }
        else {
            setQueryType();
            setDatabaseName(command);
            setTableName();
            setConditions();
            setColumns();
            setLiterals();
            setDBNameInDBCommand();
            return true;
        }
    }

    public boolean checkCommandLength(String[] command){
        int length = command.length;
        if(length != 2){
            return false;
        }
        if(!checkPlainText(command[1])){
            return false;
        }
        return true;
    }

    public boolean checkPlainText(String singularAttribute){
        int length = singularAttribute.length();
        int i = 0;
        while(i < length){
            char c = singularAttribute.charAt(i);
            if(checkIfLetter(c)){
                i++;
            }
            else if(checkIfDigit(c)){
                i++;
            }
            else{
                return false;
            }

        }
        return true;
    }

    public boolean checkIfDigit(char c){
        if(Character.isDigit(c)){
            return true;
        }
        return false;
    }

    public boolean checkIfLetter(char c){
        if(Character.isLetter(c)){
            return true;
        }
        return false;
    }

    public void setQueryType(){
        queryType = "USE";
    }

    public void setDatabaseName(String[] command){
        databaseName = command[1];
    }

    public void setDBNameInDBCommand(){
        DBCommand.databaseName = databaseName;
    }

    public String getDatabaseName(){
        return databaseName;
    }

    public void setTableName(){
        tableName = null;
    }

    public String getTableName(){
        return tableName;
    }

    public void setConditions(){
        conditions = null;
    }

    public void setLiterals(){
        literals = null;
    }

    public void setColumns(){
        columns = null;
    }




}
package edu.uob;

import com.sun.source.tree.UsesTree;

public class AlterQuery extends DBCommand {
    String[] command;
    String alterationType;
    String attribute;
    String tableName;

    public AlterQuery(String[] passInCommand){
        command = passInCommand;
    }

    public boolean parseAlter(){
        int length = command.length;
        if(length != 5){
            return false;
        }
        if(checkForTable()) {
            if (tableName()) {
                if(checkAlterationType()) {
                    if(checkAttribute()) {
                        setAttribute(command[4]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkAttribute(){
        if(checkPlainText()){
            return true;
        }
        return false;
    }


    public boolean checkPlainText(){
        String attribute = command[4];
        int length = attribute.length();
        int i = 0;
        while(i < length){
            char c = attribute.charAt(i);
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

    public boolean checkAlterationType(){
        String alteration = command[3].toUpperCase();
        if(alteration.equals("ADD")){
            setAlterationType(alteration);
            return true;
        }
        else if(alteration.equals("DROP")){
            setAlterationType(alteration);
            return true;
        }
        else {
            return false;
        }
    }

    public void setAlterationType(String alteration){
        alterationType = alteration;
    }

    public String getAlterationType(){
        return alterationType;
    }


    public void setAttribute(String theAttribute){
        attribute = theAttribute;
    }

    public String getAttribute(){
        return attribute;
    }



    public boolean checkForTable(){
        String table = command[1].toUpperCase();
        if(table.equals("TABLE")){
            return true;
        }
        return false;
    }

    public boolean tableName(){
        String theTableName = command[2];
        int length = theTableName.length();
        int i = 0;
        while(i < length){

            char c = theTableName.charAt(i);
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
        setTableName(theTableName);
        return true;
    }

    public void setTableName(String tbName){
        tableName = tbName;
    }

    public String getTableName(){
        return tableName;
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

}

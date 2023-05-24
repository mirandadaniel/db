

package edu.uob;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateQuery extends DBCommand {

    String[] command;
    String queryType;
    String databaseName;
    String tableName;
    List<String> columnHeadings = new ArrayList<>();
    String bracketString;
    int columns;
    Boolean hasAttributes;

    public CreateQuery(String[] passInCommand) {
        command = passInCommand;
    }

    public boolean parseCreate(){
        if(databaseOrTable()){
            return true;
        }
        else{
            return false;
        }
    }


    public boolean databaseOrTable(){
       String upper = command[1].toUpperCase();
       if(upper.equals("DATABASE")){
           if(createDatabase()) {
               setQueryTypeToDB();
               return true;
           }

       }
       else if(upper.equals("TABLE")){
           if(createTable()){
               setQueryTypeToTable();
               return true;
           }
       }
       return false;
    }

    public boolean createDatabase(){
        int length = command.length;
        if(length != 3) {
            return false;
        }
        if(! checkPlainText()){
            return false;
        }
        else{
            setDatabaseName();
            setDatabaseNameDBC();
            databaseName = getDatabaseName();
            return true;
       }
    }

    public boolean checkIfNoAttributes(){
        int length = command.length;
        if(length == 3){
            return true;
        }
        return false;
    }

    public boolean createTable(){
        if(checkIfNoAttributes()) {
            if (checkPlainText()) {
                setTableName();
                hasAttributes = false;
                return true;
            }
        }
        else if(createTableWithAttributes()){
                setTableName();
                hasAttributes = true;
            return true;
        }
       return false;
    }

    public boolean createTableWithAttributes(){
        splitOnBrackets();
        String sansBrackets = removeBrackets();
        String[] innerBrackets = sansBrackets.split(",");
        int size = innerBrackets.length;
        if(size == 1){
            if(justOneAttribute(innerBrackets[0])){
                setNoOfColumns();
                setColumnHeadings(innerBrackets[0], 0);
                return true;
            }
        }
        else if(size > 1){
            if(moreThanOneAttribute(innerBrackets)){
                setNoOfColumns();
                int length = innerBrackets.length;
                for(int x = 0; x < length; x++){
                    setColumnHeadings(innerBrackets[x], x);
                }
                return true;
            }
        }
        return false;
    }

    public boolean moreThanOneAttribute(String[] innerBrackets){
        if(checkPlainTextMultiAttributes(innerBrackets)){
            return true;
        }
        return false;
    }

    public boolean checkPlainTextMultiAttributes(String[] innerBrackets){
        String[] plainText = innerBrackets;
        int length = plainText.length;
        String[] trimmedPT = new String[length];
        for(int k = 0; k < length; k++){
            trimmedPT[k] = plainText[k].trim();
        }


        int i = 0;
        int j = 0;
        for(i = 0; i < length; i++){
            j = 0;
            String indiChars = trimmedPT[i];
            int length2 = indiChars.length();
            while (j < length2) {
                char c = indiChars.charAt(j);
                if (checkIfLetter(c)) {
                    j++;
                } else if (checkIfDigit(c)){
                    j++;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean justOneAttribute(String singularAttribute){
        if(checkPlainText1Attribute(singularAttribute)){
            return true;
        }
        return false;
    }

    public boolean checkPlainText1Attribute(String singularAttribute){
        int length = singularAttribute.length();
        int i = 0;
        while(i < length){
            char c = singularAttribute.charAt(0);
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


    public String removeBrackets(){
        int length = bracketString.length();
        String newString = bracketString.substring(1, length-1);
        return newString;
    }

    public String arrayToString(){
        int length = command.length;
        String strCommand = String.join(" ", command);
        return strCommand;
    }

    public void splitOnBrackets() {
        String str = arrayToString();
        Pattern pt = Pattern.compile("[\\(\\[]([^\\]\\)]*)[\\]\\)]");
        Matcher m = pt.matcher(str);
        while (m.find()) {
            bracketString = m.group(0);
        }
    }

    public boolean checkPlainText(){
       String[] plainText = splitDBName();
       int length = plainText.length;
       int i = 0;
       while(i < length){
           String indiChar = plainText[i];
           char c = indiChar.charAt(0);
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

    public String[] splitDBName(){
        String wordToCheck = command[2];
        String[] checkEachChar = wordToCheck.split("");
        return checkEachChar;
    }


    public void setQueryTypeToDB(){
        queryType = "CREATE DATABASE";
    }

    public void setQueryTypeToTable(){
        queryType = "CREATE TABLE";
    }

    public String getQueryType(){
        return queryType;
    }

    public void setDatabaseName(){
       databaseName = command[2];
    }

    public String getDatabaseName(){
        return databaseName;
    }

    public void setDatabaseNameDBC(){
       DBCommand.databaseName = databaseName;
    }

    public String getDatabaseNameDBC(){
        return DBCommand.databaseName;
    }

    public void setTableName(){
        tableName = command[2];
    }

    public String getTableName(){
        return tableName;
    }

    public void setNoOfColumns(){
        columns = columnHeadings.size();
    }

    public List<String> getColumnHeadings(){
        return columnHeadings;
    }

    public void setColumnHeadings(String col1, int i){
       columnHeadings.add(i, col1);
    }




}
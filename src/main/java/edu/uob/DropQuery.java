package edu.uob;

public class DropQuery extends DBCommand{
    String[] command;
    String dropType;
    String thingToDrop;

    public DropQuery(String[] passInCommand){
        command = passInCommand;
    }

    public boolean parseDrop(){
        int length = command.length;
        if(length != 3){
            return false;
        }
        if(checkDBorTable()){
            if(checkPlainText()) {
                setThingToDrop();
                return true;
            }
        }
        return false;
    }

    public void setThingToDrop(){
        thingToDrop = command[2];
    }

    public String getThingToDrop(){
        return thingToDrop;
    }

    public boolean checkPlainText(){
        String attribute = command[2];
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


    public boolean checkDBorTable(){
        String type = command[1].toUpperCase();
        if(type.equals("DATABASE")){
            setDropType(type);
            return true;
        }
        else if(type.equals("TABLE")){
            setDropType(type);
            return true;
        }
        return false;
    }

    public void setDropType(String dropTypeIs){
        dropType = dropTypeIs;
    }

    public String getDropType(){
       return dropType;
    }

    public String getDatabaseNameDBC(){
        return DBCommand.databaseName;
    }

}

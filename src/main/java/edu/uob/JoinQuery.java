package edu.uob;

public class JoinQuery extends DBCommand {
    String[] command;

    public JoinQuery(String[] passInCommand){
        command = passInCommand;
    }


    public boolean parseJoin(){
        int length = command.length;
        if(length != 8){
            return false;
        }
        if(tableName(command[1])){
            if(andCheck(command[2])){
                if(tableName(command[3])) {
                    if(onCheck()) {
                        if(checkAttribute(command[5])) {
                            if(andCheck(command[6])) {
                                if(checkAttribute(command[7])) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean checkAttribute(String toCheck){
        if(checkPlainText(toCheck)){
            return true;
        }
        return false;
    }

    public boolean checkPlainText(String toCheck){
        String attribute = toCheck;
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

    public boolean onCheck(){
        String upper = command[4].toUpperCase();
        if(upper.equals("ON")){
            return true;
        }
        return false;
    }

    public boolean andCheck(String toCheck){
        String upper = toCheck.toUpperCase();
        if(upper.equals("AND")){
            return true;
        }
        return false;
    }

    public boolean tableName(String name){
        String tableName = name;
        int length = tableName.length();
        int i = 0;
        while(i < length){

            char c = tableName.charAt(i);
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

}


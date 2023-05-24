package edu.uob;

import java.util.Objects;

public class SelectQuery extends DBCommand {
    String[] command;
    String attribute;
    String theTableName;
    Boolean conditional;
    String noun;
    String operator;
    String condition;


    public SelectQuery(String[] passInCommand, DBServer theDBServer) throws DBExceptions {
        command = passInCommand;
    }

    public boolean parseSelect(){
            if (checkWildAttributeList()) {
                if (checkFrom()) {
                    if (checkTablename()) {
                        if (!checkIfConditonal()) {
                            return true;
                        } else if (checkIfConditonal()) {
                            if (checkIfWhere()) {
                                if (checkValidOperation()) {
                                    noun = removeQuotationMarks(noun);
                                    condition = removeQuotationMarks(condition);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        return false;
        // TODO: throw error
    }

    public String removeQuotationMarks(String word){
            char x = word.charAt(0);
            char y = word.charAt(word.length()-1);
            if(x == '\'' && y == '\''){
                word = word.substring(1, word.length() - 1);
                // TODO: throw exception here?
                return word;
            }
        return word;
    }

    public boolean checkIfWhere(){
        String toUpper = command[4].toUpperCase();
        int length = command.length;
        if(toUpper.equals("WHERE") && (length >= 8)){
            conditional = true;
            return true;
        }
        return false;
    }

    public boolean checkIfConditonal(){
        int length = command.length;
      //  System.out.println(length);
        if(length == 4){
            conditional = false;
            return false;
        }
        else{
            return true;
        }
    }

    public boolean checkTablename(){
        String tableName = command[3];
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
        theTableName = tableName;
        return true;
    }


    public boolean checkFrom(){
        String toUpper = command[2].toUpperCase();
        if(toUpper.equals("FROM")){
            return true;
        }
        return false;
    }

    public boolean checkWildAttributeList(){
        if(Objects.equals(command[1], "*")){
            attribute = "*";
            return true;
        }
        else if(checkPlainText(command[1])){
            attribute = command[1];
            return true;
        }
        return false;
    }



    public boolean checkPlainText(String toCheck){
        int length = toCheck.length();
        int i = 0;
        while(i < length){
            char c = toCheck.charAt(i);
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


    public boolean checkValidOperation(){
        if(checkPlainText(command[5])){
            if(checkOperator(command[6])) {
                if(checkThisValue(command[7])) {
                    noun = command[5];
                    operator = command[6];
                    condition = command[7];
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkOperator(String toCheck){
        String x = toCheck;
        if(x.equals("==")){
            return true;
        }
        else if(x.equals(">")){
            return true;
        }
        else if(x.equals("<")){
            return true;
        }
        else if(x.equals(">=")){
            return true;
        }
        else if(x.equals("<=")){
            return true;
        }
        else if(x.equals("!=")){
            return true;
        }
        else if(x.equals("LIKE")){
            return true;
        }
        return false;
    }

    public boolean checkThisValue(String currentValue){
        if(checkIfNull(currentValue)){
            return true;
        }
        else if(checkIfBooleanLiteral(currentValue)){
            return true;
        }
        else if(checkIfIntegerLiteral(currentValue)){
            return true;
        }
        else if(checkIfFloatLiteral(currentValue)){
            return true;
        }
        else if(checkIfStringLiteral(currentValue)){
            return true;
        }
        return false;
    }


    public boolean checkIfIntegerLiteral(String currentValue){
        char c = currentValue.charAt(0);
        if(checkSign(c)){
            String newString = removeSign(currentValue);
            int length = newString.length();
            for(int i = 0; i < length; i++) {
                char x = newString.charAt(i);
                if (Character.isDigit(x)) {
                }
                else{
                    return false;
                }
            }
            return true;
        }
        else if(Character.isDigit(c)){
            int length = currentValue.length();
            for(int i = 0; i < length; i++) {
                char x = currentValue.charAt(i);
                if (Character.isDigit(x)) {
                }
                else{
                    return false;
                }
                return true;
            }
        }

        return false;
    }

    public String removeSign(String valueToCheck){
        String newString;
        int length = valueToCheck.length();
        newString = valueToCheck.substring(1, length);
        return newString;
    }

    public boolean checkSign(char c){
        if(c == '-' || c == '+'){
            return true;
        }
        return false;
    }



    public boolean checkIfPosNegSignFloat(String valueToCheck, char c){

        if(!checkSign(c)){
            return false;
        }
        String sansSign = removeSign(valueToCheck);

        char y = valueToCheck.charAt(1);
        if(Character.isDigit(y)){
            String[] digitStrings = splitOnDot(sansSign);
            int length = digitStrings.length;

            if(length != 2){
                return false;
            }
            int i = 0;
            while(i < length){
                int localLength = digitStrings[i].length();
                String localString = digitStrings[i];
                int j = 0;
                while(j < localLength){
                    char x = localString.charAt(j);
                    if (Character.isDigit(x)) {
                        j++;
                    }
                    else{
                        return false;
                    }
                }
                i++;
            }
        }
        return true;
    }


    public boolean checkIfFloatLiteral(String valueToCheck){
        char c = valueToCheck.charAt(0);
        if(checkIfPosNegSignFloat(valueToCheck, c)) {
            return true;
        }
        else if(Character.isDigit(c)){
            String[] digitStrings = splitOnDot(valueToCheck);
            int length = digitStrings.length;

            if(length != 2){
                return false;
            }

            if(checkThisDigit(digitStrings)){
                return true;
            }
        }

        return false;
    }

    public boolean checkThisDigit(String[] digitStrings){
        int i = 0;
        int length = digitStrings.length;
        while(i < length){
            int localLength = digitStrings[i].length();
            String localString = digitStrings[i];
            int j = 0;
            while(j < localLength){
                char x = localString.charAt(j);
                if (Character.isDigit(x)) {
                    j++;
                }
                else{
                    return false;
                }
            }
            i++;
        }
        return true;
    }

    public String[] splitOnDot(String toSplit){
        String[] dotSplit = toSplit.split("\\.");
        return dotSplit;
    }


    public boolean checkIfNull(String valueToCheck){
        String toUpper = valueToCheck.toUpperCase();
        if(toUpper.equals("NULL")){
            return true;
        }
        return false;
    }

    public boolean checkIfBooleanLiteral(String valueToCheck){
        String toUpper = valueToCheck.toUpperCase();
        if(toUpper.equals("TRUE")){
            return true;
        }
        if(toUpper.equals("FALSE")){
            return true;
        }
        return false;
    }

    public boolean checkIfStringLiteral(String valueToCheck) {
        int length = valueToCheck.length();
        int j = 1;

        char x = valueToCheck.charAt(0);
        char y = valueToCheck.charAt(length-1);
        if(x != '\'' || y != '\''){
            return false;
        }

        while (j < length-1) {
            char c = valueToCheck.charAt(j);
            if(c == ' '){
                j++;
            }
            else if(checkIfLetter(c)) {
                j++;
            } else if(checkIfSymbol(c)) {
                j++;


            } else {
                return false;
            }
        }
        return true;
    }

    public boolean checkIfSymbol(char c){
        String specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.";
        String s = String.valueOf(c);
        if(specialChars.contains(s)){
            return true;
        }
        return false;
    }
}

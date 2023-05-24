package edu.uob;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertQuery extends DBCommand {
    String[] command;
    String bracketString;
    String[] attributes;
    String tableName;

    public InsertQuery(String[] inputCommand){
        command = inputCommand;

    }

    public boolean parseInsert(){

        String[] plainText = command;
        int length = plainText.length;
        if(length < 4){
            return false;
        }

        String[] trimmedPT = new String[length];
        for(int k = 0; k < length; k++){
            trimmedPT[k] = plainText[k].trim();
        }
        command = trimmedPT;

        if(!runCommand()){
            return false;
        }
        return true;
    }


    public boolean runCommand(){
        if(checkSyntax()){
            if(checkPlainTextTableName()){
                setTableName(command[2]);
                if(checkValues()){
                    if(splitOnBrackets()){
                        removeBrackets();
                        if(identifyAttributes()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public String getDatabaseNameDBC(){
        return DBCommand.databaseName;
    }

    public boolean identifyAttributes(){
        String sansBrackets = removeBrackets();
        String[] innerBrackets = splitOnComma(sansBrackets);
        int length = innerBrackets.length;
        attributes = new String[length];
        for(int i = 0; i < length; i++) {
            attributes[i] = innerBrackets[i];
        }
        if(checkIfValidValues()){
            removeQuotationMarks();
            return true;
        }
        return false;
    }

    public void removeQuotationMarks(){
        for(int i = 0; i < attributes.length; i++){
            char x = attributes[i].charAt(0);
            char y = attributes[i].charAt(attributes[i].length()-1);
            if(x == '\'' && y == '\''){
                attributes[i] = attributes[i].substring(1, attributes[i].length() - 1);
            }
        }
    }


    public boolean checkIfValidValues(){
        int length = attributes.length;
        int i = 0;
        while(i < length){
            if(checkThisValue(attributes[i])) {
                i++;
            }
            else{
                return false;
            }
        }
        return true;
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


    public String[] splitOnComma(String sansBrackets){
        String[] innerBrackets = sansBrackets.split(",");
        int length = innerBrackets.length;
        for(int i = 0; i < length; i++){
            innerBrackets[i] = innerBrackets[i].trim();
        }
        return innerBrackets;
    }


    public String[] trimString(String[] innerBrackets){
        String[] plainText = innerBrackets;
        int length = plainText.length;
        String[] trimmedPT = new String[length];
        for(int k = 0; k < length; k++){
            trimmedPT[k] = plainText[k].trim();
        }
        return trimmedPT;
    }


    public String arrayToString(){
        String strCommand = String.join(" ", command);
        return strCommand;
    }

    public boolean checkValues(){
        String upper = command[3].toUpperCase();
        String[] temp = new String[6];
        String[] temp2 = upper.split("");
        for(int i = 0; i < 6; i++){
            temp[i] = temp2[i];
        }

        String strCommand = String.join("", temp);
        if(strCommand.equals("VALUES")){
            return true;
        }
        return false;
    }

    public boolean splitOnBrackets() {
        String str = arrayToString();
        Pattern pt = Pattern.compile("[\\(\\[]([^\\]\\)]*)[\\]\\)]");
        Matcher m = pt.matcher(str);
        while (m.find()) {
            bracketString = m.group(0);
            return true;
        }
        return false;
    }

    public String removeBrackets(){
        int length = bracketString.length();
        String newString = bracketString.substring(1, length-1);
        return newString;
    }

    public boolean checkPlainTextTableName(){
        String[] plainText = splitTableName();
        int length = plainText.length;
        int i = 0;
        while(i < length){
            String tableName = command[2];
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

    public boolean checkSyntax(){
        String upper = command[1].toUpperCase();
        if(!upper.equals("INTO")){
            return false;
        }
        return true;
    }

    public String[] splitTableName(){
        String wordToCheck = command[2];
        String[] checkEachChar = wordToCheck.split("");
        return checkEachChar;
    }

    public void setTableName(String tbName){
        tableName = tbName;
    }

    public String getTableName(){
        return tableName;
    }

}


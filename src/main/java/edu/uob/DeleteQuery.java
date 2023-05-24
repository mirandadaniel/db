package edu.uob;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteQuery extends DBCommand{

    String[] command;
    String[] bStrArr = new String[2];
    String conditionType;
    String theTableName;
    String noun;
    String operator;
    String condition;
    String noun2;
    String operator2;
    String condition2;

    public DeleteQuery(String[] inputCommand) throws DBExceptions.InvalidSyntax {
        command = inputCommand;
        int length = command.length;
        if(!checkCommandLength()){
           throw new DBExceptions.InvalidSyntax();
        }

        for(int k = 0; k < length; k++){
            command[k] = command[k].trim();
        }
    }

    public boolean checkCommandLength(){
        int length = command.length;
        if(length < 5){
            return false;
        }
        return true;
    }

    public boolean parseDelete(){
        if(checkFrom()){
            if(checkTablename()) {
                if(checkWhere()) {
                    if(!checkBracket()) {
                        if(validOperation()) {
                            conditionType = "SINGULAR";
                            return true;
                        }
                    }
                    else if(checkBracket()){
                        int noOfBrackets = checkMultiBrackets();
                        if(noOfBrackets == 1) {
                            if(checkConditionFormat()) {
                                if (ifTwoConditions()) {
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

    public boolean checkConditionFormat() {
        String commandAsStr = arrayToString();
        int length = commandAsStr.length();
        int i = 0;
        int x;
        int y;
        int z;
        int a;
        while (i < length) {
            if (commandAsStr.charAt(i) == '(') {
                x = i;
                while (x < length) {
                    if (commandAsStr.charAt(x) == ')') {
                        y = x;
                        while(y < length){
                            if (commandAsStr.charAt(y) == 'A' && (commandAsStr.charAt(y+1) == 'N') && (commandAsStr.charAt(y+2) == 'D')){
                                z = y;
                                while (z < length) {
                                    if (commandAsStr.charAt(z) == '(') {
                                        a = z;
                                        while (a < length) {
                                            if (commandAsStr.charAt(a) == ')') {
                                                conditionType = "AND";
                                                return true;
                                            }
                                            else{
                                                a++;
                                            }
                                        }
                                    }
                                    else{
                                        z++;
                                    }
                                }
                            }
                            else if(commandAsStr.charAt(y) == 'O' && (commandAsStr.charAt(y+1) == 'R')){
                                z = y;
                                while (z < length) {
                                    if (commandAsStr.charAt(z) == '(') {
                                        a = z;
                                        while (a < length) {
                                            if (commandAsStr.charAt(a) == ')') {
                                                conditionType = "OR";
                                                return true;
                                            }
                                            else{
                                                a++;
                                            }
                                        }
                                    }
                                    else{
                                        z++;
                                    }
                                }
                            }
                            else{
                                y++;
                            }
                            }
                        }
                    else{
                        x++;
                    }
                    }

                }
                else{
                    i++;
                }
            }
        return false;
    }

    public String removeBrackets(String commandString){
        int length = commandString.length();
        String newString = commandString.substring(1, length-1);
        return newString;
    }


    public boolean ifTwoConditions(){
        String commandStr = arrayToString();
        if (splitOnBrackets(commandStr)) {
            String bStr1 = removeBrackets(bStrArr[0]);
            String bStr2 = removeBrackets(bStrArr[1]);
            String[] strArr1 = bStr1.split(" ");
            String[] strArr2 = bStr2.split(" ");
            if (validOpInBrackets1(strArr1) && validOpInBrackets2(strArr2)) {
                removeQuotationMarks1();
                removeQuotationMarks2();
                return true;
            }
        }
        return false;
    }

    public void removeQuotationMarks1(){
            char x = condition.charAt(0);
            char y = condition.charAt(condition.length()-1);
            if(x == '\'' && y == '\''){
                condition = condition.substring(1, condition.length() - 1);
                // TODO: throw exception here?
        }
    }

    public void removeQuotationMarks2(){
        char x = condition2.charAt(0);
        char y = condition2.charAt(condition2.length()-1);
        if(x == '\'' && y == '\''){
            condition2 = condition2.substring(1, condition2.length() - 1);
            // TODO: throw exception here?
        }
    }


    public boolean splitOnBrackets(String stringCommand) {
        String str = stringCommand;
        Pattern pt = Pattern.compile("[\\(\\[]([^\\]\\)]*)[\\]\\)]");
        Matcher m = pt.matcher(str);
       // String[] bStrArray = new String[2];
        int i = 0;
        while(i < 1){
            while (m.find()) {
                bStrArr[i] = m.group(0);
                i++;
            }
        }
        return true;
    }

    public String arrayToString(){
        int length = command.length;
        int arrSize = length - 4;
        String[] justTheBrackets = new String[arrSize];
        int x = 0;
        for(int i = 4; i < length; i++) {
            justTheBrackets[x] = command[i];
            x++;
        }
        String strCommand = String.join(" ", justTheBrackets);
        return strCommand;
    }

    public int checkMultiBrackets(){
        String str = command[4];
        int count = 0;
        int length = str.length();
        for(int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c == '(') {
               count++;
            }
        }
        return count;
    }

    public boolean validOpInBrackets1(String[] theString){
        if(checkPlainText(theString[0])){
            noun = theString[0];
            if(checkOperator(theString[1])) {
                operator = theString[1];
                if(checkThisValue(theString[2])) {
                    condition = theString[2];
                    return true;
                }
            }
        }
        return false;
    }

    public boolean validOpInBrackets2(String[] theString){
        if(checkPlainText(theString[0])){
            noun2 = theString[0];
            if(checkOperator(theString[1])) {
                operator2 = theString[1];
                if(checkThisValue(theString[2])) {
                    condition2 = theString[2];
                    return true;
                }
            }
        }
        return false;
    }


    public boolean validOperation(){
        if(checkPlainText(command[4])){
            if(checkOperator(command[5])) {
                if(checkThisValue(command[6])) {
                    noun = command[4];
                    operator = command[5];
                   condition = command[6];
                   removeQuotationMarks1();
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

    public boolean checkBracket(){
        char c = command[4].charAt(0);
        if(c == '('){
            return true;
        }
        return false;
    }

    public boolean checkWhere(){
        String toUpper = command[3].toUpperCase();
        if(toUpper.equals("WHERE")){
            return true;
        }
        else{
            return false;
        }
    }

// TODO: need to check for '' here? !!!!!!
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

    public boolean checkTablename(){
        String tableName = command[2];
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

    public boolean checkFrom() {
        String toUpper = command[1].toUpperCase();
        if (toUpper.equals("FROM")) {
            return true;
        } else {
            return false;
        }
    }


    public boolean checkThisValue(String currentValue){
        if(checkIfNull(currentValue)){
            return true;
        }
        else if(checkIfBooleanLiteral(currentValue)){
            return true;
        }
        else if(checkIfFloatLiteral(currentValue)){
            return true;
        }
        else if(checkIfIntegerLiteral(currentValue)){
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
            // TODO: throw exception here?
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

package edu.uob;

//Potential classes:

import java.sql.SQLOutput;
import java.util.*;

public class Tokeniser {

    String command;
    String[] finalCommand;
    String commandType;

    public Tokeniser(String inputCommand) throws DBExceptions{
        command = inputCommand;
        if(!checkSemiColon()){
            throw new DBExceptions.MissingSemiColon();
        }
        else if(identifyQueryType()) {
            }
        else{
            throw new DBExceptions.InvalidTokenization();
        }
    }

    public boolean checkSemiColon(){
        if(command.charAt(command.length() - 1) == ';'){
            return true;
        }
        return false;
    }


    public String commandToUpper(){
        String queryCopy;
        queryCopy = command.toUpperCase();
        return queryCopy;
    }

    public String neatenString(){
        String upperCommand = commandToUpper();
        String trimmedCommand = upperCommand.trim();
        return trimmedCommand;
    }

    public String[] splitTheCommand(){
        String stringToSplit = neatenString();
        String[] splitString;
        splitString = stringToSplit.split(" ");
        return splitString;
    }

    public String removeSemiColon(String stringToSplit){
        String sansSemiColon = stringToSplit;
        sansSemiColon = sansSemiColon.substring(0, sansSemiColon.length()-1);
        return sansSemiColon;
    }

    public String[] makeFinalCommand(){
        String stringToSplit = command.trim();
        stringToSplit = removeSemiColon(stringToSplit);
        String[] finalSplitString;
        finalSplitString = stringToSplit.split("\\s* \\s*");
        int length = finalSplitString.length;
        for(int i = 0; i < length; i++){
            finalSplitString[i] = finalSplitString[i].trim();
        }
        return finalSplitString;
    }

    public boolean identifyQueryType() throws DBExceptions {
        String[] splitString = splitTheCommand();
        String trimmedCT = splitString[0].trim();
        String[] finalCommand = makeFinalCommand();

        if(Objects.equals(trimmedCT, "USE")){
            setFinalCommand(finalCommand);
            String ct = "USE";
            setCommandType(ct);
            return true;
        }
        if(Objects.equals(splitString[0], "CREATE")){
            setFinalCommand(finalCommand);
            String ct = "CREATE";
            setCommandType(ct);
            return true;
        }
        if(Objects.equals(trimmedCT, "INSERT")){
            setFinalCommand(finalCommand);
            String ct = "INSERT";
            setCommandType(ct);
            return true;
        }
        if(Objects.equals(splitString[0], "SELECT")){
            String ct = "SELECT";
            setFinalCommand(finalCommand);
            setCommandType(ct);
            return true;
        }
        if(Objects.equals(splitString[0], "DROP")){
            setFinalCommand(finalCommand);
            String ct = "DROP";
            setCommandType(ct);
            return true;
        }
        if(Objects.equals(splitString[0], "ALTER")){
            setFinalCommand(finalCommand);
            String ct = "ALTER";
            setCommandType(ct);
            return true;
        }
        if(Objects.equals(splitString[0], "DELETE")){
            setFinalCommand(finalCommand);
            String ct = "DELETE";
            setCommandType(ct);
            return true;
        } if(Objects.equals(splitString[0], "UPDATE")){
            setFinalCommand(finalCommand);
            String ct = "UPDATE";
            setCommandType(ct);
            return true;
        }if(Objects.equals(splitString[0], "JOIN")){
            throw new DBExceptions.CantHandleCommand();
        }
        else{
            throw new DBExceptions.InvalidQueryType();
        }
    }

    public void setFinalCommand(String[] inputFinalCommand){
        finalCommand = inputFinalCommand;
    }

    public String[] getFinalCommand(){
        return finalCommand;
    }

    public void setCommandType(String ct){
        commandType = ct;
    }

    public String getCommandType(){
        return commandType;
    }

}

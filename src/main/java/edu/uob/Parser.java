package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser {

    String[] theCommand;
    String directory;
    DBServer myDBServer;
    String commandType;
    String dbName;
    File dbDir;
    List<Database> databaseList;
    CreateQuery thisCreate;
    DropQuery thisDrop;
    AlterQuery thisAlter;
    SelectQuery thisSelect;
    DeleteQuery thisDelete;
    UpdateQuery thisUpdate;
    private int tableIndex;
    String info;
    public String returnStatement;

    public Parser(DBServer currentDBServer, String inputCommand, File passInDBDir, List<Database> dbList1) throws DBExceptions {
        databaseList = dbList1;
        dbDir = passInDBDir;
        info = dbDir.getPath();
        directory = dbDir.getPath();
        Tokeniser tokeniser1 = new Tokeniser(inputCommand);
        theCommand = tokeniser1.getFinalCommand();
        commandType = tokeniser1.getCommandType();
        myDBServer = currentDBServer;
        if (!callRelevantClass()) {
            throw new DBExceptions.UnableToExecuteCommand();
        }

    }

    public boolean callRelevantClass() throws DBExceptions {
        if (commandType.equals("USE")) {
            runUseClass();
            returnStatement = " ";
            return true;
        } else if (commandType.equals("INSERT")) {
            runInsertClass();
            returnStatement = " ";
            return true;
        } else if (commandType.equals("CREATE")) {
            if (runCreateClass()) {
                returnStatement = " ";
                return true;
            }
        } else if (commandType.equals("DROP")) {
            if (runDropClass()) {
                returnStatement = " ";
                return true;
            }
        } else if (commandType.equals("ALTER")){
            if (runAlterClass()) {
                returnStatement = " ";
                return true;
            }
        } else if (commandType.equals("SELECT")){
            if (runSelectClass()) {
                return true;
            }
        } else if (commandType.equals("DELETE")){
            if (runDeleteClass()) {
                returnStatement = " ";
                return true;
            }
        } else if (commandType.equals("UPDATE")){
        if (runUpdateClass()) {
            returnStatement = " ";
            return true;
        }
    }


        return false;
    }

    ////////////////////////// UPDATE CLASS /////////////////////////////////////

    public boolean runUpdateClass() throws DBExceptions.TableDoesNotExist, DBExceptions.EmptyTableList, DBExceptions.ColumnDoesntExist, DBExceptions.NoConditionMatchFound, DBExceptions.CantHandleCommand {
        thisUpdate = new UpdateQuery(theCommand);
        if(!thisUpdate.parseUpdate()){
            return false;
        }
        String tableName = thisUpdate.tableName;
        String noun = thisUpdate.noun;
        String operator = thisUpdate.operator;
        String condition = thisUpdate.condition;
        String databaseName = DBCommand.databaseName;
        String[] valuePair = thisUpdate.valuePair;
        if (findRelevantTable(tableName, databaseName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);
            if(thisUpdate.conditionType == "SINGULAR") {
                thisTable.updateConditionMatch(noun, operator, condition, valuePair);
                String localDir = directory + File.separator + databaseName + File.separator + thisTable.getTableName();
               thisTable.saveEditedFile(localDir);
                return true;
            }
            else if(thisUpdate.conditionType == "AND") {
                throw new DBExceptions.CantHandleCommand();
            }
        } else if (readInFile(tableName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);

            if(thisUpdate.conditionType == "SINGULAR") {
                thisTable.updateConditionMatch(noun, operator, condition, valuePair);
                String localDir = directory + File.separator + databaseName + File.separator + thisTable.getTableName();
                thisTable.saveEditedFile(localDir);
                return true;
            }
            else if(thisUpdate.conditionType == "AND") {
                throw new DBExceptions.CantHandleCommand();
            }
        } else {
            throw new DBExceptions.TableDoesNotExist();
        }
        return false;
    }

    ////////////////////////// DELETE CLASS /////////////////////////////////////

    public boolean runDeleteClass() throws DBExceptions.TableDoesNotExist, DBExceptions.EmptyTableList, DBExceptions.CantHandleCommand, DBExceptions.ColumnDoesntExist, DBExceptions.NoConditionMatchFound, DBExceptions.InvalidSyntax {
        thisDelete = new DeleteQuery(theCommand);
        if(!thisDelete.parseDelete()){
            return false;
        }
        String tableName = thisDelete.theTableName;
        String noun = thisDelete.noun;
        String operator = thisDelete.operator;
        String condition = thisDelete.condition;
        String databaseName = DBCommand.databaseName;
        if (findRelevantTable(tableName, databaseName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);
            if(thisDelete.conditionType == "SINGULAR") {
                thisTable.deleteConditionMatch(noun, operator, condition);
                String localDir = directory + File.separator + databaseName + File.separator + thisTable.getTableName();
                thisTable.saveEditedFile(localDir);
                return true;
            }
            else if(thisDelete.conditionType == "AND") {
                throw new DBExceptions.CantHandleCommand();
            }
            return false;
        }
        else if (readInFile(tableName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);
            if(thisDelete.conditionType == "SINGULAR") {
                thisTable.deleteConditionMatch(noun, operator, condition);
                String localDir = directory + File.separator + databaseName + File.separator + thisTable.getTableName();
                thisTable.saveEditedFile(localDir);
                return true;
            }
            else if(thisDelete.conditionType == "AND") {
                throw new DBExceptions.CantHandleCommand();
            }
            return false;
        } else {
            throw new DBExceptions.TableDoesNotExist();
        }
    }


    ////////////////////////// SELECT CLASS /////////////////////////////////////

    public boolean runSelectClass() throws DBExceptions {
        thisSelect = new SelectQuery(theCommand, myDBServer);
        if(!thisSelect.parseSelect()){
            return false;
        }
        String databaseName = DBCommand.databaseName;
        String tableName = thisSelect.theTableName;
        if (findRelevantTable(tableName, databaseName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);
            if(thisSelect.conditional == Boolean.FALSE && thisSelect.attribute == "*") {
                if(thisTable.selectAll()) {
                    returnStatement = thisTable.selectMatches;
                    return true;
                }
            }
            else if(thisSelect.conditional == Boolean.FALSE && thisSelect.attribute != null) {
                if(thisTable.selectSpecificCol(thisSelect.attribute)) {
                    returnStatement = thisTable.selectMatches;
                    return true;
                }
            }
            else if(thisSelect.conditional == Boolean.TRUE && thisSelect.attribute.equals("*")) {
                if(thisTable.selectAllOfCondition(thisSelect.noun, thisSelect.operator, thisSelect.condition)) {
                    returnStatement = thisTable.selectMatches;
                    return true;
                }
            }
            else if(thisSelect.conditional == Boolean.TRUE && thisSelect.attribute != null) {
                if (thisTable.selectOnlyCondition(thisSelect.attribute, thisSelect.operator, thisSelect.condition, thisSelect.noun)) {
                    returnStatement = thisTable.selectMatches;
                    return true;
                }
            }
            return false;
        } else if (readInFile(tableName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);
            if(thisSelect.conditional == Boolean.FALSE && thisSelect.attribute == "*") {
                if(thisTable.selectAll()) {
                    returnStatement = thisTable.selectMatches;
                    return true;
                }
            }
            else if(thisSelect.conditional == Boolean.FALSE && thisSelect.attribute != null) {
                if(thisTable.selectSpecificCol(thisSelect.attribute)) {
                    returnStatement = thisTable.selectMatches;
                    return true;
                }
            }
            else if(thisSelect.conditional == Boolean.TRUE && thisSelect.attribute.equals("*")) {
                if(thisTable.selectAllOfCondition(thisSelect.noun, thisSelect.operator, thisSelect.condition)) {
                    returnStatement = thisTable.selectMatches;
                    return true;
                }
            }
            else if(thisSelect.conditional == Boolean.TRUE && thisSelect.attribute != null) {
                if (thisTable.selectOnlyCondition(thisSelect.attribute, thisSelect.operator, thisSelect.condition, thisSelect.noun)) {
                    returnStatement = thisTable.selectMatches;
                    return true;
                }
            }
            return false;
        } else {
            throw new DBExceptions.TableDoesNotExist();
        }
    }


    ////////////////////////// ALTER CLASS /////////////////////////////////////

    public boolean runAlterClass() throws DBExceptions {
        thisAlter = new AlterQuery(theCommand);
        if(!thisAlter.parseAlter()){
            return false;
        }
        String databaseName = DBCommand.databaseName;
        String tableName = thisAlter.getTableName();

        if (findRelevantTable(tableName, databaseName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);
            if(thisAlter.alterationType.equals("ADD")){
                addCol(thisTable, databaseName);
                return true;
            }
            if(thisAlter.alterationType.equals("DROP")){
                dropCol(thisTable, databaseName);
                return true;
            }
            return false;
        } else if (readInFile(tableName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);
            if(thisAlter.alterationType.equals("ADD")){
                addCol(thisTable, databaseName);
                return true;
            }
            if(thisAlter.alterationType.equals("DROP")){
                dropCol(thisTable, databaseName);
                return true;
            }
            return false;
        } else {
            throw new DBExceptions.TableDoesNotExist();
        }
    }

    public void dropCol(Table thisTable, String databaseName) throws DBExceptions.ColumnDoesntExist {
        thisTable.dropCol(thisAlter.getAttribute());
        String localDir = directory + File.separator + databaseName + File.separator + thisTable.getTableName();
        thisTable.saveEditedFile(localDir);
    }

    public void addCol(Table thisTable, String databaseName){
        thisTable.add1ColAtEnd(thisAlter.getAttribute());
        String localDir = directory + File.separator + databaseName + File.separator + thisTable.getTableName();
        thisTable.saveEditedFile(localDir);
    }


    ////////////////////////// DROP CLASS /////////////////////////////////////

    public boolean runDropClass() throws DBExceptions.TableDoesNotExist, DBExceptions.InvalidCommand, DBExceptions.EmptyTableList {
        thisDrop = new DropQuery(theCommand);
        if(!thisDrop.parseDrop()){
            throw new DBExceptions.InvalidCommand(theCommand);
        }
        if(thisDrop.dropType.equals("TABLE")){
            String tableName = thisDrop.getThingToDrop();
            if(dropTable(tableName)) {
                return true;
            }
            else{
                throw new DBExceptions.TableDoesNotExist();
            }
        }
        if(thisDrop.dropType.equals("DATABASE")){
            String databaseToDrop = thisDrop.getThingToDrop();
            DBCommand.databaseName = databaseToDrop;
            if(dropDatabase1(databaseToDrop)){
                return true;
            }
        }
        return false;
    }


    public boolean dropDatabase1(String databaseToDrop){
        File mainDir = new File(dbDir.getPath());
        ArrayList<String> databasesInDir =  getDatabaseFilenames(mainDir);
        if(findDBToDrop(databaseToDrop, databasesInDir)) {
            deleteThisDB(databaseToDrop);
            return true;
        }
        return false;
    }

    public boolean findDBToDrop(String databaseToDrop, ArrayList<String> databasesInDir){
        int length = databasesInDir.size();
        for(int i = 0; i < length; i++){
            if(Objects.equals(databasesInDir.get(i), databaseToDrop)) {
                return true;
            }
        }
        return false;
    }

    public boolean deleteThisDB(String databaseName){
        File dbToDelete = new File(dbDir.getPath() + File.separator + databaseName);
        ArrayList<String> filesInDB =  getDatabaseFilenames(dbToDelete);
        deleteTablesWithinDB(databaseName, filesInDB);
        deleteTheDBItself(databaseName);
        return true;
    }

    public boolean deleteTheDBItself(String databaseName) {
        String newFilePath = dbDir.getPath() + File.separator;
        File dbToDelete = new File(newFilePath + databaseName);
        if (dbToDelete.delete()) {
            return true;
        } else {
            return false;
        }
    }


    public int deleteTablesWithinDB(String databaseName, ArrayList<String> filesInDB){
        int length = filesInDB.size();
        if(length != 0) {
            for (int i = 0; i < length; i++) {
                deleteTableWithinTheDB(filesInDB.get(i), databaseName);
            }
            File dirToDelete = new File(dbDir.getPath() + File.separator + databaseName);
            ArrayList<String> databasesInEmptyDir = getDatabaseFilenames(dirToDelete);
            length = databasesInEmptyDir.size();
            return length;
        }
        return length;
    }

    public boolean deleteTableWithinTheDB(String tableName, String databaseName){
        String newFilePath = dbDir.getPath() + File.separator + databaseName + File.separator;
        File tableToDelete = new File(newFilePath + tableName);
        if(tableToDelete.delete()){
            return true;
        }
        else{
            return false;
        }
    }


    public boolean dropTable(String tableName) throws DBExceptions.TableDoesNotExist, DBExceptions.EmptyTableList {
        String databaseName = thisDrop.getDatabaseNameDBC();
        if(findRelevantTable(tableName, databaseName)){
            String newFilePath = dbDir.getPath() + File.separator + databaseName + File.separator;
            File tableToDelete = new File(newFilePath + tableName + ".tab");
            if(tableToDelete.delete()){
                int index = findDatabaseIndex();
                databaseList.get(index).getTableList().remove(tableIndex);
                return true;
            }
            else{
                throw new DBExceptions.TableDoesNotExist();
            }
        }
        else{
            return false;
        }
    }


    ////////////////////// USE ////////////////////////////////////////////////

    public boolean runUseClass() throws DBExceptions.DatabaseDoesNotExist {
        UseQuery thisUse = new UseQuery(theCommand);
        if (!thisUse.tokeniseUse()) {
            return false;
        }
        dbName = thisUse.getDatabaseName();
        String dbName1 = thisUse.getDatabaseName();
        String dbPath = makeDBPath(dbName);
        File dbToUse = new File(dbPath);
        if(!checkDBExists(dbToUse)){
            throw new DBExceptions.DatabaseDoesNotExist();
        }
        DBCommand.databaseName = dbName;
        addDBToList(dbName1);
        ArrayList<String> fileNames = getDatabaseFilenames(dbToUse);
        readInDBTables(fileNames, dbName1);
        return true;
    }

    public void addDBToList(String dbName){
        Database dbToAdd = new Database();
        dbToAdd.setDatabaseName(dbName);
        databaseList.add(dbToAdd);
    }

    public boolean checkDBExists(File dbToUse) {
        if (dbToUse.isDirectory()) {
            return true;
        }
        else {
            return false;
        }
    }

    public String makeDBPath(String dbName){
        String newFilePath = dbDir.getPath() + File.separator + dbName;
        return  newFilePath;
    }

    public ArrayList<String> getDatabaseFilenames(File dbToCheck){
        ArrayList<String> fileNames = new ArrayList<String>(Arrays.asList(dbToCheck.list()));
        return fileNames;
    }


    public void readInDBTables(ArrayList<String> fileNames, String dbName){
        int length = fileNames.size();
        for(int i = 0; i < length; i++) {
            if (!illegalFile(fileNames.get(i))) {
                if(readInThisFile(fileNames.get(i), dbName)){
                }
            }
        }
    }

    public boolean readInThisFile(String fileName, String databaseName){
        try{
            String newFilePath = dbDir.getPath() + File.separator + databaseName + File.separator;
            File fileToOpen = new File(newFilePath + fileName);
            if(fileToOpen.exists()) {
                if(checkIfEmptyFile(fileToOpen)){
                    fileName = removeDotTab(fileName);
                    createEmptyTable1(fileName, databaseName);
                    return true;
                }
                else{
                    List<String[]> rowArr = fileToArray(fileToOpen);
                    fileName = removeDotTab(fileName);
                    createFilledTable(rowArr, fileName, databaseName);
                    return true;
                }
            }

        } catch (Exception IOException) {
            IOException.printStackTrace();
        }
        return false;
    }

    public String removeDotTab(String filename){
        if(fileHasDotTab(filename)){
            String filenameWithoutTab = filename.substring(0, filename.length()-4);
            return filenameWithoutTab;
        }
        else{
            return filename;
        }
    }


    public boolean fileHasDotTab(String filename){
        if(filename.endsWith(".tab")){
            return true;
        }
        return false;
    }

    public int createFilledTable(List<String[]> rowArr, String fileName, String databaseName){
        Table testTable = new Table();
        testTable.setTableName(fileName);
        testTable.makeFilledTable(rowArr);
        int dbIndex = findDatabaseIndexGivenDB(databaseName);
        databaseList.get(dbIndex).getTableList().add(testTable);
        return databaseList.get(dbIndex).getTableList().size();
    }

    public List<String[]> fileToArray(File fileToOpen){
        try{
            List<String[]> rowArr = new ArrayList<>();
            FileReader reader = new FileReader(fileToOpen);
            BufferedReader buffReader = new BufferedReader(reader);
            String firstLine = buffReader.readLine();
            rowArr.add(firstLine.split("\t"));
            String line;
            while( (line = buffReader.readLine()) != null){
                String[] colEl = line.split("\t");
                rowArr.add(colEl);
            }
            return rowArr;


        } catch (Exception IOException) {
            IOException.printStackTrace();
        }
        return null;
    }



    public int createEmptyTable1(String fileName, String databaseName){
        Table testTable = new Table();
        testTable.setTableName(fileName);
        int dbIndex = findDatabaseIndexGivenDB(databaseName);
        databaseList.get(dbIndex).getTableList().add(testTable);
        return databaseList.get(dbIndex).getTableList().size();
    }

    public boolean checkIfEmptyFile(File fileToOpen){
        if(fileToOpen.length() == 0){
            return true;
        }
        return false;
    }


    public int findDatabaseIndexGivenDB(String databaseName) {
        int dbIndex;
        int length = databaseList.size();
        String dbToFind = databaseName;
        for (int i = 0; i < length; i++) {
            Database dbToCheck = databaseList.get(i);

            String nameToCheck = dbToCheck.getDatabaseName();
            if (nameToCheck.equals(dbToFind)) {
                dbIndex = i;
                return dbIndex;
            }
        }
        return 0;
    }

    public boolean illegalFile(String filename){
        if(filename.startsWith(".")){
            return true;
        }
        return false;
    }

    public boolean runCreateClass() {
        thisCreate = new CreateQuery(theCommand);
        if (!thisCreate.parseCreate()) {
            return false;
        }
        commandType = thisCreate.getQueryType();
        if (commandType.equals("CREATE TABLE")) {
            createTable();
            thisCreate.setTableName();
            return true;
        }
        if (commandType.equals("CREATE DATABASE")) {
            createDatabase();
            thisCreate.setDatabaseName();
            return true;
        }
        return false;

    }


    public boolean createDatabase() {
        String databaseName = thisCreate.getDatabaseName();
        File database = new File(dbDir.getPath() + File.separator + databaseName);
        try {
            if (!database.mkdir()) {
                return false;
            }
            Database newDB = new Database();
            newDB.setDatabaseName(databaseName);
            databaseList.add(newDB);
            return true;
        } catch (Exception exception) {
        }
        return false;
    }

    public int findDatabaseIndex() {
        int dbIndex;
        int length = databaseList.size();
        String dbToFind = DBCommand.databaseName;
        for (int i = 0; i < length; i++) {
            Database dbToCheck = databaseList.get(i);

            String nameToCheck = dbToCheck.getDatabaseName();
            if (nameToCheck.equals(dbToFind)) {
                dbIndex = i;
                return dbIndex;
            }
        }
        return 0;
    }


    public boolean createTable() {
        if (!thisCreate.hasAttributes) {
            createEmptyTable();
            return true;
        } else {
            createTableWithCols();
            return true;
        }
    }

    public boolean createTableWithCols(){
        List<String> cols = thisCreate.getColumnHeadings();
        Table thisTable = new Table();
        thisTable.makeTableWCols(cols);
        String tableName = thisCreate.getTableName();
        thisTable.setTableName(tableName);
        String databaseName = thisCreate.getDatabaseNameDBC();
        String newFilePath = dbDir.getPath() + File.separator + databaseName + File.separator;
        File newTableFile = new File(newFilePath + tableName + ".tab");
        try
        {
            if (newTableFile.createNewFile()) {
                int index = findDatabaseIndex();
                String currDB = thisCreate.getDatabaseNameDBC();
                String localDir = directory + File.separator + currDB + File.separator;
                thisTable.saveEditedTable(thisTable, localDir);
                databaseList.get(index).getTableList().add(thisTable);
                return true;
            } else {
                return false;
            }

        } catch(IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }



    public boolean createEmptyTable(){
        String tableName = thisCreate.getTableName();
        List<String[]> rowArr = new ArrayList<>();
        String nullLine = "null    null    null";
        rowArr.add(nullLine.split("\t"));
        Table thisTable = new Table();
        String databaseName = thisCreate.getDatabaseNameDBC();
        String newFilePath = dbDir.getPath() + File.separator + databaseName + File.separator;
        File newTableFile = new File(newFilePath + tableName + ".tab");
        try
        {
            if (newTableFile.createNewFile()) {
                thisTable.setTableName(tableName);
            } else {
                return false;
            }
        } catch(
                IOException e)
        {
        }
        int index = findDatabaseIndex();
        databaseList.get(index).getTableList().add(thisTable);
        return true;
    }


    ///////// INSERT /////////////////////////////////////////////////////////
    public boolean runInsertClass() throws DBExceptions.EmptyTableList, DBExceptions.NullTable, DBExceptions.TableDoesNotExist {
        InsertQuery thisInsert = new InsertQuery(theCommand);
        String databaseName = DBCommand.databaseName;
        thisInsert.parseInsert();
        String tableName = thisInsert.getTableName();
        if (findRelevantTable(tableName, databaseName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);
            thisTable.addRowAtEnd(thisInsert.attributes);
            String currDB = thisInsert.getDatabaseNameDBC();
            String localDir = directory + File.separator + currDB + File.separator;
            thisTable.saveEditedTable(thisTable, localDir);
            return true;
        } else if (readInFile(tableName)) {
            int dbIndex = findDatabaseIndexGivenDB(databaseName);
            Database dbToGetFrom = databaseList.get(dbIndex);
            Table thisTable = dbToGetFrom.getTableList().get(tableIndex);
            thisTable.addRowAtEnd(thisInsert.attributes);
            String currDB = thisInsert.getDatabaseNameDBC();
            String localDir = directory + File.separator + currDB + File.separator;
            thisTable.saveEditedTable(thisTable, localDir);
            return true;
        } else {
            throw new DBExceptions.TableDoesNotExist();
        }

    }


    public boolean readInFile(String fileName){
        try {
            String fileToCheck = fileName;

            String databaseName = DBCommand.databaseName;
            String newFilePath = dbDir.getPath() + File.separator + databaseName + File.separator;
            File fileToOpen = new File(newFilePath + fileName);
            List<String[]> rowArr = new ArrayList<>();
            if(fileToOpen.exists()) {
                FileReader reader = new FileReader(fileToOpen);
                BufferedReader buffReader = new BufferedReader(reader);
                String firstLine = buffReader.readLine();
                if(firstLine != null){
                    rowArr.add(firstLine.split("\t"));
                }
                else{
                    readInEmptyFile(fileName);
                    return true;
                }
                String line;
                while( (line = buffReader.readLine()) != null){
                    String[] colEl = line.split("\t");
                    rowArr.add(colEl);
                }

                Table testTable = new Table();
                testTable.setTableName(fileName);
                testTable.makeFilledTable(rowArr);
                int dbIndex = findDatabaseIndex();
                databaseList.get(dbIndex).getTableList().add(testTable);
                buffReader.close();
            }
            else{
                if(fileToOpen.createNewFile()) {
                    FileReader reader = new FileReader(fileToOpen);
                    BufferedReader buffReader = new BufferedReader(reader);
                    String firstLine = buffReader.readLine();
                    buffReader.close();
                }
            }

        } catch (Exception IOException) {
            IOException.printStackTrace();
        }
        return false;
    }



    public void readInEmptyFile(String fileName){
        Table testTable = new Table();
        testTable.setTableName(fileName);
        int dbIndex = findDatabaseIndex();
        databaseList.get(dbIndex).getTableList().add(testTable);
    }


    public boolean findRelevantTable(String tableName, String databaseName) throws DBExceptions.EmptyTableList {
        int dbIndex = findDatabaseIndexGivenDB(databaseName);
        if(databaseList.get(dbIndex).getTableList() == null){
            throw new DBExceptions.EmptyTableList();
        }

        int length = databaseList.get(dbIndex).getTableList().size();

        String tableToFind = tableName;
        Database dbToCheck = databaseList.get(dbIndex);

        for (int i = 0; i < length; i++) {
            String nameToCheck = dbToCheck.getTableList().get(i).getTableName();

            if (tableToFind.equals(nameToCheck)) {
                tableIndex = i;
                return true;
            }
        }
        return false;
    }

}
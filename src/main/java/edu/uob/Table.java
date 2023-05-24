package edu.uob;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Table{
    private String tableName;
    private ArrayList<ArrayList<String>> myTable;
    private int colMatch;
    private int cols;
    private int rows;
    private int colToDelete;
    public String selectMatches;
    private int selectedCol;
    private int columnToMatch;
    private String changeValueTo;

    public Table(){
        myTable = new ArrayList<>();
    }

    public void makeFilledTable(List<String[]> rowArr){
        int row = rowArr.size();
        int col = rowArr.get(0).length;
        int x, y;
        for(y = 0; y < row; y++){
            ArrayList<String> newRow = new ArrayList<>();
            myTable.add(newRow);
            for(x = 0; x < col; x++){
                myTable.get(y).add(null);
            }
        }
        for(y = 0; y < row; y++){
            for(x = 0; x < col; x++){
                myTable.get(y).set(x, rowArr.get(y)[x]);
            }
        }
    }

    public void makeTableWCols(List<String> attributes){
        ArrayList<String> newRow = new ArrayList<>();
        int cols = attributes.size();
        myTable.add(newRow);
        for(int i = 0; i < cols; i++){
            myTable.get(0).add(i, attributes.get(i));
        }
        setNumberOfColsNew(cols);
        setNumberOfRowsNew(1);
    }

    public void saveEditedFile(String filename){
        int cols = getNumberOfColsStringArr();
        int rows = getNumberOfRowsStringArr();
        try {
            String name = filename + ".tab";
            File fileToOpen = new File(name);
            FileWriter writer = new FileWriter(fileToOpen);

            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < cols; y++) {
                    writer.write(myTable.get(x).get(y));
                    writer.write("\t");
                }
                writer.write("\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception IOException) {
            IOException.printStackTrace();
        }
    }


    public void saveEditedTable(Table tableToSave, String directory){
        try {
            int cols = getNumberOfColsStringArr();
            int rows = getNumberOfRowsStringArr();
            File fileToOpen = new File(directory + tableToSave.getTableName() + ".tab");
            FileWriter fWriter = new FileWriter(fileToOpen);

            for(int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    fWriter.write(tableToSave.getTableContents().get(i).get(j));
                    fWriter.write("\t");
                }
                fWriter.write("\n");
            }
            fWriter.flush();
            fWriter.close();
        } catch(IOException e)

        { e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<String>> getTableContents(){
        return myTable;
    }

    public String getTableName(){
        return tableName;
    }

    public void setTableName(String requiredTable){
        tableName = requiredTable;
    }

    public int getNumberOfColsStringArr(){
        int cols = myTable.get(0).size();
        return cols;
    }

    public int getNumberOfRowsStringArr(){
        int rows = myTable.size();
        return rows;
    }

    public boolean addRowAtEnd(String[] attributes) throws DBExceptions.NullTable {
        if(checkIfTableNull()){
                throw new DBExceptions.NullTable();
        }

        int cols = getNumberOfColsStringArr();
        int rows = getNumberOfRowsStringArr();

        ArrayList<String> extraRow = new ArrayList<>();
        myTable.add(extraRow);

        int i=0;
        int x;
        for(x=0; x < cols; x++){
            myTable.get(rows).add(i, attributes[i]);
            i++;
        }
        rows++;

        rows = getNumberOfRowsStringArr();
        setNumberOfRowsNew(rows);
        return true;
    }

    public boolean checkIfTableNull(){
        int rows = myTable.size();
        if(rows == 0){
            return true;
        }
        return false;
    }



    public void setNumberOfColsNew(int i){
        cols = i;
    }


    public void setNumberOfRowsNew(int i){
       rows = i;
    }



    public void add1ColAtEnd(String attributeName){
        int cols = getNumberOfColsStringArr();
        int rows = getNumberOfRowsStringArr();
        String[] colArr = new String[rows];
        colArr[0] = attributeName;
        for(int x = 1; x < rows; x++){
            colArr[x] = "   ";
        }

        for(int a=0; a < rows; a++){
            myTable.get(a).add(cols, colArr[a]);
        }
        cols++;
    }


    public void searchFileForMatchesToDelete(String requiredWord){
        String findMatch = requiredWord;
        int row = getNumberOfRowsStringArr();
        int cols = getNumberOfColsStringArr();
        int newNumRows = row;
        for(int i = row-1; i > 0; i--){
            for(int j = 0; j < cols; j++){
                if(myTable.get(i).get(j).contains(findMatch)){
                    myTable.remove(i);
                    newNumRows--;
                    }
                }
            }
        rows = newNumRows;
    }

    public void removeRow(int row){
        myTable.remove(row);

    }

    public boolean dropCol(String attribute) throws DBExceptions.ColumnDoesntExist {
        if(!findColToDelete(attribute)){
            throw new DBExceptions.ColumnDoesntExist();
        }
        int rows = getNumberOfRowsStringArr();
        for(int i = 0; i < rows; i++){
            myTable.get(i).remove(colToDelete);
            return true;
        }
        return false;
    }

    public boolean findColToDelete(String requiredWord){
        String findMatch = requiredWord;
        int rows = getNumberOfRowsStringArr();
        int cols = getNumberOfColsStringArr();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(myTable.get(i).get(j).contains(findMatch)){
                    colToDelete = j;
                        return true;
                    }
                }
            }
        return false;
    }

    public void deleteConditionMatch(String noun, String operator, String condition) throws DBExceptions.ColumnDoesntExist, DBExceptions.NoConditionMatchFound {
        String colToDeleteFrom = noun;
        if(!checkColExists(colToDeleteFrom)){
            throw new DBExceptions.ColumnDoesntExist();
        }
        if(!deleteConditionMatches(operator, condition)){
            throw new DBExceptions.NoConditionMatchFound();
        }
    }

    public boolean deleteConditionMatches(String operator, String condition){
        if(Objects.equals(operator, "==")){
            if(deleteEqualsMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, ">")){
            if(deleteGreaterThanMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, ">=")){
            if(deleteGreaterThanOrEqualMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "<")){
            if(deleteLessThanMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "<=")){
            if(deleteLessThanOrEqualMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "!=")){
            if(condition.equals("TRUE") || condition.equals("FALSE")){
                if(deleteBooleanNotEquals(condition)) {
                    return true;
                }
            }
            else if(deleteNotEqualMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "LIKE")){
            if(deleteLikeMatch(condition)){
                return true;
            }
        }
        return false;
    }

    public boolean deleteBooleanNotEquals(String condition){
        int row = getNumberOfRowsStringArr();
        int newRowCount = row;
        if(condition.equals("TRUE")){
            for(int i = row-1; i > 0; i--){
                if(Objects.equals(myTable.get(i).get(colMatch), "FALSE")){
                    myTable.remove(i);
                    newRowCount--;
                }
            }
            rows = newRowCount;
            return true;
        }
        else if(condition.equals("FALSE")){
            for(int i = row-1; i > 0; i--){
                if(Objects.equals(myTable.get(i).get(colMatch), "TRUE")){
                    myTable.remove(i);
                    newRowCount--;
                }
            }
            rows = newRowCount;
            return true;
        }
        return true;
    }


    public boolean deleteLikeMatch(String condition){
        searchFileForMatchesToDelete(condition);
        return true;
    }

    public String removeQuotationMarks(String condition){
        String newCondition = condition.substring(1, condition.length() - 1);
        return newCondition;
    }

    public boolean deleteNotEqualMatch(String condition){
        int row = getNumberOfRowsStringArr();
        int newRowCount = row;
        for(int i = row-1; i > 0; i--){
            String strToParse = myTable.get(i).get(colMatch);
            if(checkIfNumber(condition)) {
                float toCheck = Float.parseFloat(strToParse);
                float toCompare = Float.parseFloat(condition);
                if (toCheck != toCompare) {
                    myTable.remove(i);
                    newRowCount--;
                }
            }
            else if(strToParse.equals(condition)){
                myTable.remove(i);
                newRowCount--;
            }
        }
        rows = newRowCount;

        return true;
    }

    public boolean deleteLessThanOrEqualMatch(String condition){
        int row = getNumberOfRowsStringArr();
        int newRowCount = row;
        for(int i = row-1; i > 0; i--){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck <= toCompare){
                myTable.remove(i);
                newRowCount--;

            }
        }
        rows = newRowCount;
        return true;
    }

    public boolean deleteLessThanMatch(String condition){
        int row = getNumberOfRowsStringArr();
        int newRowCount = row;
        for(int i = row-1; i > 0; i--){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck < toCompare){
                myTable.remove(i);
                newRowCount--;

            }
        }
        rows = newRowCount;
        return true;
    }

    public boolean deleteGreaterThanOrEqualMatch(String condition){
        int row = getNumberOfRowsStringArr();
        int newRowCount = row;
        for(int i = row-1; i > 0; i--){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck >= toCompare){
                myTable.remove(i);
                newRowCount--;

            }
        }
        rows = newRowCount;
        return true;
    }


    public boolean deleteGreaterThanMatch(String condition){
        int row = getNumberOfRowsStringArr();
        int newRowCount = row;
            for (int i = row - 1; i > 0; i--) {
                String strToParse = myTable.get(i).get(colMatch);
                float floatToCheck = Float.parseFloat(strToParse);
                float floatToCompare = Float.parseFloat(condition);
                if (floatToCheck > floatToCompare) {
                    myTable.remove(i);
                    newRowCount--;
                }
            }
        rows = newRowCount;
        return true;
    }


  public boolean deleteEqualsMatch(String condition){
        int row = getNumberOfRowsStringArr();
        int newRowCount = row;
        for(int i = row-1; i > 0; i--){
                if(Objects.equals(myTable.get(i).get(colMatch), condition)){
                    myTable.remove(i);
                    newRowCount--;
                }
            }
      rows = newRowCount;
      return true;
    }

    public boolean selectOnlyCondition(String attribute, String operator, String condition, String noun){
       if(checkColExists2(attribute) && checkColExists3(noun)){
           selectSpecificColIfConditionMatches(attribute, operator, condition, noun);
           return true;
       }
        return false;
    }


    public boolean selectAll(){
        int rows = getNumberOfRowsStringArr();
        int cols = getNumberOfColsStringArr();
        createTopRow();
        selectMatches = selectMatches + "\n";
        for(int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                selectMatches = selectMatches + myTable.get(i).get(j) + "\t";
            }
            selectMatches = selectMatches + "\n";
        }
        return true;
    }


    public boolean checkColExists(String requiredCol) {
        int cols = getNumberOfColsStringArr();
            for (int j = 0; j < cols; j++) {
                if (myTable.get(0).get(j).contains(requiredCol)) {
                    colMatch = j;
                    return true;
                }
            }
        return false;
    }

    public boolean checkColExists2(String requiredCol) {
        int cols = getNumberOfColsStringArr();
        for (int j = 0; j < cols; j++) {
            if (myTable.get(0).get(j).contains(requiredCol)) {
                selectedCol = j;
                return true;
            }
        }
        return false;
    }

    public boolean checkColExists3(String requiredCol) {
        int cols = getNumberOfColsStringArr();
        for (int j = 0; j < cols; j++) {
            if (myTable.get(0).get(j).contains(requiredCol)) {
                columnToMatch = j;
                return true;
            }
        }
        return false;
    }


    public boolean selectSpecificCol(String attribute){
        if(findCol(attribute)){
            createTopRow1Col();
            addRowsTo1Col();
            return true;
        }
        return false;
    }

    public void addRowsTo1Col(){
        int row = getNumberOfRowsStringArr();
       int col = getNumberOfColsStringArr();
       for(int j = 1; j < row; j++){
           selectMatches = selectMatches + "\n" + myTable.get(j).get(selectedCol);
       }
    }



    public void createTopRow1Col(){
        selectMatches = myTable.get(0).get(selectedCol);
    }

    public boolean findCol(String requiredWord){
        String findMatch = requiredWord;
        int rows = getNumberOfRowsStringArr();
        int cols = getNumberOfColsStringArr();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(myTable.get(i).get(j).contains(findMatch)){
                    selectedCol = j;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findColToMatch(String requiredWord){
        String findMatch = requiredWord;
        int rows = getNumberOfRowsStringArr();
        int cols = getNumberOfColsStringArr();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(myTable.get(i).get(j).contains(findMatch)){
                    columnToMatch = j;
                    return true;
                }
            }
        }
        return false;
    }


    public boolean selectAllOfCondition(String noun, String operator, String condition){
        createTopRow();
        if(checkColExists(noun)){
            if (getConditionMatches(operator, condition)) {
                return true;
           }
        }
        return false;
    }


    public boolean getConditionMatches(String operator, String condition){
        if(Objects.equals(operator, "==")){
            if(findEqualsMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, ">")){
            if(findGreaterThanMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, ">=")){
            if(findGreaterThanOrEqualMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "<")){
            if(findLessThanMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "<=")){
            if(findLessThanOrEqualMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "!=")){
            if(condition.equals("TRUE") || condition.equals("FALSE")){
                if(booleanNotEquals(condition)) {
                    return true;
                }
            }

            else if(findNotEqualMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "LIKE")){
            if(findLikeMatch(condition)){
                return true;
            }
        }
        return false;
    }

    public boolean booleanNotEquals(String condition){
        int rows = getNumberOfRowsStringArr();
        if(condition.equals("TRUE")){
            for(int i = 0; i < rows; i++){
                if(Objects.equals(myTable.get(i).get(colMatch), "FALSE")){
                    addRowToSelectMatches(i);
                }
            }
            return true;
        }
        else if(condition.equals("FALSE")){
            for(int i = 0; i < rows; i++){
                if(Objects.equals(myTable.get(i).get(colMatch), "TRUE")){
                    addRowToSelectMatches(i);
                }
            }
            return true;
        }
        return true;
    }

    public boolean findLikeMatch(String condition){
        searchFileForMatches(condition);
        return true;
    }


    public void searchFileForMatches(String requiredWord){
        String findMatch = requiredWord;
        int row = getNumberOfRowsStringArr();
        int cols = getNumberOfColsStringArr();
        for(int i = 1; i < row; i++){
            for(int j = 0; j < cols; j++){
                if(myTable.get(i).get(j).contains(findMatch)){
                    addRowToSelectMatches(i);
                }
            }
        }
    }

    public void addRowToSelectMatches(int i){
        String rowToAdd = createRowFromMatch(i);
        if(selectMatches == null){
            selectMatches = rowToAdd;
        }
        else{
            selectMatches = selectMatches + "\n" + rowToAdd;
        }
    }

    public String createRowFromMatch(int i){
        String theRow;
        theRow = myTable.get(i).get(0);
        for(int j = 1; j < myTable.get(i).size(); j++){
            theRow = theRow + "\t" + myTable.get(i).get(j);
        }
        return theRow;
    }

    public void createTopRow(){
       selectMatches = myTable.get(0).get(0);
        for(int j = 1; j < myTable.get(0).size(); j++){
            selectMatches = selectMatches + "\t" + myTable.get(0).get(j);
        }
    }


    public boolean findNotEqualMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(colMatch);
            if(checkIfNumber(condition)) {
                float toCheck = Float.parseFloat(strToParse);
                float toCompare = Float.parseFloat(condition);
                if (toCheck != toCompare) {
                    addRowToSelectMatches(i);
                }
            }
        }
        return true;
    }

    public boolean checkIfNumber(String strToCheck){
        try {
            double num = Double.parseDouble(strToCheck);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean findLessThanOrEqualMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck <= toCompare){
                addRowToSelectMatches(i);
            }
        }
        return true;
    }

    public boolean findLessThanMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);

            if(toCheck < toCompare){
                addRowToSelectMatches(i);
            }
        }
        return true;
    }

    public boolean findGreaterThanOrEqualMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck >= toCompare){
                addRowToSelectMatches(i);
            }
        }
        return true;
    }


    public boolean findGreaterThanMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck > toCompare){
                addRowToSelectMatches(i);
            }
        }
        return true;
    }


    public boolean findEqualsMatch(String condition){
        int rows = getNumberOfRowsStringArr();
        for(int i = 0; i < rows; i++){
                if(Objects.equals(myTable.get(i).get(colMatch), condition)){
                    addRowToSelectMatches(i);
            }
        }
        return true;
    }

    public boolean selectSpecificColIfConditionMatches(String attribute, String operator, String condition, String noun){
        if(findCol(attribute) && findColToMatch(noun)){
            createTopRow1Col();
            getColIfConditionMatches2(operator, condition);
            return true;
        }
        return false;
    }

    public boolean getColIfConditionMatches2(String operator, String condition){
        if(Objects.equals(operator, "==")){
            if(findEqualsMatch2(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, ">")){
            if(findGreaterThanMatch2(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, ">=")){
            if(findGreaterThanOrEqualMatch2(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "<")){
            if(findLessThanMatch2(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "<=")){
            if(findLessThanOrEqualMatch2(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "!=")){
            if(condition.equals("TRUE") || condition.equals("FALSE")){
                if(booleanNotEquals2(condition)) {
                    return true;
                }
            }
            else if(findNotEqualMatch2(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "LIKE")){
            if(findLikeMatch2(condition)){
                return true;
            }
        }
        return false;
    }

    public boolean booleanNotEquals2(String condition){
        int rows = getNumberOfRowsStringArr();
        if(condition.equals("TRUE")){
            for(int i = 0; i < rows; i++){
                if(Objects.equals(myTable.get(i).get(columnToMatch), "FALSE")){
                    addRowToSelectMatches2(i);
                }
            }
            return true;
        }
        else if(condition.equals("FALSE")){
            for(int i = 0; i < rows; i++){
                if(Objects.equals(myTable.get(i).get(columnToMatch), "TRUE")){
                    addRowToSelectMatches2(i);
                }
            }
            return true;
        }
        return true;
    }

    public boolean findLikeMatch2(String condition){
        String newCondition = removeQuotationMarks(condition);
        searchFileForMatches2(newCondition);
        return true;
    }

    public void searchFileForMatches2(String requiredWord){
        String findMatch = requiredWord;
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            if(myTable.get(i).get(columnToMatch).contains(findMatch)){
                addRowToSelectMatches2(i);
            }
        }
    }

    public void addRowToSelectMatches2(int i){
        selectMatches = selectMatches + "\n" + myTable.get(i).get(selectedCol);
    }

// need to do a BOOLEAN CHECK
    public boolean findNotEqualMatch2(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(columnToMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck != toCompare){
                addRowToSelectMatches2(i);
            }
        }
        return true;
    }

    public boolean findLessThanOrEqualMatch2(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(columnToMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck <= toCompare){
                addRowToSelectMatches2(i);
            }
        }
        return true;
    }

    public boolean findLessThanMatch2(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(columnToMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck < toCompare){
                addRowToSelectMatches2(i);
            }
        }
        return true;
    }

    public boolean findGreaterThanOrEqualMatch2(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(columnToMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck >= toCompare){
                addRowToSelectMatches2(i);
            }
        }
        return true;
    }


    public boolean findGreaterThanMatch2(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = 1; i < row; i++){
            String strToParse = myTable.get(i).get(columnToMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck > toCompare){
                addRowToSelectMatches2(i);
            }
        }
        return true;
    }


    public boolean findEqualsMatch2(String condition){
        int rows = getNumberOfRowsStringArr();
        for(int i = 0; i < rows; i++){
            if(Objects.equals(myTable.get(i).get(columnToMatch), condition)){
                addRowToSelectMatches2(i);
            }
        }
        return true;
    }


    public boolean updateConditionMatch(String noun, String operator, String condition, String[] valuePair){
        changeValueTo = valuePair[1];
        if(checkColExists(noun)){
            if(checkColExists2(valuePair[0])) {
                if (getUpdateConditionMatches(operator, condition)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean getUpdateConditionMatches(String operator, String condition){
        if(Objects.equals(operator, "==")){
            if(updateEqualsMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, ">")){
            if(updateGreaterThanMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, ">=")){
            if(updateGreaterThanOrEqualMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "<")){
            if(updateLessThanMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "<=")){
            if(updateLessThanOrEqualMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "!=")){
            if(updateNotEqualMatch(condition)){
                return true;
            }
        }
        if(Objects.equals(operator, "LIKE")){
            if(updateLikeMatch(condition)){
                return true;
            }
        }
        return false;
    }

    public boolean updateLikeMatch(String condition){
        searchFileForMatchesToUpdate(condition);
        return true;
    }

    public void searchFileForMatchesToUpdate(String requiredWord){
        String findMatch = requiredWord;
        int row = getNumberOfRowsStringArr();
        int cols = getNumberOfColsStringArr();
        int newNumRows = row;
        for(int i = row-1; i > 0; i--){
            for(int j = 0; j < cols; j++){
                if(myTable.get(i).get(j).contains(findMatch)){
                    myTable.get(i).set(selectedCol, changeValueTo);
                }
            }
        }
        rows = newNumRows;
    }


    public boolean updateNotEqualMatch(String condition){
        int row = getNumberOfRowsStringArr();
        int newRowCount = row;
        for(int i = row-1; i > 0; i--){
            String strToParse = myTable.get(i).get(colMatch);
            if(checkIfNumber(condition)) {
                float toCheck = Float.parseFloat(strToParse);
                float toCompare = Float.parseFloat(condition);
                if (toCheck != toCompare) {
                    myTable.get(i).set(selectedCol, changeValueTo);
                }
            }
            else if(strToParse.equals(condition)){
                myTable.get(i).set(selectedCol, changeValueTo);
            }
        }
        rows = newRowCount;
        return true;
    }

    public boolean updateLessThanOrEqualMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = row-1; i > 0; i--){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck <= toCompare){
                myTable.get(i).set(selectedCol, changeValueTo);
            }
        }
        return true;
    }

    public boolean updateLessThanMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = row-1; i > 0; i--){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck < toCompare){
                myTable.get(i).set(selectedCol, changeValueTo);
            }
        }
        return true;
    }

    public boolean updateGreaterThanOrEqualMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = row-1; i > 0; i--){
            String strToParse = myTable.get(i).get(colMatch);
            float toCheck = Float.parseFloat(strToParse);
            float toCompare = Float.parseFloat(condition);
            if(toCheck >= toCompare){
                myTable.get(i).set(selectedCol, changeValueTo);
            }
        }
        return true;
    }


    public boolean updateGreaterThanMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for (int i = row - 1; i > 0; i--) {
            String strToParse = myTable.get(i).get(colMatch);
            float floatToCheck = Float.parseFloat(strToParse);
            float floatToCompare = Float.parseFloat(condition);
            if (floatToCheck > floatToCompare) {
                myTable.get(i).set(selectedCol, changeValueTo);
                }
            }
        return true;
    }


    public boolean updateEqualsMatch(String condition){
        int row = getNumberOfRowsStringArr();
        for(int i = row-1; i > 0; i--){
            if(Objects.equals(myTable.get(i).get(colMatch), condition)){
                myTable.get(i).set(selectedCol, changeValueTo);
            }
        }
        return true;
    }

}

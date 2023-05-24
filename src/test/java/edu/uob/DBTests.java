package edu.uob;

import com.sun.security.jgss.GSSUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static edu.uob.DBServer1.readFile;
import static org.junit.jupiter.api.Assertions.*;


final class DBTests {

  private DBServer server;

  @BeforeEach
  void setup(@TempDir File dbDir) {
    server = new DBServer(dbDir);
  }


@Test
void testQuery() throws DBExceptions{
      String command = "create table Table1;";
      Tokeniser testTokeniser = new Tokeniser(command);
      String uppercaseCommand = testTokeniser.commandToUpper();
      assertEquals(uppercaseCommand, "CREATE TABLE TABLE1;");
}

    @Test
    void testQuery2() throws DBExceptions {
        String command = "CreAte table mIrAnDa;";
        Tokeniser testTokeniser = new Tokeniser(command);
        String uppercaseCommand = testTokeniser.commandToUpper();
        assertEquals(uppercaseCommand, "CREATE TABLE MIRANDA;");
    }

    @Test
    void checkSemiColon() throws DBExceptions {
        String command = "CreAte table mIrAnDa;";
        Tokeniser testTokeniser = new Tokeniser(command);
        assertTrue(testTokeniser.checkSemiColon());
    }

    @Test
    void checkNoSemiColon() throws DBExceptions {
        String command = "CreAte table mIrAnDa";
        final Tokeniser[] testTokeniser1 = new Tokeniser[1];
        assertThrows(DBExceptions.MissingSemiColon.class, ()-> testTokeniser1[0] = new Tokeniser(command));
    }

    @Test
    void checkInvalidQueryType() throws DBExceptions {
        String command = "Craaae table mIrAnDa;";
        final Tokeniser[] testTokeniser1 = new Tokeniser[1];
        assertThrows(DBExceptions.InvalidQueryType.class, ()-> testTokeniser1[0] = new Tokeniser(command));
    }

    @Test
    void neatenString() throws DBExceptions {
        String command = "    CREATE TABLE MIRANDA;";
        Tokeniser testTokeniser = new Tokeniser(command);
        assertEquals(testTokeniser.neatenString(), "CREATE TABLE MIRANDA;");
    }

    @Test
    void splitStringOnWhitespace() throws DBExceptions {
        String command = "CREATE TABLE MIRANDA;";
        Tokeniser testTokeniser = new Tokeniser(command);
        String[] stringArr = {"CREATE", "TABLE", "MIRANDA;"};
        String[] check = testTokeniser.splitTheCommand();
        assertEquals(check[0], stringArr[0]);
        assertEquals(check[1], stringArr[1]);
        assertEquals(check[2], stringArr[2]);
    }

    @Test
    void checkFinalCommand() throws DBExceptions {
        String command = "USE TABLE Miranda   ;";
        Tokeniser testTokeniser = new Tokeniser(command);
        String[] check = testTokeniser.makeFinalCommand();
        String[] check2 = {"USE", "TABLE", "Miranda"};
        assertEquals(check[0], check2[0]);
        assertEquals(check[1], check2[1]);
        assertEquals(check[2], check2[2]);
    }

    @Test
    void checkFinalCommand2() throws DBExceptions {
        String command = "INSERT INTO Miranda1 Values(id, age, number);";
        Tokeniser testTokeniser = new Tokeniser(command);
        String[] check = testTokeniser.makeFinalCommand();
        String[] check2 = {"INSERT", "INTO", "Miranda1", "Values(id,", "age,", "number)"};
        assertEquals(check[0], check2[0]);
        assertEquals(check[1], check2[1]);
        assertEquals(check[2], check2[2]);
        assertEquals(check[3], check2[3]);
        assertEquals(check[4], check2[4]);
        assertEquals(check[5], check2[5]);
    }


    @Test
    void testUseHasEmptyDBName(@TempDir File dbDir){
        String[] command = {"USE"};
        server = new DBServer(dbDir);
        UseQuery useTest = new UseQuery(command);
        assertFalse(useTest.checkCommandLength(command));
    }

    @Test
    void testUseHasDBName(@TempDir File dbDir){
        String[] command = {"USE", "DB2"};
        server = new DBServer(dbDir);
        UseQuery useTest = new UseQuery(command);
        assertTrue(useTest.checkCommandLength(command));
    }

    @Test
    void testUseHasDBName1(@TempDir File dbDir) throws DBExceptions{
        String command = "USE DB2;";
        server = new DBServer(dbDir);
        final Parser[] testParser = new Parser[1];
        assertThrows(DBExceptions.DatabaseDoesNotExist.class, ()-> testParser[0] = new Parser(server, command, dbDir, server.databaseList));
    }


    @Test
    void testDBPlainTextLetters(@TempDir File dbDir){
      String[] command = {"CREATE", "DATABASE", "MYDB"};
      server = new DBServer(dbDir);
      CreateQuery createTest = new CreateQuery(command);
      String[] splitDBName = createTest.splitDBName();
      String[] checkSplit = {"M", "Y", "D", "B"};
      assertEquals(splitDBName[0], checkSplit[0]);
      assertEquals(splitDBName[1], checkSplit[1]);
      assertEquals(splitDBName[2], checkSplit[2]);
      assertEquals(splitDBName[3], checkSplit[3]);
      assertTrue(createTest.checkPlainText());
    }


    @Test
    void testDBPlainTextLetters222() {
        String command = "CREATE DATABASE myDB1;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        String command1 = "USE myDB1;";
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        String command3 = "CREATE TABLE marks (name);";
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
    }

    @Test
    void testDBPlainTextLetters333() {
        String command = "create database myDB3;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        String command1 = "USE myDB3;";
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
    }

    @Test
    void testDBPlainTextLetters444() {
        String command = "CREATE DATABASE myDB3;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        String directory = "thisDirectory";
        String command1 = "USE myDB3;";
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        String command3 = "CREATE TABLE marks (name, mark, pass);";
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        String command33 = "CREATE TABLE data (username, school, class);";
        assertTrue(server.handleCommand(command33).startsWith("[OK]"));
        String command6 = "CREATE DATABASE myDB4;";
        assertTrue(server.handleCommand(command6).startsWith("[OK]"));
        String command7 = "USE myDB4;";
        assertTrue(server.handleCommand(command7).startsWith("[OK]"));
        String command8 = "CREATE TABLE marks (name, mark, pass);";
        assertTrue(server.handleCommand(command8).startsWith("[OK]"));
    }


    @Test
    void testDBPlainTextLetters555() {
        String[] command = {"INSERT", "INTO", "marks", "VALUES", "('Miranda',", "100,", "TRUE)"};
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.checkIfStringLiteral("'Miranda'"));
    }

    @Test
    void testDBPlainTextLetters566() {
        String[] command = {"INSERT", "INTO", "marks", "VALUES", "(Miranda',", "100,", "TRUE)"};
        InsertQuery insert = new InsertQuery(command);
        assertFalse(insert.checkIfStringLiteral("Miranda'"));
    }

    @Test
    void testUsePlainText() {
        String[] command1 = {"USE", "myDB3"};
        UseQuery use = new UseQuery(command1);
        assertTrue(use.tokeniseUse());
    }

    @Test
    void testUsePlainTextWrong() {
        String[] command1 = {"USE", "my£DB3"};
        UseQuery use = new UseQuery(command1);
        assertFalse(use.tokeniseUse());
    }

    @Test
    void testInsertPlainTextWrong() {
        String[] command = {"INSERT", "iNTO", "t£ble1", "values", "(name,", "ag£e)"};
        InsertQuery insert = new InsertQuery(command);
        assertFalse(insert.checkPlainTextTableName());
    }

    @Test
    void testInsertReadInTable() {
        String command = "Create database myDB1;";
        String command2 = "USE myDB1;";
        String command3 = "create table marks (name, grade, pass);";
        String command4 = "Insert into marks values ('Miranda', 100, true);";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
    }

    @Test
    void testDBPlainTextLetters777() {
        String command = "CREATE DATABASE myDB3;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        String command1 = "USE myDB3;";
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        String command3 = "CREATE TABLE random (name, mark, pass);";
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        String command33 = "CREATE TABLE data (username, school, class);";
        assertTrue(server.handleCommand(command33).startsWith("[OK]"));
    }

    @Test
    void testDBPlainTextLetters888() {
        String[] command = {"ALTER", "table", "myTable", "Drop", "Age"};
        AlterQuery alter = new AlterQuery(command);
        assertTrue(alter.checkForTable());
        assertTrue(alter.tableName());
        assertTrue(alter.checkAlterationType());
        String alterationType = alter.getAlterationType();
        assertEquals(alterationType, "DROP");
        assertTrue(alter.checkPlainText());
        assertTrue(alter.checkAttribute());
        assertTrue(alter.parseAlter());
    }

    @Test
    void testDBPlainTextLetters802() {
        String[] command = {"ALTER", "table", "myTable", "aDD", "Age"};
        AlterQuery alter = new AlterQuery(command);
        assertTrue(alter.checkForTable());
        assertTrue(alter.tableName());
        assertTrue(alter.checkAlterationType());
        String alterationType = alter.getAlterationType();
        assertEquals(alterationType, "ADD");
        assertTrue(alter.checkPlainText());
        assertTrue(alter.checkAttribute());
        assertTrue(alter.parseAlter());
        assertEquals(alter.getAttribute(), "Age");
    }

    @Test
    void testDBPlainTextLetters800() {
        String[] command = {"ALTER", "myTable", "Dropp", "Age"};
        AlterQuery alter = new AlterQuery(command);
        assertFalse(alter.checkForTable());
        assertFalse(alter.parseAlter());
        assertFalse(alter.checkAlterationType());
    }

    @Test
    void testDBPlainTextLetters801() {
        String[] command = {"ALTER", "table", "m!Table", "Dropp", "*ge"};
        AlterQuery alter = new AlterQuery(command);
        assertFalse(alter.parseAlter());
        assertFalse(alter.checkAlterationType());
        assertFalse(alter.checkPlainText());
    }


    @Test
    void testDrop103(){
        String command = "CREATE DATABASE newDB;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        String command1 = "USE newDB;";
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        String command2 = "CREATE TABLE tableToDrop;";
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        String command3 = "CREATE TABLE fillerTable;";
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        String command4 = "DROP DATABASE newDB;";
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
    }

    @Test
    void testDropDB100(){
        String command = "CREATE DATABASE myDB4;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        String command1 = "USE myDB4;";
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        String command4 = "DROP DATABASE myDB4;";
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
    }

    @Test
    void testDrop101() {
        String[] command = {"DROP", "daTabase", "myFaveDB"};
        DropQuery drop = new DropQuery(command);
        assertTrue(drop.checkDBorTable());
        assertEquals(drop.getDropType(), "DATABASE");
        assertTrue(drop.parseDrop());
        assertEquals(drop.getThingToDrop(), "myFaveDB");
    }

    @Test
    void testDrop102() {
        String[] command = {"DROP", "myFaveDB"};
        DropQuery drop = new DropQuery(command);
        assertFalse(drop.parseDrop());
    }


    @Test
    void testDBPlainText2(){
        String[] command = {"CREATE", "DATABASE", "myDBmirANda"};
        CreateQuery createTest = new CreateQuery(command);
        assertTrue(createTest.checkPlainText());
    }

    @Test
    void testDBPlainText4(){
        String[] command = {"CREATE", "DATABASE", "1m2yD3B4"};
        CreateQuery createTest = new CreateQuery(command);
        assertTrue(createTest.checkPlainText());
    }

    @Test
    void testTablePlainTextLetters(){
        String[] command = {"CREATE", "table", "table1"};
        CreateQuery createTest = new CreateQuery(command);
        String[] splitDBName = createTest.splitDBName();
        String[] checkSplit = {"t", "a", "b", "l", "e", "1"};
        assertEquals(splitDBName[0], checkSplit[0]);
        assertEquals(splitDBName[1], checkSplit[1]);
        assertEquals(splitDBName[2], checkSplit[2]);
        assertEquals(splitDBName[3], checkSplit[3]);
        assertEquals(splitDBName[4], checkSplit[4]);
        assertEquals(splitDBName[5], checkSplit[5]);
        assertTrue(createTest.checkPlainText());
        assertTrue(createTest.createTable());
        assertEquals(createTest.getTableName(), "table1");
    }

    @Test
    void testTableFail(){
        String[] command = {"CREATE", "tabl", "table1"};
        CreateQuery createTest = new CreateQuery(command);
        assertFalse(createTest.databaseOrTable());
    }

    @Test
    void testTablePlainText2(){
        String[] command = {"CREATE", "Table", "th1sTable"};
        CreateQuery createTest = new CreateQuery(command);
        assertTrue(createTest.checkPlainText());
    }


    @Test
    void testTablePlainText4(){
        String[] command = {"CREATE", "TABLE", "1m2yD3B4"};
        CreateQuery createTest = new CreateQuery(command);
        assertTrue(createTest.createTable());
    }


    @Test
    void testTableBrackets() throws DBExceptions {
        String command = "create TABLE Miranda (   id, name, age   );";
        Tokeniser testTokeniser = new Tokeniser(command);
        String[] check = testTokeniser.makeFinalCommand();
        CreateQuery createQuery = new CreateQuery(check);
        String check2 = createQuery.arrayToString();
        createQuery.splitOnBrackets();
        assertEquals(createQuery.bracketString, "( id, name, age )");
    }

    @Test
    void testArrToString(){
        String[] command = {"CREATE", "TABLE", "(id)"};
        CreateQuery createQuery = new CreateQuery(command);
        String check = createQuery.arrayToString();
        createQuery.splitOnBrackets();
        assertEquals(createQuery.bracketString, "(id)");
    }

    @Test
    void testRemoveBrackets(){
        String[] command = {"CREATE", "TABLE", "(1id)"};
        CreateQuery createQuery = new CreateQuery(command);
        String check = createQuery.arrayToString();
        createQuery.splitOnBrackets();
        assertEquals(createQuery.bracketString, "(1id)");
        String sansBrackets = createQuery.removeBrackets();
        assertEquals(sansBrackets, "1id");
        assertTrue(createQuery.checkPlainText1Attribute(sansBrackets));
        assertTrue(createQuery.createTableWithAttributes());
    }


    @Test
    void testMultiAttributes() throws DBExceptions {
        String command = "CREATE TABLE info (id, name, location);";
        Tokeniser tokeniser = new Tokeniser(command);
        String[] finalCommand = tokeniser.makeFinalCommand();
        CreateQuery createQuery = new CreateQuery(finalCommand);
        String check = createQuery.arrayToString();
        createQuery.splitOnBrackets();
        assertEquals(createQuery.bracketString, "(id, name, location)");
        String sansBrackets = createQuery.removeBrackets();
        assertEquals(sansBrackets, "id, name, location");
        assertTrue(createQuery.checkPlainText1Attribute(sansBrackets));
        assertTrue(createQuery.createTableWithAttributes());
    }


    @Test
    void testInsert(@TempDir File dbDir) throws DBExceptions {
        String command = "   iNSErt  inTo   table1 values(id, name, location);";
        server = new DBServer(dbDir);
        String directory = "thisDir";
        Tokeniser tokeniser = new Tokeniser(command);
        String[] finalCommand = tokeniser.makeFinalCommand();
        int length = finalCommand.length;
        InsertQuery insert = new InsertQuery(finalCommand);
        assertTrue(insert.checkSyntax());
        assertTrue(insert.checkValues());
        assertTrue(insert.splitOnBrackets());
    }

    @Test
    void testInsert777(@TempDir File dbDir) throws DBExceptions {
        String command = "   iNSErt  inTo   table1 values id, name, location;";
        server = new DBServer(dbDir);
        String directory = "thisDir";
        Tokeniser tokeniser = new Tokeniser(command);
        String[] finalCommand = tokeniser.makeFinalCommand();
        InsertQuery insert = new InsertQuery(finalCommand);
        assertTrue(insert.checkSyntax());
        assertTrue(insert.checkValues());
        assertFalse(insert.splitOnBrackets());
    }


    @Test
    void testInsert22(@TempDir File dbDir){
        String[] command = {"iNSErt ", "table1"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertFalse(insert.checkSyntax());
    }


    @Test
    void testInsert4(@TempDir File dbDir){
        String[] command = {"iNSErt ", "   inTo", "  t!ble1", "VALUES(id,",   "name,",  "age)"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertFalse(insert.checkPlainTextTableName());
    }

    @Test
    void testInsert5(@TempDir File dbDir){
        String[] command = {"iNSErt", "inTo", "table2", "VALUES('id',",   "'name',", "'age')"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.checkSyntax());
        assertTrue(insert.checkPlainTextTableName());
        String bracketStrTest = "('id', 'name', 'age')";
        assertTrue(insert.splitOnBrackets());
        String bracketStr = insert.bracketString;
        assertEquals(bracketStrTest, bracketStr);
        String sansBrackets = insert.removeBrackets();
        assertEquals(sansBrackets, "'id', 'name', 'age'");
        String[] innerBrackets = insert.splitOnComma(sansBrackets);
        String[] innerBracketsTest = {"'id'", "'name'", "'age'"};
        assertEquals(innerBracketsTest[0], innerBrackets[0]);
        assertEquals(innerBracketsTest[1], innerBrackets[1]);
        assertEquals(innerBracketsTest[2], innerBrackets[2]);
        String[] trimmedString = insert.trimString(innerBrackets);
        String[] trimmedStrTest = {"'id'", "'name'", "'age'"};
        assertEquals(trimmedStrTest[0], trimmedString[0]);
        assertEquals(trimmedStrTest[1], trimmedString[1]);
        assertEquals(trimmedStrTest[2], trimmedString[2]);
        assertTrue(insert.identifyAttributes());
    }

    @Test
    void testInsert7(@TempDir File dbDir){
        String[] command = {"iNSErt", "inTo", "table2", "VALUES(id)"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.checkSyntax());
        assertTrue(insert.checkPlainTextTableName());
        String bracketStrTest = "(id)";
        assertTrue(insert.splitOnBrackets());
        String bracketStr = insert.bracketString;
        assertEquals(bracketStrTest, bracketStr);
        String sansBrackets = insert.removeBrackets();
        assertEquals(sansBrackets, "id");
        String[] innerBrackets = insert.splitOnComma(sansBrackets);
        String[] innerBracketsTest = {"id"};
        assertEquals(innerBracketsTest[0], innerBrackets[0]);
    }

    @Test
    void testInsert6(@TempDir File dbDir){
        String[] command = {"iNSErt", "inTo", "table2", "VALUES", "id", "name", "age"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.checkSyntax());
        assertTrue(insert.checkPlainTextTableName());
        assertFalse(insert.splitOnBrackets());
    }

    @Test
    void testInsert8(@TempDir File dbDir){
        String[] command = {"iNSErt", "inTo", "table2", "VALUES", "('M!randa', FALSE, 'a girl')"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.checkSyntax());
        assertTrue(insert.checkPlainTextTableName());
        String bracketStrTest = "('M!randa', FALSE, 'a girl')";
        assertTrue(insert.splitOnBrackets());
        String bracketStr = insert.bracketString;
        assertEquals(bracketStrTest, bracketStr);
        String sansBrackets = insert.removeBrackets();
        assertEquals(sansBrackets, "'M!randa', FALSE, 'a girl'");
        String[] innerBrackets = insert.splitOnComma(sansBrackets);
        String[] innerBracketsTest = {"'M!randa'", "FALSE", "'a girl'"};
        assertEquals(innerBracketsTest[0], innerBrackets[0]);
        assertEquals(innerBracketsTest[1], innerBrackets[1]);
        assertEquals(innerBracketsTest[2], innerBrackets[2]);
        assertTrue(insert.identifyAttributes());
    }


    @Test
    void testInsert12(@TempDir File dbDir){
        String[] command = {"iNSErt", "inTo", "table2", "VALUES", "(true,", "11.56)"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.checkSyntax());
        assertTrue(insert.checkPlainTextTableName());
        assertTrue(insert.splitOnBrackets());
        String sansBrackets = insert.removeBrackets();
        assertTrue(insert.identifyAttributes());
    }

    @Test
    void testInsert13(@TempDir File dbDir){
        String[] command = {"iNSErt", "inTo", "table2", "VALUES", "(true,",  "+11.56)"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.checkSyntax());
        assertTrue(insert.checkPlainTextTableName());
        String bracketStrTest = "(true, +11.56)";
        assertTrue(insert.splitOnBrackets());
        String bracketStr = insert.bracketString;
        assertEquals(bracketStrTest, bracketStr);
        String sansBrackets = insert.removeBrackets();
        assertEquals(sansBrackets, "true, +11.56");
        String[] innerBrackets = insert.splitOnComma(sansBrackets);
        String[] innerBracketsTest = {"true", "+11.56"};
        assertEquals(innerBracketsTest[0], innerBrackets[0]);
        assertEquals(innerBracketsTest[1], innerBrackets[1]);
        assertTrue(insert.identifyAttributes());
    }

    @Test
    void testInsert14(@TempDir File dbDir){
        String[] command = {"iNSErt", "inTo", "table2", "VALUES", "(true, -11.!6)"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.checkSyntax());
        assertTrue(insert.checkPlainTextTableName());
        String bracketStrTest = "(true, -11.!6)";
        assertTrue(insert.splitOnBrackets());
        String bracketStr = insert.bracketString;
        assertEquals(bracketStrTest, bracketStr);
        String sansBrackets = insert.removeBrackets();
        assertEquals(sansBrackets, "true, -11.!6");
        String[] innerBrackets = insert.splitOnComma(sansBrackets);
        String[] innerBracketsTest = {"true", "-11.!6"};
        assertEquals(innerBracketsTest[0], innerBrackets[0]);
        assertEquals(innerBracketsTest[1], innerBrackets[1]);
        String strToCheck = "-11.!6";
        assertFalse(insert.checkIfFloatLiteral(strToCheck));
    }

    @Test
    void testInsert15(@TempDir File dbDir){
        String[] command = {"iNSErt", "inTo", "table2", "VALUES", "(true, +2)"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.parseInsert());
    }

    @Test
    void testInsert16(@TempDir File dbDir){
        String[] command = {"iNSErt", "inTo", "table2", "VALUES", "(-1, 'miranda')"};
        server = new DBServer(dbDir);
        InsertQuery insert = new InsertQuery(command);
        assertTrue(insert.parseInsert());
    }

    @Test
    void testDelete(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "frOm", "table2", "where", "age", "<", "5;"};
        server = new DBServer(dbDir);
        DeleteQuery delete = new DeleteQuery(command);
        assertTrue(delete.checkFrom());
        assertTrue(delete.checkTablename());
        assertTrue(delete.checkWhere());
        assertFalse(delete.checkBracket());
        assertTrue(delete.checkPlainText("age"));
        assertTrue(delete.validOperation());
    }

    @Test
    void testDelete1(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "frOm", "table2", "where", "*ge", "<", "5;"};
        server = new DBServer(dbDir);
        DeleteQuery delete = new DeleteQuery(command);
        assertTrue(delete.checkFrom());
        assertTrue(delete.checkTablename());
        assertTrue(delete.checkWhere());
        assertFalse(delete.checkBracket());
        assertFalse(delete.checkPlainText("*ge"));
        assertFalse(delete.validOperation());
    }

    @Test
    void testDelete2(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "frOm", "table2", "where", "age", ">=", "!5.2;"};
        server = new DBServer(dbDir);
        DeleteQuery delete = new DeleteQuery(command);
        assertTrue(delete.checkFrom());
        assertTrue(delete.checkTablename());
        assertTrue(delete.checkWhere());
        assertFalse(delete.checkBracket());
        assertTrue(delete.checkPlainText(command[4]));
        assertTrue(delete.checkOperator(command[5]));
        assertFalse(delete.validOperation());
    }


    @Test
    void testDeleteBracket(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "frOm", "table2", "where", "(pass", "==", "FALSE)", "AND", "(mark", ">", "35)"};
        String command2 = "Delete from Table2 where (pass == FALSE) and (mark > 35)";
        server = new DBServer(dbDir);
        DeleteQuery delete = new DeleteQuery(command);
        assertTrue(delete.checkFrom());
        assertTrue(delete.checkTablename());
        assertTrue(delete.checkWhere());
        assertTrue(delete.checkBracket());
        assertTrue(delete.splitOnBrackets(command2));
        assertTrue(delete.parseDelete());
    }

    @Test
    void testDeleteBracket2(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "frOm", "table2", "where", "pass", "==", "FALSE)", "AND", "(mark", ">", "35)"};
        server = new DBServer(dbDir);
        DeleteQuery delete = new DeleteQuery(command);
        assertTrue(delete.checkFrom());
        assertTrue(delete.checkTablename());
        assertTrue(delete.checkWhere());
        assertFalse(delete.checkBracket());
    }

    @Test
    void testDeleteNoWhere(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "frOm", "table2", "from",  "age", "<", "5;"};
        server = new DBServer(dbDir);
        DeleteQuery delete = new DeleteQuery(command);
        assertTrue(delete.checkFrom());
        assertTrue(delete.checkTablename());
        assertFalse(delete.checkWhere());
    }

    @Test
    void testDeleteNotValidTableName(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "frOm", "t!!able2", "where", "age", "<", "5;"};
        server = new DBServer(dbDir);
        DeleteQuery delete = new DeleteQuery(command);
        assertTrue(delete.checkFrom());
        assertFalse(delete.checkTablename());
    }

    @Test
    void testDeleteNoFrom(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "table2", "where", "age", "<", "5;"};
        server = new DBServer(dbDir);
        DeleteQuery delete = new DeleteQuery(command);
        assertTrue(delete.checkCommandLength());
        assertFalse(delete.checkFrom());
    }

    @Test
    void testDeleteTooShort(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "from   ;",};
        server = new DBServer(dbDir);
        final DeleteQuery[] testDelete = new DeleteQuery[1];
        assertThrows(DBExceptions.InvalidSyntax.class, ()-> testDelete[0] = new DeleteQuery(command));
    }

    @Test
    void testDelete101(@TempDir File dbDir) throws DBExceptions.InvalidSyntax {
        String[] command = {"Delete", "frOm", "table2", "where", "(Name", "==", "'Miranda')", "AND", "(age", ">", "5)"};
        server = new DBServer(dbDir);
        DeleteQuery delete = new DeleteQuery(command);
        assertTrue(delete.checkFrom());
        assertTrue(delete.checkTablename());
        assertTrue(delete.checkWhere());
        assertTrue(delete.checkBracket());
        assertTrue(delete.parseDelete());
    }

    @Test
    void testJoin101(@TempDir File dbDir){
        String[] command = {"JOIN", "table1", "and", "table2", "on", "Name", "AND", "ID" };
        server = new DBServer(dbDir);
        JoinQuery join = new JoinQuery(command);
        assertTrue(join.parseJoin());
    }

    @Test
    void testJoin102(@TempDir File dbDir){
        String[] command = {"JOIN", "and", "table2", "on", "Name", "AND", "ID" };
        server = new DBServer(dbDir);
        JoinQuery join = new JoinQuery(command);
        assertFalse(join.parseJoin());
    }


  @Test
  void testInsertingACol(){
      String command1 = "USE myDB3;";
      server.handleCommand(command1);
      String command2 = "ALTER TABLE libraries ADD temperature;";
      server.handleCommand(command2);
  }

    @Test
    void testDeleteACol(){
        String command1 = "USE myDB3;";
        server.handleCommand(command1);
        String command2 = "ALTER TABLE libraries drop location;";
        server.handleCommand(command2);
    }
/*
    @Test
    void testSelect() throws DBExceptions {
      String command1 = "Select * from marks where (pass == FALSE) AND (mark > 35);";
      final Parser[] testParser = new Parser[1];
      assertThrows(DBExceptions.UnableToExecuteCommand.class, ()-> testParser[0] = new Parser(server, command1, dbDir1, server.databaseList));
    } */


    @Test
    void testDelete111(){
        String command4 = "USE myDB1;";
        String command5 = "delete from names where nickname == 'Ollie';";
        server.handleCommand(command4);
        server.handleCommand(command5);
    }

    @Test
    void createDBTest(){
        String command2 = "use db100;";
        server.handleCommand(command2);
        String command7 = "delete from pets where breed LIKE 'dgs';";
        server.handleCommand(command7);
    }

    @Test
    void testInsertStringStuff111(){
        String command4 = "USE myDB1;";
        server.handleCommand(command4);
        String command6 = "DELETE from names where nickname == 'Ollie';";
        server.handleCommand(command6);
    }

    @Test
    void testInsertStringStuff112(){
        String command1 = "USE myDB3;";
        String command3 = "DELETE FROM libraries WHERE (books > 50) AND (location == 'Clifton');";
        server.handleCommand(command1);
        server.handleCommand(command3);
    }

    @Test
    void testSelect111() throws DBExceptions {
        String[] command = {"SELECT", "nicknames", "FROM", "names"};
        SelectQuery thisSelect = new SelectQuery(command, server);
        assertTrue(thisSelect.checkWildAttributeList());
        assertTrue(thisSelect.checkFrom());
        assertTrue(thisSelect.checkTablename());
        assertFalse(thisSelect.checkIfConditonal());
    }

    @Test
    void testSelect112() throws DBExceptions {
        String[] command = {"SELECT", "nicknames", "FROM", "names", "where", "nickname", "LIKE", "'ly'"};
        SelectQuery thisSelect = new SelectQuery(command, server);
        assertTrue(thisSelect.checkWildAttributeList());
        assertTrue(thisSelect.checkFrom());
        assertTrue(thisSelect.checkTablename());
        assertTrue(thisSelect.checkIfConditonal());
        assertTrue(thisSelect.checkIfWhere());
        assertTrue(thisSelect.checkValidOperation());
    }


    @Test
    void testInvalidTokeniser() throws DBExceptions{
        String command = "yabba yabba yabba;";
        final Tokeniser[] testTokeniser1 = new Tokeniser[1];
        assertThrows(DBExceptions.InvalidQueryType.class, ()-> testTokeniser1[0] = new Tokeniser(command));
    }


    @Test
    void testDeleteFromTable() throws DBExceptions{
        String command = "Use myDB2;";
        server.handleCommand(command);
        String command2 = "Delete from sheds where Name == 'Plaza';";
        server.handleCommand(command2);
    }

    @Test
    void testUseDB(@TempDir File dbDir){
        String command = "create database myDB2;";
        String command1 = "use myDB2;";
        String command2 = "create table Miranda;";
        String command3 = "drop table Miranda;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
    }

 /*   @Test
    void testDropWhenTableDoesntExist(@TempDir File dbDir) {
        String command1 = "use myDB1;";
        server.handleCommand(command1);
        String command2 = "drop table miranda;";
        server.handleCommand(command2);
        final Parser[] testParser = new Parser[1];
        assertThrows(DBExceptions.TableDoesNotExist.class, ()-> testParser[0] = new Parser(server, command2, dbDir, server.databaseList));
    } */

    @Test
    void testInvalidDrop(@TempDir File dbDir) {
        String command1 = "drop myDB11;";
        final Parser[] testParser = new Parser[1];
        assertThrows(DBExceptions.InvalidCommand.class, ()-> testParser[0] = new Parser(server, command1, dbDir, server.databaseList));
    }

    @Test
    void testDropWhenDBDoesntExist(@TempDir File dbDir) {
        String command1 = "drop database myDB11;";
        final Parser[] testParser = new Parser[1];
        assertThrows(DBExceptions.UnableToExecuteCommand.class, ()-> testParser[0] = new Parser(server, command1, dbDir, server.databaseList));
    }

    @Test
    void testFindLikeMatch() {
        String command = "create database myDB1;";
        String command1 = "use myDB1;";
        String command2 = "create table names (name, age, nickname);";
        String command3 = "insert into names values ('Miranda', 23, 'Mimi');";
        String command4 = "insert into names values ('Matilda', 21, 'Tilly');";
        String command5 = "select * from names where nickname LIKE 'ly';";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
    }

    @Test
    void testFindColMatch() {
        String command = "create database myDB1;";
        String command1 = "use myDB1;";
        String command2 = "create table names (name, age, nickname);";
        String command3 = "insert into names values ('Miranda', 23, 'Mimi');";
        String command4 = "insert into names values ('Hannah', 21, 'Han');";
        String command5 = "select nickname from names;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
    }

    @Test
    void testFindAllMatches() {
        String command = "create database myDB1;";
        String command1 = "use myDB1;";
        String command2 = "create table names (name, age, nickname);";
        String command3 = "insert into names values ('Miranda', 23, 'Mimi');";
        String command4 = "insert into names values ('Hannah', 21, 'Han');";
        String command5 = "select * from names;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
    }

//    @Test
//    void testFindConditionalMatches() {
//        String command = "use myDB3;";
//        server.handleCommand(command);
//        String command2 = "select name from libraries where location LIKE 'ton';";
//        server.handleCommand(command2);
//        assertTrue(server.handleCommand(command).startsWith("[OK]"));
//        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
//    }

    @Test
    void testAddColAndSave() throws DBExceptions {
        String command = "use myDB3;";
        server.handleCommand(command);
        String command2 = "alter table libraries add id;";
        server.handleCommand(command2);
    }

    @Test
    void selectAllFromWhere(){
        String command1 = "use myDB1;";
        server.handleCommand(command1);
        String command2 = "SELECT * FROM marks WHERE name != 'Dave';";
        server.handleCommand(command2);
    }


    @Test
    void parseUpdate(){
        String[] command = {"UPDATE", "marks", "SET", "mark", "=", "38", "WHERE", "name", "==", "'Clive'"};
        UpdateQuery update = new UpdateQuery(command);
        assertTrue(update.parseUpdate());
        assertTrue(update.checkSet());
        assertTrue(update.checkPlainText(command[3]));
        assertTrue(update.checkThisValue(command[5]));
    }

    @Test
    void parseUpdate1(){
        String[] command = {"UPDATE", "m@rks", "SET", "mark", "=", "38", "WHERE", "name", "==", "'Clive'"};
        UpdateQuery update = new UpdateQuery(command);
        assertFalse(update.parseUpdate());
    }

    @Test
    void parseUpdate2(){
        String[] command = {"UPDATE", "m@rks", "SET", "mark", "!", "38", "WHERE", "name", "==", "Clive"};
        UpdateQuery update = new UpdateQuery(command);
        assertFalse(update.parseUpdate());
    }

    @Test
    void parseUpdate3(){
        String[] command = {"UPDATE", "m@rks", "SET", "mark", "!", "38", "WERE", "name", "==", "Clive"};
        UpdateQuery update = new UpdateQuery(command);
        assertFalse(update.parseUpdate());
    }

    @Test
    void parseUpdate4(){
        String[] command = {"UPDATE", "marks", "SET", "mark", "=", "38", "WHERE", "(name", "==", "'Clive')", "AND", "(age", ">", "30)"};
        UpdateQuery update = new UpdateQuery(command);
        assertTrue(update.parseUpdate());
        assertTrue(update.checkSet());
        assertTrue(update.checkPlainText(command[3]));
        assertTrue(update.checkThisValue(command[5]));
    }

   @Test
    void parseUpdate5() throws DBExceptions{
        String command = "create database myDB1;";
        String command1 = "use myDB1;";
        String command2 = "create table marks (name, grade, pass);";
        String command3 = "insert into marks values ('Clive', 50, TRUE);";
        String command4 = "insert into marks values ('Hannah', 100, TRUE);";
        String command5 = "Update marks set mark = 95 WHERE name LIKE 'ive';";
        String command6 = "select * from marks;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
        assertTrue(server.handleCommand(command6).startsWith("[OK]"));
    }

    @Test
    void testSelectNew(){
        String command = "create database myDB1;";
        String command1 = "use myDB1;";
        String command2 = "create table marks (name, grade, pass);";
        String command3 = "insert into marks values ('Toby', 50, TRUE);";
        String command4 = "insert into marks values ('Hannah', 100, TRUE);";
        String command5 = "select * from marks;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
    }


    @Test
    void testSelectNew3(){
        String command = "create database db100;";
        String command1 = "use db100;";
        String command2 = "create table pets (name, owner, age);";
        String command3 = "insert into pets values ('Fido', 'Miranda', 3);";
        String command4 = "insert into pets values ('Rudy', 'Oscar', 2);";
        String command5 = "select owner from pets where age > 2.5;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
    }

    @Test
    void testSelectNew4(){
        String command = "create database db100;";
        String command1 = "use db100;";
        String command2 = "create table pets (name, owner, age);";
        String command3 = "insert into pets values ('Fido', 'Miranda', 3);";
        String command4 = "insert into pets values ('Rudy', 'Oscar', 2);";
        String command5 = "select name from pets where age != 2.5;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
    }

    @Test
    void testSelectNew5(){
        String command = "create database db100;";
        String command1 = "use db100;";
        String command2 = "create table pets (name, owner, age);";
        String command3 = "insert into pets values ('Fido', TRUE, 3);";
        String command4 = "insert into pets values ('Rudy', FALSE, 2);";
        String command5 = "select name from pets where owner != TRUE;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
    }

    @Test
    void testSelectNew6(){
        String command = "create database db100;";
        String command1 = "use db100;";
        String command2 = "create table pets (name, owner, age);";
        String command3 = "insert into pets values ('Fido', TRUE, 3);";
        String command4 = "insert into pets values ('Rudy', FALSE, 2);";
        String command5 = "select * from pets where owner != FALSE;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
    }

    @Test
    void parseUpdate33(){
        String[] command = {"UPDATE", "marks", "SET", "m@rk", "=", "38,", "age", "=", "20", "WHERE", "name", "==", "Clive"};
        UpdateQuery update = new UpdateQuery(command);
        assertFalse(update.parseUpdate());
        assertFalse(update.checkPlainText(command[3]));
    }

    @Test
    void parseUpdate44(){
        String[] command = {"UPDATE", "marks", "SET", "mark", "=", "38,", "age", "=", "20", "name", "==", "Clive"};
        UpdateQuery update = new UpdateQuery(command);
        assertFalse(update.parseUpdate());
    }

    @Test
    void testALot(){
        String command = "create database testDB1;";
        String command1 = "create database testDB2;";
        String command2 = "use testDB1;";
        String command3 = "create table pets (name, owner, age);";
        String command4 = "insert into pets values ('Fido', TRUE, 3);";
        String command5 = "insert into pets values ('Rudy', FALSE, 2);";
        String command6 = "select * from pets where owner != FALSE;";
        String command7 = "use testDB2;";
        String command8 = "create table people (id, Name, Age, Email);";
        String command9 = "insert into people values (1, 'Bob', 21, 'bob@bob.ac.uk');";
        String command10 = "insert into people values (2, 'Harry', 32, 'harry@harry.com');";
        String command11 = "insert into people values (3, 'Chris', 42, 'chris@chris.ac.uk');";
        String command12 = "select * from people where Age >= 30;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
        assertTrue(server.handleCommand(command6).startsWith("[OK]"));
        assertTrue(server.handleCommand(command7).startsWith("[OK]"));
        assertTrue(server.handleCommand(command8).startsWith("[OK]"));
        assertTrue(server.handleCommand(command9).startsWith("[OK]"));
        assertTrue(server.handleCommand(command10).startsWith("[OK]"));
        assertTrue(server.handleCommand(command11).startsWith("[OK]"));
        assertTrue(server.handleCommand(command12).startsWith("[OK]"));
    }

    @Test
    void testALot2(){
        String command = "create database thisDB1;";
        String command1 = "create database thisDB2;";
        String command2 = "use thisDB2;";
        String command3 = "create table pets (name, owner, age);";
        String command4 = "insert into pets values ('Fido', TRUE, 3);";
        String command5 = "insert into pets values ('Rudy', FALSE, 2);";
        String command6 = "select name from pets where age >= 2.555;";
        String command7 = "use thisDB1;";
        String command8 = "create table people (id, Name, Age, Email);";
        String command9 = "insert into people values (1, 'Bob', 21, 'bob@bob.ac.uk');";
        String command10 = "insert into people values (2, 'Harry', 32, 'harry@harry.com');";
        String command11 = "insert into people values (3, 'Chris', 42, 'chris@chris.ac.uk');";
        String command12 = "select Email from people where Name LIKE 'is';";
        String command13 = "use thisDB2;";
        String command14 = "create table sheds (id, Name, Height, PurchaserID);";
        String command15 = "insert into sheds values (1, 'Dorchester', 1800, 3);";
        String command16 = "insert into sheds values (2, 'Plaza', 1200, 1);";
        String command17 = "insert into sheds values (3, 'Excelsior', 1000, 2);";
        String command18 = "select Name from sheds where Name LIKE 'sior';";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
        assertTrue(server.handleCommand(command6).startsWith("[OK]"));
        assertTrue(server.handleCommand(command7).startsWith("[OK]"));
        assertTrue(server.handleCommand(command8).startsWith("[OK]"));
        assertTrue(server.handleCommand(command9).startsWith("[OK]"));
        assertTrue(server.handleCommand(command10).startsWith("[OK]"));
        assertTrue(server.handleCommand(command11).startsWith("[OK]"));
        assertTrue(server.handleCommand(command12).startsWith("[OK]"));
        assertTrue(server.handleCommand(command13).startsWith("[OK]"));
        assertTrue(server.handleCommand(command14).startsWith("[OK]"));
        assertTrue(server.handleCommand(command15).startsWith("[OK]"));
        assertTrue(server.handleCommand(command16).startsWith("[OK]"));
        assertTrue(server.handleCommand(command17).startsWith("[OK]"));
        assertTrue(server.handleCommand(command18).startsWith("[OK]"));
    }

    @Test
    void testToFail(){
        String command = "create thisDB1;";
        String command1 = "create database thisDB2";
        String command2 = "use thisDB3;";
        String command22 = "create database thisDB1;";
        String command23 = "use thisDB1;";
        String command3 = "create table pets name, owner, age);";
        String command4 = "insert into pets values ('Fido', TRUE, 3;";
        String command44 = "create table pets;";
        String command45 = "create table people;";
        String command46 = "create table sheds;";
        String command5 = "insert into pets values (Rudy, FALSE, 2);";
        String command6 = "select from pets where age >= 2.555;";
        String command7 = "thisDB1;";
        String command8 = "create table people (id, N*me, Age, Email);";
        String command9 = "insert into people values (1, 'Bob', 21, 'bob@bob.ac.uk');";
        String command10 = "insert into people values (2, 'Harry', 32, 'harry@harry.com');";
        String command11 = "insert into people values (3, 'Chris', 42, 'chris@chris.ac.uk');";
        String command12 = "select Email from people where Name LIKE 'is';";
        String command13 = "use thisDB2;";
        String command14 = "create tble sheds (i   !d, Name, Height, PurchaserID);";
        String command15 = "insert into sheds values (1, Dorchester', 1800, 3);";
        String command16 = "insert into sheds values (2, 'Plaza', 1200, 1;";
        String command17 = "insert sheds values (3, 'Excelsior', 1000, 2) (rdtkhlkd);";
        String command18 = "select Name from sheds where Name LIKE 'sior';";
        assertTrue(server.handleCommand(command).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command1).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command2).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command22).startsWith("[OK]"));
        assertTrue(server.handleCommand(command23).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command4).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command44).startsWith("[OK]"));
        assertTrue(server.handleCommand(command45).startsWith("[OK]"));
        assertTrue(server.handleCommand(command46).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command6).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command7).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command8).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command9).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command10).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command11).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command12).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command13).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command14).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command15).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command16).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command17).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command18).startsWith("[ERROR]"));
    }

    @Test
    void testDeleting(){
        String command = "create database testDB1;";
        String command1 = "create database testDB2;";
        String command2 = "use testDB1;";
        String command3 = "create table pets (name, owner, age);";
        String command4 = "insert into pets values ('Fido', TRUE, 3);";
        String command5 = "insert into pets values ('Rudy', FALSE, 2);";
        String command6 = "delete from pets WHERE owner != FALSE;";
        String command7 = "use testDB2;";
        String command8 = "create table people (id, Name, Age, Email);";
        String command9 = "insert into people values (1, 'Bob', 21, 'bob@bob.ac.uk');";
        String command10 = "insert into people values (2, 'Harry', 32, 'harry@harry.com');";
        String command11 = "insert into people values (3, 'Chris', 42, 'chris@chris.ac.uk');";
        String command12 = "delete from people where Name LIKE 'ob';";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
        assertTrue(server.handleCommand(command6).startsWith("[OK]"));
        assertTrue(server.handleCommand(command7).startsWith("[OK]"));
        assertTrue(server.handleCommand(command8).startsWith("[OK]"));
        assertTrue(server.handleCommand(command9).startsWith("[OK]"));
        assertTrue(server.handleCommand(command10).startsWith("[OK]"));
        assertTrue(server.handleCommand(command11).startsWith("[OK]"));
        assertTrue(server.handleCommand(command12).startsWith("[OK]"));
    }

    @Test
    void testDeleting2(){
        String command = "create database testDB1;";
        String command1 = "create database testDB2;";
        String command2 = "use testDB1;";
        String command3 = "create table pets (name, owner, age);";
        String command4 = "insert into pets values ('Rudy', FALSE, 2);";
        String command5 = "alter table pets add id;";
        String command6 = "delete from pets WHERE owner != FALSE;";
        String command7 = "use testDB2;";
        String command8 = "create table people (id, Name, Age, Email);";
        String command9 = "drop table people;";
        String command10 = "insert into people values (2, 'Harry', 32, 'harry@harry.com');";
        String command11 = "drop database testDB2;";
        String command12 = "use testDB2;";
        assertTrue(server.handleCommand(command).startsWith("[OK]"));
        assertTrue(server.handleCommand(command1).startsWith("[OK]"));
        assertTrue(server.handleCommand(command2).startsWith("[OK]"));
        assertTrue(server.handleCommand(command3).startsWith("[OK]"));
        assertTrue(server.handleCommand(command4).startsWith("[OK]"));
        assertTrue(server.handleCommand(command5).startsWith("[OK]"));
        assertTrue(server.handleCommand(command6).startsWith("[OK]"));
        assertTrue(server.handleCommand(command7).startsWith("[OK]"));
        assertTrue(server.handleCommand(command8).startsWith("[OK]"));
        assertTrue(server.handleCommand(command9).startsWith("[OK]"));
        assertTrue(server.handleCommand(command10).startsWith("[ERROR]"));
        assertTrue(server.handleCommand(command11).startsWith("[OK]"));
        assertTrue(server.handleCommand(command12).startsWith("[ERROR]"));
    }

}

// This has arrayList, not 2D array

package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;

/** This class implements the DB server. */
public final class DBServer1 {

  private static final char END_OF_TRANSMISSION = 4;

  public static void main(String[] args) throws IOException {
    new DBServer1(Paths.get(".").toAbsolutePath().toFile()).blockingListenOn(8888);
  }

  /**
   * KEEP this signature (i.e. {@code edu.uob.DBServer(File)}) otherwise we won't be able to mark
   * your submission correctly.
   *
   * <p>You MUST use the supplied {@code databaseDirectory} and only create/modify files in that
   * directory; it is an error to access files outside that directory.
   *
   * @param databaseDirectory The directory to use for storing any persistent database files such
   *     that starting a new instance of the server with the same directory will restore all
   *     databases. You may assume *exclusive* ownership of this directory for the lifetime of this
   *     server instance.
   */
  public DBServer1(File databaseDirectory) {
    // TODO implement your server logic here

    String filename = "sheds";
    // will acc get this from the user input
    readFile(filename);
  }

  /**
   * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
   * able to mark your submission correctly.
   *
   * <p>This method handles all incoming DB commands and carry out the corresponding actions.
   */

  public static void readFile(String filename) {
    try {
      String filenameWithTab = filename + ".tab";
      String fileToCheck = filenameWithTab;
      // get the file name from the USER COMMAND
      ArrayList<String[]> myList = new ArrayList<String[]>();
      File fileToOpen = new File(fileToCheck);
      if(fileToOpen.exists()) {
        FileReader reader = new FileReader(fileToOpen);
        BufferedReader buffReader = new BufferedReader(reader);
       // String firstLine = buffReader.readLine();
        String line;
        int x = 0;
        int y;
        while( (line = buffReader.readLine()) != null){
         String[] lineToSplit = line.split("\t");
         myList.add(lineToSplit);
        // y = lineToSplit.length;
       //  System.out.println(line);
          //System.out.println(y);
        //  int i =0;
        //  System.out.println(myList.get(i)[j]);
        //  i++;
          x++;
        }
        y = myList.get(0).length;
       // System.out.println(y);

        int i;
        int j;
        for(i = 0; i < x; i++){
          for(j = 0; j < y; j++){
            System.out.print(myList.get(i)[j]);
            System.out.print(" ");
          }
          System.out.println(" ");
        }
        //System.out.println(myList.get(3)[1]);
        buffReader.close();
      }
      // dont need this?
      else{
        if(fileToOpen.createNewFile()) { // checking that new file has been successfully created...
          FileReader reader = new FileReader(fileToOpen);
          BufferedReader buffReader = new BufferedReader(reader);
          String firstLine = buffReader.readLine();
          buffReader.close();
        }
      }

      addToFile(myList);

    } catch (Exception IOException) {
      System.out.println("failed");
      IOException.printStackTrace();
    }
    //catch (Exception FileNotFoundException) {
    //}
  }

  public static void addToFile(ArrayList<String[]> myList){
    String addWord = "The Ivy";
    myList.get(3)[1] = addWord;
    printArrayList(myList);
    //Arrays.toString()
    for(int i = 0; i < 4; i++) {

      String[] thisString = myList.get(i);
      String addToLine;
     // System.out.println("ThisString: " + thisString);
      System.out.println("MEMEMEME");
      for(i = 0; i < 4; i ++){ // must not be 4!!!
        System.out.print(thisString[i] + " ");
        addToLine = thisString[i];
        //addLine = thisString[i].toString;
        System.out.println("Add to line: " + addToLine);
      }
      //String addLine = thisString.toString();
      //System.out.println("addLine: " + addLine);
    }

   // var weekdays= ["Mon", "Fri"] var weekStr = weekdays.join("\t"); // weekStr = "Mon\tTue\tWed\tthur\tFri"
  }

  public static void printArrayList(ArrayList<String[]> myList){
    int i;
    int j;
    for(i = 0; i < 4; i++){
      for(j = 0; j < 4; j++){
        System.out.print(myList.get(i)[j]);
        System.out.print(" ");
      }
      System.out.println(" ");
    }
  }

//  public static void splitInTabs(String line){
//    ArrayList<String> myList = new ArrayList<String>();
//    //String[] lineToSplit = line;
//    lineToSplit = line.split("\t");
//    // call this per line?
//    // create the arraylist outside of this method, and then call this method on each line, and
//    // add each line to the array list
//
//    //arraylist words;
//    // words.split("\t");
//    String[] columnDetail = new String[11];
//    columnDetail = column.split("\t");
//  }

  public class table{

    // fields
    public String tableName;

    // constructor class: this initialises instances (objects) of a class:
    public table(ArrayList<String[]> myList, String requiredTable){
      tableName = requiredTable;
      int cols = myList.get(0).length;
      int rows = myList.size();
     // System.out.println(cols);
     // System.out.println(rows);
      String[][] table = new String[rows][cols];
    }

    // methods go below:
     //public void addCol(){
     //}
  }

  public class alter{
    // fields
    //public int numberOfCols;

    // constructor class: this initialises instances (objects) of a class:
    public alter(String[][] table){
    //  int numberOfCols = table.cols;
    }

    // methods go below:
    public void addCol(){
      //return this.numberOfCols;
    }
  }

  public String handleCommand(String command) {
    // TODO implement your server logic here
    return "[OK] Thanks for your message: " + command;
  }

  //  === Methods below are there to facilitate server related operations. ===

  /**
   * Starts a *blocking* socket server listening for new connections. This method blocks until the
   * current thread is interrupted.
   *
   * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
   * you want to.
   *
   * @param portNumber The port to listen on.
   * @throws IOException If any IO related operation fails.
   */
  public void blockingListenOn(int portNumber) throws IOException {
    try (ServerSocket s = new ServerSocket(portNumber)) {
      System.out.println("Server listening on port " + portNumber);
      while (!Thread.interrupted()) {
        try {
          blockingHandleConnection(s);
        } catch (IOException e) {
          System.err.println("Server encountered a non-fatal IO error:");
          e.printStackTrace();
          System.err.println("Continuing...");
        }
      }
    }
  }

  /**
   * Handles an incoming connection from the socket server.
   *
   * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
   * * you want to.
   *
   * @param serverSocket The client socket to read/write from.
   * @throws IOException If any IO related operation fails.
   */
  private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
    try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

      System.out.println("Connection established: " + serverSocket.getInetAddress());
      while (!Thread.interrupted()) {
        String incomingCommand = reader.readLine();
        System.out.println("Received message: " + incomingCommand);
        String result = handleCommand(incomingCommand);
        writer.write(result);
        writer.write("\n" + END_OF_TRANSMISSION + "\n");
        writer.flush();
      }
    }
  }
}

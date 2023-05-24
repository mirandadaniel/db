package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.String;



/** This class implements the DB server. */
public final class DBServer {
  private String directory;
  String databaseName;
  File theDBDir;
  List<Database> databaseList = new ArrayList<>();
  String returnStatement = "    ";
  String path;


  private static final char END_OF_TRANSMISSION = 4;

  public static void main(String[] args) throws IOException {
    new DBServer(Paths.get(".").toAbsolutePath().toFile()).blockingListenOn(8888);
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
  public DBServer(File databaseDirectory) {
    // TODO implement your server logic here

    theDBDir = databaseDirectory;

    directory = databaseDirectory.getPath();
    if(directory.endsWith(".")){
      directory = directory.substring(0, directory.length()-1);
    }

  }

  /**
   * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
   * able to mark your submission correctly.
   *
   * <p>This method handles all incoming DB commands and carry out the corresponding actions.
   */

  public String getDirectory(){
    return this.directory;
  }


  public void setDatabaseName(String dbName){
    databaseName = dbName;
  }

  public String getDatabaseName(){
    return databaseName;
  }


  public String removeFullStop(String originalPath){
    int length = originalPath.length();
    char fullStopCheck = originalPath.charAt(length - 1);
    if(fullStopCheck == '.'){
      String newPath = originalPath.substring(0, originalPath.length()-1);
      return newPath;
    }
    return originalPath;
  }


  public String handleCommand(String command) {


    try {

      path = theDBDir.getPath();
      String newPath = removeFullStop(path);
      File passInDBDir = new File(newPath);
      path = passInDBDir.getPath();
      Parser thisParser = new Parser(this, command, passInDBDir, databaseList);
      if(thisParser.returnStatement != null) {
        returnStatement = thisParser.returnStatement;
      }
    }catch(Exception e){
      return "[ERROR]" + e.getMessage();
    }

    return "[OK] Thanks for your message: "  + command + "\n" + returnStatement;
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

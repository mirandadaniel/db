package edu.uob;

public class DBExceptions extends Exception {

    private static final long serialVersionUID = -2629325698018774532L;

    public DBExceptions(String message) {
        super(message);
    }


    public static class InvalidCommand extends DBExceptions {
        private static final long serialVersionUID = -2405736440769523511L;

        public InvalidCommand(String[] command){
            super("Command is invalid." + command);
        }

    }


    public static class MissingSemiColon extends DBExceptions {
        private static final long serialVersionUID = -240573645856743511L;

        public MissingSemiColon(){
            super("Command is missing semi-colon.");
        }

    }

    public static class InvalidQueryType extends DBExceptions {
        private static final long serialVersionUID = -240773645756743511L;

        public InvalidQueryType(){
            super("Query type is invalid.");
        }

    }

    public static class InvalidTokenization extends DBExceptions {
        private static final long serialVersionUID = -2405745445511L;

        public InvalidTokenization(){
            super("Unable to tokenize this query.");
        }

    }

    public static class TableDoesNotExist extends DBExceptions {
        private static final long serialVersionUID = -24057775511L;

        public TableDoesNotExist(){
            super("Requested table does not exist.");
        }

    }

    public static class DatabaseDoesNotExist extends DBExceptions {
        private static final long serialVersionUID = -24033775511L;

        public DatabaseDoesNotExist(){
            super("Requested database does not exist.");
        }

    }

    public static class FailedToDeleteFile extends DBExceptions {
        private static final long serialVersionUID = -24443775511L;

        public FailedToDeleteFile(){
            super("Failed to delete file.");
        }

    }

    public static class FailedToDeleteDB extends DBExceptions {
        private static final long serialVersionUID = -243413775511L;

        public FailedToDeleteDB(){
            super("Failed to delete the database.");
        }

    }

    public static class UnableToExecuteCommand extends DBExceptions {
        private static final long serialVersionUID = -24443775511L;

        public UnableToExecuteCommand(){
            super("Unable to execute command.");
        }

    }

    public static class EmptyTableList extends DBExceptions {
        private static final long serialVersionUID = -24443775511L;

        public EmptyTableList(){
            super("Unable to execute command.");
        }

    }

    public static class ColumnDoesntExist extends DBExceptions {
        private static final long serialVersionUID = -24443733511L;

        public ColumnDoesntExist(){
            super("Unable to execute command.");
        }

    }

    public static class CantHandleCommand extends DBExceptions {
        private static final long serialVersionUID = -24433733511L;

        public CantHandleCommand(){
            super("This query is too complex to execute.");
        }

    }

    public static class NoConditionMatchFound extends DBExceptions {
        private static final long serialVersionUID = -244311733511L;

        public NoConditionMatchFound(){
            super("No matching column found to delete from.");
        }

    }

    public static class NullTable extends DBExceptions {
        private static final long serialVersionUID = -2447733511L;

        public NullTable(){
            super("No matching column found to delete from.");
        }

    }

    public static class UnsuccessfulDirCreation extends DBExceptions {
        private static final long serialVersionUID = -24477332111L;

        public UnsuccessfulDirCreation(){
            super("Unable to create the specified database.");
        }

    }

    public static class TableAlreadyExists extends DBExceptions {
        private static final long serialVersionUID = -244225332111L;

        public TableAlreadyExists(){
            super("The table to create already exists.");
        }

    }

    public static class TableDoesntExist extends DBExceptions {
        private static final long serialVersionUID = -244225332111L;

        public TableDoesntExist(){
            super("The table to create already exists.");
        }

    }

    public static class InvalidSyntax extends DBExceptions {
        private static final long serialVersionUID = -24488332111L;

        public InvalidSyntax(){
            super("Invalid command syntax");
        }

    }





}


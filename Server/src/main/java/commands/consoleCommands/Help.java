package commands.consoleCommands;

import commands.Receiver;

import java.io.Serializable;


/**
 * This command prints information about all available commands
 */

public class Help implements Command, Serializable {
    public String name = "help";
    public String getName(){
        return name;
    }

    public String required = "";
    public String getRequired(){
        return required;
    }

    public final static String[] args = new String[0];
    public static String[] inputs = new String[0];
    public String[] getInputs() {
        return inputs;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean complicated = false;

    public boolean isComplicated(){
        return complicated;
    }

    boolean isAuthorizationRequired = false;
    public boolean isAuthorizationRequired(){
        return isAuthorizationRequired;
    };

    Receiver receiver;
    public Help (Receiver receiver){
        this.receiver = receiver;
    }

    @Override
    public void execute(String[] arguments, String path, boolean isScript, Object object, Integer ID) {
        receiver.helpCommand(arguments, path, isScript, object, ID);
    }

    @Override
    public String[] args() {
        return args;
    }
}

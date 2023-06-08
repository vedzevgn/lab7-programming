package commands.consoleCommands;

import commands.Receiver;

import java.io.IOException;
import java.io.Serializable;

/**
 * This command executes commands from the script file.
 */

public class Execute implements Command, Serializable {
    public String name = "execute_script";
    public String getName(){
        return name;
    }

    public String required = "";
    public String getRequired(){
        return required;
    }

    public final static String[] args = {"имя файла"};
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

    boolean isAuthorizationRequired = true;
    public boolean isAuthorizationRequired(){
        return isAuthorizationRequired;
    };

    Receiver receiver;
    public Execute (Receiver receiver){
        this.receiver = receiver;
    }

    @Override
    public void execute(String[] arguments, String path, boolean isScript, Object object, Integer ID) throws IOException {
        receiver.executeCommand(arguments, path, isScript, object);
    }

    @Override
    public String[] args() {
        return args;
    }
}

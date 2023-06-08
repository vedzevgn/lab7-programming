package commands.consoleCommands;

import commands.Receiver;

import java.io.IOException;
import java.sql.SQLException;

public class SignUp implements Command{
    public String name = "sign_up";
    public String getName(){
        return name;
    }

    public String required = "";
    public String getRequired(){
        return required;
    }

    public final static String[] args = new String[3];
    public static String[] inputs = new String[3];
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
    public SignUp(Receiver receiver){
        this.receiver = receiver;
    }

    public void execute(String[] arguments, String path, boolean isScript, Object object, Integer ID) throws SQLException, IOException {
        receiver.signUpCommand(arguments, path, isScript, object);
    }

    @Override
    public String[] args() {
        return args;
    }
}

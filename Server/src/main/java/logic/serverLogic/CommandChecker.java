package logic.serverLogic;

import java.util.Objects;

public class CommandChecker {
    public boolean isExecute(String line){
        return Objects.equals(line.split(" ")[0], "execute_script");
    }
}

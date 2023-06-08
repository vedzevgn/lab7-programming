package responces;

import java.io.Serializable;

public enum CommandType implements Serializable {
    OBJECT,
    POST_OBJECT,
    NON_ARGUMENT,
    ARGUMENT,
    EXIT,
    EXECUTE,
    LOGIN,
    SIGN_UP,
    AUTHORIZATION_REQUIRED,
    WITHOUT_AUTHORIZATION,
    LOGOUT
}

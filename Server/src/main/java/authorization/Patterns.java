package authorization;

public class Patterns {
    public static final String getUserIDbyLogin = """
                        SELECT user_id
                        FROM users
                        WHERE login = ?;
                        """;

    public static final String getNameOfUser = """
                        SELECT full_name
                        FROM users
                        WHERE login = ? and password = ?;
                        """;

    public static final String checkLoginInfo = """
                        SELECT user_id
                        FROM users
                        WHERE login = ? AND password = ?;
                        """;

    public static final String getSalt = """
                        SELECT salt
                        FROM users
                        WHERE login = ?;
                        """;

    public static final String signUp = """
                        INSERT INTO users (full_name, login, password, salt)
                        VALUES (?, ?, ?, ?);
                        """;

    public static final String userIDFromUsername = """
                SELECT
                        users.id
                FROM
                        users
                WHERE
                        users.login = ?;
                """;
}

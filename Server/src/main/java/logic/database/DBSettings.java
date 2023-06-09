package logic.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBSettings {

    private String URL = "jdbc:postgresql://localhost:5432/";
    private String password;
    private String userName;


    public DBSettings(String userName, String password, String URLTail) {
        this.password = password;
        this.userName = userName;
        this.URL = URL + URLTail;
        /*Properties props = new Properties();
        try(InputStream resources = DBSettings.class.getResourceAsStream("/config.properties")) {
            if(resources != null) {
                props.load(resources);
                URL = props.getProperty("db_url");

                String pgpass = props.getProperty("pgpass");
                if(pgpass != null) {
                    try {
                        PGParser pgParser = new PGParser(pgpass);
                        userName = pgParser.getUserName();
                        password = pgParser.getPassword();
                    } catch (Exception ignored) {}
                }

                userName = props.getProperty("user_name");
                password = props.getProperty("user_password");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public String getURL() {
        return URL;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}

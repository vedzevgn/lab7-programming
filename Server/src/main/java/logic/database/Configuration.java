package logic.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private String dbURL = "jdbc:postgresql://localhost:5432/postgres";
    private String password = "18381838";
    private String userName = "postgres";


    public Configuration() {
        Properties props = new Properties();
        try(InputStream resources = Configuration.class.getResourceAsStream("/config.properties")) {

            if(resources != null) {
                props.load(resources);
                dbURL = props.getProperty("db_url");

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
        }
    }

    public String getDbURL() {
        return dbURL;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}

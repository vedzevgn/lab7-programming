package data.collectionTools;

import logic.database.DBConnection;
import parameters.Coordinates;
import parameters.MusicBand;
import parameters.MusicGenre;
import parameters.Studio;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static data.collectionTools.Queries.*;

public class DBCollectionLoader {
    private final DBConnection connection;

    public DBCollectionLoader(DBConnection connection) {
        this.connection = connection;
    }

    public void checkTablesExists(Connection connection) throws SQLException {
        boolean exists = false;
        PreparedStatement checkTableSt = connection.prepareStatement(checkTableExists);
        checkTableSt.setString(1, "bands");
        ResultSet rs = checkTableSt.executeQuery();
        if (rs.next()) {
            exists = rs.getBoolean(1);
        }
        if(!exists){
            PreparedStatement createBandsTable = connection.prepareStatement(Queries.createBandsTable);
            createBandsTable.executeUpdate();
        }

        exists = false;
        checkTableSt = connection.prepareStatement(checkTableExists);
        checkTableSt.setString(1, "users");
        rs = checkTableSt.executeQuery();
        if (rs.next()) {
            exists = rs.getBoolean(1);
        }
        if(!exists){
            PreparedStatement createBandsTable = connection.prepareStatement(createUsersTable);
            createBandsTable.executeUpdate();
        }
    }
    
    public ArrayList<MusicBand> loadCollection(DBConnection connection) {
        ArrayList<MusicBand> list = new ArrayList<MusicBand>();

        try(Statement statement = connection.getDBConnection().createStatement()) {
            ResultSet set = statement.executeQuery(start);
            while (set.next()) {
                MusicBand band = new MusicBand();
                band.setId(set.getLong("band_id"));
                band.setName(set.getString("band_name"));
                
                if (!set.wasNull()) {
                    Coordinates coordinates = new Coordinates();
                    coordinates.setX(set.getDouble("x_coordinate"));
                    coordinates.setY(set.getFloat("y_coordinate"));
                    band.setCoordinates(coordinates);
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-d");
                //band.setCreationDate(LocalDate.pasete(set.getObject("creationdate"), formatter));
                //band.setCreationDate(set.getObject("creationdate", OffsetDateTime.class).toZonedDateTime());
                Studio studio = new Studio();
                studio.setName(set.getString("studio_name"));
                band.setStudio(studio);
                band.setGenre(MusicGenre.valueOf(set.getString("genre").toUpperCase()));
                band.setNOP(set.getLong("nop"));
                band.setUserID(set.getInt("user_id"));
                list.add(band);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return list;
    }
}

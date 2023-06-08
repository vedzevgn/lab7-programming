package data.collectionTools;

import logic.database.DBConnection;
import parameters.Coordinates;
import parameters.MusicBand;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static data.collectionTools.Queries.*;

public class DBCollectionManager {

    java.sql.Connection connection;

    public DBCollectionManager(Connection connection) {
        this.connection = connection;
    }
    public void removeBand(Long id) throws SQLException {
        PreparedStatement remove = connection.prepareStatement(removeBand);
        remove.setLong(1, id);
        remove.executeUpdate();
    }

    public Integer getMaxID() throws SQLException {
        Integer ID = 0;
        PreparedStatement currentBand = connection.prepareStatement(maxID);
        ResultSet maxID = currentBand.executeQuery();
        maxID.next();
        ID = maxID.getInt(1);
        System.out.println(ID);
        return ID;
    }

    public Integer addBand(MusicBand band, int userID) throws SQLException {
        int ID;
        PreparedStatement addStatement = connection.prepareStatement(addBand);
        addStatement.setString(1, band.getName());
        addStatement.setString(2, String.valueOf(band.getGenre()));
        addStatement.setDouble(3, band.getCoordinates().getX());
        addStatement.setFloat(4, band.getCoordinates().getY());
        addStatement.setString(5, band.getStudio().getName());
        addStatement.setLong(6, band.getNOP());
        addStatement.setString(7, String.valueOf(band.getCreationDate()));
        addStatement.setInt(8, userID);
        addStatement.executeUpdate();
        PreparedStatement currentBand = connection.prepareStatement(current);
        currentBand.setString(1, "bands_band_id_seq");
        ResultSet current = currentBand.executeQuery();
        current.next();
        ID = current.getInt(1);
        System.out.println(ID);
        return ID;
    }

    public boolean updateBand(MusicBand band, int IDtoUpdate, int userID) throws SQLException {
        PreparedStatement exists = connection.prepareStatement(isExists);
        exists.setInt(1, IDtoUpdate);
        ResultSet exResult = exists.executeQuery();
        if(exResult.next()) {
            PreparedStatement updateStatement = connection.prepareStatement(updateBand);
            updateStatement.setString(1, band.getName());
            updateStatement.setString(2, String.valueOf(band.getGenre()));
            updateStatement.setDouble(3, band.getCoordinates().getX());
            updateStatement.setFloat(4, band.getCoordinates().getY());
            updateStatement.setString(5, band.getStudio().getName());
            updateStatement.setLong(6, band.getNOP());
            updateStatement.setString(7, String.valueOf(band.getCreationDate()));
            updateStatement.setInt(8, userID);
            updateStatement.setInt(9, IDtoUpdate);
            updateStatement.executeUpdate();
            return true;
        }
        return false;
    }

    public void removeBandsOfUser(Integer ID) throws SQLException {
        PreparedStatement addStatement = connection.prepareStatement(removeBandsOfUser);
        addStatement.setInt(1, ID);
        addStatement.executeUpdate();
    }
}

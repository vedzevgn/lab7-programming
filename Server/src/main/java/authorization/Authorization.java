package authorization;

import responces.LoginState;
import responces.SignupState;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static authorization.Patterns.*;

public class Authorization {
    private static final String pepper = "^p^p^r^";
    private static MessageDigest md5;
    private Connection connection;

    public Authorization(Connection dbConnection) throws FileNotFoundException, SQLException {
        try {
            this.connection = dbConnection;
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
            throw new RuntimeException();
        }
    }

    public boolean isUserExists(String user) {
        try {
            PreparedStatement userStatement = connection.prepareStatement(getUserIDbyLogin);
            userStatement.setString(1, user);
            ResultSet result = userStatement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Integer getUserID(String username) {
        try {
            PreparedStatement userStatement = connection.prepareStatement(getUserIDbyLogin);
            userStatement.setString(1, username);
            ResultSet result = userStatement.executeQuery();
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            System.out.println("Пользователь не существует.");
            return 0;
        }
    }

    private byte[] getSalt(String user) throws SQLException{
            PreparedStatement saltStatement = connection.prepareStatement(getSalt);
            saltStatement.setString(1, user);
            ResultSet saltRS = saltStatement.executeQuery();
            saltRS.next();
            return saltRS.getBytes(1);
    }

    private byte[] getPasswordHash(byte[] salt, String password) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(pepper.getBytes());
            baos.write(password.getBytes());
            baos.write(salt);
        } catch (IOException ignored) {}

        return md5.digest(baos.toByteArray());
    }

    private static byte[] getSalt() {
        byte[] saltBytes = new byte[4];
        new Random().nextBytes(saltBytes);
        return saltBytes;
    }

    public boolean logIn(String username, String password) throws SQLException {
        PreparedStatement checkUser = connection.prepareStatement(checkLoginInfo);
        checkUser.setString(1, username);
        byte[] hash;
        try {
            hash = getPasswordHash(getSalt(username), password);
            checkUser.setBytes(2, hash);
        } catch (SQLException e) {
            return false;
        }
        ResultSet userExists = checkUser.executeQuery();
        if(userExists.next()) {
            //System.out.println("ok");
            return true;
        }
        return false;
    }

    public boolean signUp(String fullName, String username, String password) throws SQLException {
        //System.out.println(fullName + username + password);
        byte[] salt = getSalt();
        byte[] hash = getPasswordHash(salt, password);

        PreparedStatement checkUser = connection.prepareStatement(checkIfUserExists);
        checkUser.setString(1, username);
        ResultSet userExists = checkUser.executeQuery();
        if(userExists.next()) {
            return false;
        } else {
            PreparedStatement addUser = connection.prepareStatement(signUp);
            addUser.setString(1, fullName);
            addUser.setString(2, username);
            addUser.setBytes(3, hash);
            addUser.setBytes(4, salt);
            addUser.executeUpdate();
            return true;
        }
    }

    public String getName(String username, String password) throws SQLException {
        PreparedStatement checkUser = connection.prepareStatement(getNameOfUser);
        checkUser.setString(1, username);
        System.out.println(username);
        byte[] hash = getPasswordHash(getSalt(username), password);
        checkUser.setBytes(2, hash);
        System.out.println(hash);
        ResultSet userExists = checkUser.executeQuery();
        userExists.next();
        return userExists.getString(1);
    }
}

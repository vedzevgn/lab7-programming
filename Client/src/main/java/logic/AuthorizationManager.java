package logic;

import connection.ChannelConnection;
import connection.Connection;
import exceptions.InvalidResponseException;
import requests.RequestBuilder;
import responces.Response;
import responces.ResponseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static logic.ConsoleManager.loggedIn;

public class AuthorizationManager {
    Connection connection;
    public AuthorizationManager(Connection connection){
        this.connection = connection;
    }
    BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));

    public String[] login() throws IOException, ClassNotFoundException {
        String[] authData = new String[2];
        String[] nullData = {"", ""};
        System.out.print("Введите имя пользователя: ");
        authData[0] = scanner.readLine();
        System.out.print("Введите пароль: ");
        authData[1] = scanner.readLine();
        System.out.println();
        connection.send(new RequestBuilder().buildRequest("login " + authData[0] + " " + authData[1], authData, null, authData[0], authData[1]));
        try {
            if(Objects.equals(new ResponseHandler().getText((Response) connection.receive()), authData[0])) {
                loggedIn = true;
                return authData;
            } else {
                loggedIn = false;
                return nullData;
            }
        } catch (InvalidResponseException e) {
            System.out.println(e.getMessage());
            return nullData;
        }
    }

    public String[] signup() throws IOException, ClassNotFoundException, InvalidResponseException {
        String[] authData = new String[3];
        String[] nullData = {"", ""};
        System.out.print("Введите ваше имя: ");
        authData[2] = scanner.readLine();
        System.out.print("Придумайте имя пользователя: ");
        authData[0] = scanner.readLine();
        System.out.print("Придумайте пароль: ");
        authData[1] = passwordLength();
        System.out.println();
        connection.send(new RequestBuilder().buildRequest("sign_up " + authData[2] + " " + authData[0] + " " + authData[1], authData, null, authData[0], authData[1]));
        if(Objects.equals(new ResponseHandler().getText((Response) connection.receive()), authData[0])) {
            loggedIn = true;
            return authData;
        } else {
            loggedIn = false;
            return nullData;
        }
    }

    public String passwordLength() throws IOException {
        String passwd = scanner.readLine();
        if(passwd.length() < 6){
            System.out.print("Пароль слишком короткий, повторите ввод: ");
            return passwordLength();
        } else {
            return passwd;
        }
    }
}

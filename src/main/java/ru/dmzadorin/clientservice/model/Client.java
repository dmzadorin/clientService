package ru.dmzadorin.clientservice.model;

/**
 * Created by Dmitry Zadorin on 02.03.2018.
 */
public class Client {
    private final String login;
    private final String passwordHash;
    private final double balance;

    public Client(String login, String passwordHash, double balance) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.balance = balance;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public double getBalance() {
        return balance;
    }
}

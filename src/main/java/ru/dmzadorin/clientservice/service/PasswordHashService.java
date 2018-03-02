package ru.dmzadorin.clientservice.service;

/**
 * Created by Dmitry Zadorin on 02.03.2018.
 */
public interface PasswordHashService {
    String hashPassword(String password);
}

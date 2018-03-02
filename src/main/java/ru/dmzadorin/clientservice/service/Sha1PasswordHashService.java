package ru.dmzadorin.clientservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.clientservice.model.exceptions.InternalApplicationException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Dmitry Zadorin on 02.03.2018.
 */
public class Sha1PasswordHashService implements PasswordHashService {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public String hashPassword(String password) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(password.getBytes());
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to hash password, SHA-1 not found", e);
            throw new InternalApplicationException("Failed to hash password, SHA-1 not found", e);
        }
        return sb.toString();
    }
}

package ru.dmzadorin.clientservice.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 28.02.2018
 */
@FunctionalInterface
public interface ConnectionProcessor<R> {
    /**
     * Executes some action with connection, for example add new values to table
     * Typically those actions are done in transactions
     *
     * @param connection
     * @return value, that might be returned after executing actions with connection
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    R doInConnection(Connection connection) throws SQLException;
}

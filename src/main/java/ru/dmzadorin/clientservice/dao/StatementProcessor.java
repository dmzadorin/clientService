package ru.dmzadorin.clientservice.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
@FunctionalInterface
public interface StatementProcessor<R> {
   /**
    * Executes some action with statement, for example get some data
    *
    * @param statement
    * @return value, that might be returned after executing actions with statement
    * @throws SQLException if a database access error occurs or this method is called on a closed connection
    */
   R doInPreparedStatement(PreparedStatement statement) throws SQLException;
}

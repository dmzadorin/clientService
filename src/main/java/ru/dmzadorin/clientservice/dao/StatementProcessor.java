package ru.dmzadorin.clientservice.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
@FunctionalInterface
public interface StatementProcessor<R> {
   R doInPreparedStatement(PreparedStatement statement) throws SQLException;
}

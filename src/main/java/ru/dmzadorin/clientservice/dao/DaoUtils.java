package ru.dmzadorin.clientservice.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.clientservice.model.exceptions.ApplicationException;
import ru.dmzadorin.clientservice.model.exceptions.InternalApplicationException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
public class DaoUtils {
    private static final Logger logger = LogManager.getLogger();

    public static <T> T executeQueryWithTransaction(DataSource datasource, ConnectionProcessor<T> connectionProcessor)
            throws InternalApplicationException {
        try (Connection conn = datasource.getConnection()) {
            conn.setAutoCommit(false);
            T result;

            try {
                result = connectionProcessor.doInConnection(conn);
                conn.commit();
            } catch (SQLException e) {
                logger.error("Failed to execute sql action due to error", e);
                conn.rollback();
                throw new InternalApplicationException("Internal error occurred", e);
            } catch (ApplicationException e) {
                logger.error("Failed to execute sql action due to {}", e.getMessage());
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Failed to execute sql action due to error", e);
            throw new InternalApplicationException("Internal error occurred", e);
        }
    }

    public static <T> T executeQuery(DataSource datasource, String query, StatementProcessor<T> statementProcessor)
            throws InternalApplicationException {
        try (Connection conn = datasource.getConnection()) {
            T result;
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                result = statementProcessor.doInPreparedStatement(statement);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Failed to execute sql action due to error", e);
            throw new InternalApplicationException("Internal error occurred", e);
        }
    }
}

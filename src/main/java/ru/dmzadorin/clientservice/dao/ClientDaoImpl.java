package ru.dmzadorin.clientservice.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.clientservice.model.Client;
import ru.dmzadorin.clientservice.model.exceptions.ClientAlreadyExistException;
import ru.dmzadorin.clientservice.model.exceptions.ClientNotExistException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
public class ClientDaoImpl implements ClientDao {
    private static final Logger logger = LogManager.getLogger();
    private static final String REGISTER_CLIENT = "insert into clients (login, password, balance) values (?, ?, 0)";
    private static final String GET_CLIENT = "select * from clients where login = ?";
    private static final String CHECK_IF_CLIENT_EXIST = "select 1 from clients where login = ?";
    private final DataSource dataSource;

    public ClientDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void registerClient(String login, String passwordHash) {
        DaoUtils.executeQueryWithTransaction(dataSource, connection -> {
            try (PreparedStatement selectStmt = connection.prepareStatement(CHECK_IF_CLIENT_EXIST)) {
                selectStmt.setString(1, login);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        logger.warn("Client with login {} exists", login);
                        throw new ClientAlreadyExistException(login);
                    }
                }
                try (PreparedStatement insertStmt = connection.prepareStatement(REGISTER_CLIENT)) {
                    insertStmt.setString(1, login);
                    insertStmt.setString(2, passwordHash);
                    insertStmt.executeUpdate();
                }
            }
            return null;
        });
    }

    @Override
    public Client getClient(String login) {
        return DaoUtils.executeQuery(dataSource, GET_CLIENT, statement -> {
            statement.setString(1, login);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getDouble("balance")
                    );
                } else {
                    throw new ClientNotExistException(login);
                }
            }
        });
    }
}

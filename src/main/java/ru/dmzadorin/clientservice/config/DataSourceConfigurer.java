package ru.dmzadorin.clientservice.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceConfigurer {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Utility function to initiate connection to H2 database and return a data source
     *
     * @param url            url of database
     * @param user           user name to be used by data source
     * @param pass           password to be used by data source
     * @param initScriptPath path to init script - if needed to setup database with some schema first
     * @return H2 datasource using supplied params
     */
    public static DataSource initDataSource(String url, String user, String pass, String initScriptPath) {
        try {
            if (initScriptPath != null) {
                logger.info("Running init script: " + initScriptPath);
                RunScript.execute(url, user, pass, initScriptPath, StandardCharsets.UTF_8, false);
            }
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(url);
            ds.setUser(user);
            ds.setPassword(pass);
            try (Connection connection = ds.getConnection()) {
                logger.info("Connection to {} successfully acquired", connection);
            }
            return ds;
        } catch (SQLException e) {
            logger.info("Failed to instantiate connection to {}", url);
            throw new IllegalStateException(e);
        }
    }
}

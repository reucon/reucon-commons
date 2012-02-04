package com.reucon.commons.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

public class OperationalEnvironmentBuilder
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public OperationalEnvironment buildWithSystemProperties(DataSource dataSource)
    {
        return build(System.getProperties(), dataSource);
    }

    public OperationalEnvironment build(Properties systemProperties, DataSource dataSource)
    {
        final OperationalEnvironmentImpl env = new OperationalEnvironmentImpl();

        applySystemProperties(env, systemProperties);
        applyDataSource(env, dataSource);

        return env;
    }

    void applySystemProperties(OperationalEnvironmentImpl env, Properties properties)
    {
        env.setOsName(properties.getProperty("os.name"));
        env.setOsVersion(properties.getProperty("os.version"));
        env.setJvmVendor(properties.getProperty("java.vendor"));
        env.setJvmVersion(properties.getProperty("java.version"));
        env.setContainerInfo(System.getProperty("container.info"));
    }

    void applyDataSource(OperationalEnvironmentImpl env, DataSource dataSource)
    {
        if (dataSource == null)
        {
            return;
        }

        try
                (Connection conn = dataSource.getConnection())
        {
            final DatabaseMetaData meta = conn.getMetaData();
            env.setDatabaseProductName(meta.getDatabaseProductName());
            env.setDatabaseMajorVersion(meta.getDatabaseMajorVersion());
            env.setDatabaseMinorVersion(meta.getDatabaseMinorVersion());
            env.setJdbcDriverMajorVersion(meta.getDriverMajorVersion());
            env.setJdbcDriverMinorVersion(meta.getDriverMinorVersion());
        }
        catch (SQLException e)
        {
            logger.warn("Unable to read database meta data", e);
        }
    }
}

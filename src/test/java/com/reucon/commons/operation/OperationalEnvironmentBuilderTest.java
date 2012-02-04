package com.reucon.commons.operation;

import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;

public class OperationalEnvironmentBuilderTest
{
    private Properties systemProperties;
    private DataSource dataSource;
    private Connection connection;
    private DatabaseMetaData meta;
    private OperationalEnvironmentBuilder builder;
    
    @Before
    public void setUp() throws Exception
    {
        systemProperties = new Properties();
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        meta = mock(DatabaseMetaData.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(meta);

        builder = new OperationalEnvironmentBuilder();
    }

    @Test
    public void testBuild() throws Exception
    {
        systemProperties.put("java.vendor", "Oracle Corporation");
        systemProperties.put("java.version", "1.7.0_02");
        systemProperties.put("os.name", "Linux");
        systemProperties.put("os.version", "3.0.0-16-generic");

        when(meta.getDatabaseProductName()).thenReturn("MySQL");
        when(meta.getDatabaseMajorVersion()).thenReturn(1);
        when(meta.getDatabaseMinorVersion()).thenReturn(2);
        when(meta.getDriverMajorVersion()).thenReturn(3);
        when(meta.getDriverMinorVersion()).thenReturn(4);

        final OperationalEnvironment env = builder.build(systemProperties, dataSource);
        
        assertThat(env.getJvmVendor(), is("Oracle Corporation"));
        assertThat(env.getJvmVersion(), is("1.7.0_02"));
        assertThat(env.getOsName(), is("Linux"));
        assertThat(env.getOsVersion(), is("3.0.0-16-generic"));

        assertThat(env.getDatabaseProductName(), is("MySQL"));
        assertThat(env.getDatabaseMajorVersion(), is(1));
        assertThat(env.getDatabaseMinorVersion(), is(2));
        assertThat(env.getJdbcDriverMajorVersion(), is(3));
        assertThat(env.getJdbcDriverMinorVersion(), is(4));

        verify(connection).close();
    }

    @Test
    public void testBuildWithoutDataSource() throws Exception
    {
        systemProperties.put("java.vendor", "Oracle Corporation");
        systemProperties.put("java.version", "1.7.0_02");
        systemProperties.put("os.name", "Linux");
        systemProperties.put("os.version", "3.0.0-16-generic");

        final OperationalEnvironment env = builder.build(systemProperties, null);

        assertThat(env.getJvmVendor(), is("Oracle Corporation"));
        assertThat(env.getJvmVersion(), is("1.7.0_02"));
        assertThat(env.getOsName(), is("Linux"));
        assertThat(env.getOsVersion(), is("3.0.0-16-generic"));
        
        assertThat(env.getDatabaseProductName(), is(nullValue()));
        assertThat(env.getDatabaseMajorVersion(), is(nullValue()));
        assertThat(env.getDatabaseMinorVersion(), is(nullValue()));
        assertThat(env.getJdbcDriverMajorVersion(), is(nullValue()));
        assertThat(env.getJdbcDriverMinorVersion(), is(nullValue()));
    }

    @Test
    public void testBuildWithSqlException() throws Exception
    {
        systemProperties.put("java.vendor", "Oracle Corporation");
        when(meta.getDatabaseProductName()).thenThrow(new SQLException("Error when retrieving database product name"));

        final OperationalEnvironment env = builder.build(systemProperties, dataSource);

        assertThat(env.getJvmVendor(), is("Oracle Corporation"));

        assertThat(env.getDatabaseProductName(), is(nullValue()));
        assertThat(env.getDatabaseMajorVersion(), is(nullValue()));
        assertThat(env.getDatabaseMinorVersion(), is(nullValue()));
        assertThat(env.getJdbcDriverMajorVersion(), is(nullValue()));
        assertThat(env.getJdbcDriverMinorVersion(), is(nullValue()));

        verify(connection).close();
    }
}

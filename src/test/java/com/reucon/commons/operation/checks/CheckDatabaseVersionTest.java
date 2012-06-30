package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;
import com.reucon.commons.operation.OperationalEnvironmentImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CheckDatabaseVersionTest
{
    private CheckDatabaseVersion check;
    private OperationalEnvironmentImpl environment;

    @Before
    public void setUp() throws Exception
    {
        this.check = new CheckDatabaseVersion("5.5");
        this.environment = new OperationalEnvironmentImpl();
    }

    @Test
    public void testRunPass() throws Exception
    {
        environment.setDatabaseMajorVersion(5);
        environment.setDatabaseMinorVersion(6);
        final EnvironmentCheckResult result = check.run(environment);
        assertTrue(result.isPassed());
    }

    @Test
    public void testRunPassExactly() throws Exception
    {
        environment.setDatabaseMajorVersion(5);
        environment.setDatabaseMinorVersion(5);
        final EnvironmentCheckResult result = check.run(environment);
        assertTrue(result.isPassed());
    }

    @Test
    public void testRunFail() throws Exception
    {
        environment.setDatabaseMajorVersion(5);
        environment.setDatabaseMinorVersion(0);
        final EnvironmentCheckResult result = check.run(environment);
        assertTrue(result.isFailed());
    }

    @Test
    public void testRunFailNull() throws Exception
    {
        final EnvironmentCheckResult result = check.run(environment);
        assertTrue(result.isFailed());
    }
}

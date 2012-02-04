package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckJvmVersionTest
{
    private OperationalEnvironment env;

    @Before
    public void setUp() throws Exception
    {
        env = mock(OperationalEnvironment.class);
        when(env.getJvmVersion()).thenReturn("1.7.0_02");
    }

    @Test
    public void testPassed() throws Exception
    {
        final CheckJvmVersion check = new CheckJvmVersion("1.7.0");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isPassed());
    }

    @Test
    public void testPassedWithMicro() throws Exception
    {
        final CheckJvmVersion check = new CheckJvmVersion("1.7.0_02");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isPassed());
    }

    @Test
    public void testPassedWithEarlyAccess() throws Exception
    {
        env = mock(OperationalEnvironment.class);
        when(env.getJvmVersion()).thenReturn("1.7.0_04-ea");

        final CheckJvmVersion check = new CheckJvmVersion("1.7.0");
        check.setAllowEarlyAccess(true);
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isPassed());
    }

    @Test
    public void testFailed() throws Exception
    {
        final CheckJvmVersion check = new CheckJvmVersion("1.8.0");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
    }

    @Test
    public void testFailedWithMicro() throws Exception
    {
        final CheckJvmVersion check = new CheckJvmVersion("1.7.0_03");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
    }

    @Test
    public void testFailedWithEarlyAccess() throws Exception
    {
        env = mock(OperationalEnvironment.class);
        when(env.getJvmVersion()).thenReturn("1.7.0_04-ea");

        final CheckJvmVersion check = new CheckJvmVersion("1.7.0");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
        assertEquals("Detected JVM version 1.7.0_04-ea is an early access version", result.getMessage());
    }
}

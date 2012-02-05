package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckContainerVesionTest
{
    private OperationalEnvironment env;

    @Before
    public void setUp() throws Exception
    {
        env = mock(OperationalEnvironment.class);
        when(env.getContainerInfo()).thenReturn("jetty/7.2.3.v20101205");
    }

    @Test
    public void testPassed() throws Exception
    {
        final CheckContainerVersion check = new CheckContainerVersion("7.0.0");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isPassed());
    }

    @Test
    public void testFailed() throws Exception
    {
        final CheckContainerVersion check = new CheckContainerVersion("8.0.0");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
    }

    @Test
    public void testNull() throws Exception
    {
        when(env.getContainerInfo()).thenReturn(null);
        final CheckContainerVersion check = new CheckContainerVersion("8.0.0");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
    }

    @Test
    public void testNoSlash() throws Exception
    {
        when(env.getContainerInfo()).thenReturn("something");
        final CheckContainerVersion check = new CheckContainerVersion("8.0.0");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
        assertEquals("Detected container version null is not at least required version 8.0.0", result.getMessage());
    }
}

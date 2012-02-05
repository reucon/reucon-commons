package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckContainerVendorTest
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
        final CheckContainerVendor check = new CheckContainerVendor("jetty");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isPassed());
    }

    @Test
    public void testFailed() throws Exception
    {
        final CheckContainerVendor check = new CheckContainerVendor("Apache Tomcat");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
    }

    @Test
    public void testNull() throws Exception
    {
        when(env.getContainerInfo()).thenReturn(null);
        final CheckContainerVendor check = new CheckContainerVendor("Apache Tomcat");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
        assertEquals("Detected container vendor null does not match required pattern Apache Tomcat", result.getMessage());
    }
}

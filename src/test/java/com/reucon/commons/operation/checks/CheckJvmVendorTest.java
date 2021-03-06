package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckJvmVendorTest
{
    private OperationalEnvironment env;

    @Before
    public void setUp() throws Exception
    {
        env = mock(OperationalEnvironment.class);
        when(env.getJvmVendor()).thenReturn("Oracle Corporation");
    }

    @Test
    public void testPassed() throws Exception
    {
        final CheckJvmVendor check = new CheckJvmVendor("(Oracle Corporation|Sun Microsystems Inc\\.)");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isPassed());
    }

    @Test
    public void testFailed() throws Exception
    {
        final CheckJvmVendor check = new CheckJvmVendor("Oracle Inc\\.");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
    }

    @Test
    public void testNull() throws Exception
    {
        when(env.getJvmVendor()).thenReturn(null);

        final CheckJvmVendor check = new CheckJvmVendor("Oracle Inc\\.");
        final EnvironmentCheckResult result = check.run(env);

        assertTrue(result.isFailed());
    }
}

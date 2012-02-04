package com.reucon.commons.operation;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentCheckerTest
{
    class SuccessCheck implements EnvironmentCheck
    {
        @Override
        public EnvironmentCheckResult run(OperationalEnvironment environment)
        {
            return EnvironmentCheckResult.passed(getClass());
        }
    }

    class FailCheck implements EnvironmentCheck
    {
        @Override
        public EnvironmentCheckResult run(OperationalEnvironment environment)
        {
            return EnvironmentCheckResult.failed(getClass());
        }
    }

    private EnvironmentChecker checker;
    
    @Before
    public void setUp() throws Exception
    {
        checker = new EnvironmentChecker();
    }

    @Test
    public void testCheckPassed() throws Exception
    {
        final List<EnvironmentCheck> checks = new ArrayList<>();
        checks.add(new SuccessCheck());
        checker.setChecks(checks);

        checker.check();
    }

    @Test(expected = IllegalStateException.class)
    public void testCheckFailed() throws Exception
    {
        final List<EnvironmentCheck> checks = new ArrayList<>();
        checks.add(new SuccessCheck());
        checks.add(new FailCheck());
        checker.setChecks(checks);

        checker.check();
    }

    @Test
    public void testCheckFailedWithoutException() throws Exception
    {
        final List<EnvironmentCheck> checks = new ArrayList<>();
        checks.add(new SuccessCheck());
        checks.add(new FailCheck());
        checker.setChecks(checks);
        checker.setThrowExceptionOnFailure(false);

        checker.check();
    }
}

package com.reucon.commons.operation;

/**
 * Individual environment check.
 */
public interface EnvironmentCheck
{
    /**
     * Runs this check.
     *
     * @param environment the environment to check.
     * @return the result.
     */
    EnvironmentCheckResult run(OperationalEnvironment environment);
}
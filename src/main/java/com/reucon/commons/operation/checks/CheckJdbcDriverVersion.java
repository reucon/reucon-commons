package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;
import com.reucon.commons.operation.VersionNumber;

/**
 * Checks that the version of the JDBC driver.
 */
public class CheckJdbcDriverVersion extends AbstractEnvironmentCheck
{
    private final VersionNumber requiredVersion;

    public CheckJdbcDriverVersion(String requiredVersion)
    {
        this.requiredVersion = new VersionNumber(requiredVersion);
    }

    @Override
    public EnvironmentCheckResult run(OperationalEnvironment environment)
    {
        return checkVersionIsAtLeast(requiredVersion, determineVersion(environment));
    }

    protected VersionNumber determineVersion(OperationalEnvironment environment)
    {
        return new VersionNumber(environment.getJdbcDriverMajorVersion(), environment.getJdbcDriverMinorVersion());
    }
}

package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;
import com.reucon.commons.operation.VersionNumber;

/**
 *
 */
public class CheckJvmVersion extends AbstractEnvironmentCheck
{
    private static final String EARLY_ACCESS_QUALIFIER = "ea";

    private final VersionNumber requiredVersion;
    private boolean allowEarlyAccess = false;

    public CheckJvmVersion(String requiredVersion)
    {
        this.requiredVersion = VersionNumber.fromJvmVersion(requiredVersion);
    }

    /**
     * Sets whether to allow early access version of JDK.<br/>
     * Default is <code>false</code>.
     *
     * @param allowEarlyAccess <code>true</code> to allow early access version, <code>false</code> to disallow them.
     */
    public void setAllowEarlyAccess(boolean allowEarlyAccess)
    {
        this.allowEarlyAccess = allowEarlyAccess;
    }

    @Override
    public EnvironmentCheckResult run(OperationalEnvironment environment)
    {
        final VersionNumber actualJvmVersion = determineVersion(environment);
        final EnvironmentCheckResult result = checkVersionIsAtLeast(requiredVersion, actualJvmVersion);

        if (result.isPassed())
        {
            if (!allowEarlyAccess && EARLY_ACCESS_QUALIFIER.equalsIgnoreCase(actualJvmVersion.getQualifier()))
            {
                return failedWithMessage("failedDueToEarlyAccess", environment.getJvmVersion());
            }
        }

        return result;
    }

    protected VersionNumber determineVersion(OperationalEnvironment environment)
    {
        return VersionNumber.fromJvmVersion(environment.getJvmVersion());
    }
}

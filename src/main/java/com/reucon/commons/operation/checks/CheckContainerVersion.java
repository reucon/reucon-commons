package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;
import com.reucon.commons.operation.VersionNumber;

/**
 *
 */
public class CheckContainerVersion extends AbstractEnvironmentCheck
{
    private final VersionNumber requiredVersion;

    public CheckContainerVersion(String requiredVersion)
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
        final String info = environment.getContainerInfo();
        final int slashPosition = info.indexOf("/");
        if(slashPosition < 0)
        {
            return null;
        }
        return new VersionNumber(info.substring(slashPosition + 1));
    }
}

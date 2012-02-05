package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheck;
import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.VersionNumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class for environment checks.
 */
public abstract class AbstractEnvironmentCheck implements EnvironmentCheck
{
    protected EnvironmentCheckResult passed(Object... params)
    {
        return EnvironmentCheckResult.passed(getClass(), params);
    }

    protected EnvironmentCheckResult failed(Object... params)
    {
        return EnvironmentCheckResult.failed(getClass(), params);
    }

    protected EnvironmentCheckResult failedWithMessage(String messageKey, Object... params)
    {
        return EnvironmentCheckResult.failedWithMessage(getClass(), messageKey, params);
    }

    protected EnvironmentCheckResult checkPatternMatches(Pattern pattern, String actual)
    {
        if (actual == null)
        {
            return failed(pattern.pattern(), actual);
        }

        final Matcher matcher = pattern.matcher(actual);

        if (matcher.matches())
        {
            return passed(pattern.pattern(), actual);
        }
        else
        {
            return failed(pattern.pattern(), actual);
        }
    }

    protected EnvironmentCheckResult checkVersionIsAtLeast(VersionNumber requiredVersion, VersionNumber actualVersion)
    {
        if (actualVersion != null && actualVersion.isAtLeast(requiredVersion))
        {
            return passed(requiredVersion, actualVersion);
        }
        else
        {
            return failed(requiredVersion, actualVersion);
        }
    }
}

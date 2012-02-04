package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;

import java.util.regex.Pattern;

/**
 * Checks that the Container vendor matches a regular expression.
 */
public class CheckContainerVendor extends AbstractEnvironmentCheck
{
    private Pattern requiredVendorPattern;

    public CheckContainerVendor(String requiredVendorRegex)
    {
        this.requiredVendorPattern = Pattern.compile(requiredVendorRegex);
    }

    @Override
    public EnvironmentCheckResult run(OperationalEnvironment environment)
    {
        return checkPatternMatches(requiredVendorPattern, determineVendor(environment));
    }

    protected String determineVendor(final OperationalEnvironment environment)
    {
        final String info = environment.getContainerInfo();
        final int slashPosition = info.indexOf("/");
        if(slashPosition < 0)
        {
            return "";
        }
        return info.substring(0, slashPosition);
    }
}

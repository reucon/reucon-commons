package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;

import java.util.regex.Pattern;

/**
 * Checks that the JVM vendor matches a regular expression.
 */
public class CheckJvmVendor extends AbstractEnvironmentCheck
{
    private Pattern requiredVendorPattern;

    public CheckJvmVendor(String requiredVendorRegex)
    {
        this.requiredVendorPattern = Pattern.compile(requiredVendorRegex);
    }

    @Override
    public EnvironmentCheckResult run(OperationalEnvironment environment)
    {
        return checkPatternMatches(requiredVendorPattern, environment.getJvmVendor());
    }
}

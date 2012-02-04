package com.reucon.commons.operation.checks;

import com.reucon.commons.operation.EnvironmentCheckResult;
import com.reucon.commons.operation.OperationalEnvironment;

import java.util.regex.Pattern;

/**
 * Checks that the database product name matches a regular expression.
 */
public class CheckDatabaseProductName extends AbstractEnvironmentCheck
{
    private Pattern requiredProductPattern;

    public CheckDatabaseProductName(String requiredProductRegex)
    {
        this.requiredProductPattern = Pattern.compile(requiredProductRegex);
    }

    @Override
    public EnvironmentCheckResult run(OperationalEnvironment environment)
    {
        return checkPatternMatches(requiredProductPattern, environment.getDatabaseProductName());
    }
}

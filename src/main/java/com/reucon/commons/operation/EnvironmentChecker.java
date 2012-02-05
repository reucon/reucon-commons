package com.reucon.commons.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Runs a list of {@link EnvironmentCheck EnvironmentChecks}.
 */
public class EnvironmentChecker
{
    private static final String ENVIRONMENT_SUPPORTED_PROPERTY = "environment.supported";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private OperationalEnvironmentBuilder builder = new OperationalEnvironmentBuilder();
    private List<EnvironmentCheck> checks;
    private boolean throwExceptionOnFailure = true;
    private DataSource dataSource;

    public EnvironmentChecker()
    {
        try
        {
            if("false".equalsIgnoreCase(System.getProperty(ENVIRONMENT_SUPPORTED_PROPERTY)))
            {
                throwExceptionOnFailure = false;
            }
        }
        catch (Exception e)
        {
            logger.debug(String.format("Could not read system property '%s'", ENVIRONMENT_SUPPORTED_PROPERTY), e);
        }
    }
    
    public void setChecks(List<EnvironmentCheck> checks)
    {
        this.checks = checks;
    }

    public void setThrowExceptionOnFailure(boolean throwExceptionOnFailure)
    {
        this.throwExceptionOnFailure = throwExceptionOnFailure;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public List<EnvironmentCheckResult> check()
    {
        final OperationalEnvironment env = builder.buildWithSystemProperties(dataSource);
        logger.info("Determined environment {}", env);
        final List<EnvironmentCheckResult> results = runChecks(env);
        final boolean passed = determineSuccess(results);
        logger.info("Environment check {}", passed ? "passed" : "failed");

        if (!passed && throwExceptionOnFailure)
        {
            throw new IllegalStateException("Detected Operational environment does not match expected production environment.");
        }
        return results;
    }

    List<EnvironmentCheckResult> runChecks(OperationalEnvironment environment)
    {
        final List<EnvironmentCheckResult> results = new ArrayList<>();

        if (checks == null)
        {
            return results;
        }

        for (EnvironmentCheck check : checks)
        {
            final EnvironmentCheckResult result = runCheck(environment, check);
            results.add(result);
            if (result.isPassed())
            {
                logger.debug(result.toString());
            }
            else
            {
                logger.warn(result.toString());
            }
        }
        return results;
    }

    private EnvironmentCheckResult runCheck(OperationalEnvironment environment, EnvironmentCheck check)
    {
        try
        {
            return check.run(environment);
        }
        catch (Exception e)
        {
            logger.warn(String.format("Unable to execute check %s", check.getClass()), e);
            return EnvironmentCheckResult.error(check.getClass(), e);
        }
    }

    /**
     * Determines whether all checks have passed.
     *
     * @param checkResults collection of results from running checks.
     * @return <code>true</code> if all checks have passed, <code>false</code> if at least one has failed.
     */
    boolean determineSuccess(Collection<EnvironmentCheckResult> checkResults)
    {
        for (EnvironmentCheckResult checkResult : checkResults)
        {
            if (checkResult.isFailed())
            {
                return false;
            }
        }
        return true;
    }
}

package com.reucon.commons.operation;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Result of an {@link EnvironmentCheck}.
 */
public class EnvironmentCheckResult implements Serializable
{
    private static final long serialVersionUID = 0L;
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com/reucon/commons/operation/environment-check-messages");

    private final boolean passed;
    private final String check;
    private final String messageKey;
    private final Object[] params;
    private final String message;

    /**
     * Returns a new EnvironmentCheckResult for a check that has passed.
     *
     * @param checkClass class of the check that has passed.
     * @param params     the parameters for the default passed message.
     * @return the created EnvironmentCheckResult object.
     */
    public static EnvironmentCheckResult passed(Class<? extends EnvironmentCheck> checkClass, Object... params)
    {
        final String key = checkClass.getSimpleName() + ".passed";
        return new EnvironmentCheckResult(true, checkClass.getSimpleName(), resolveMessage(key, params), key, params);
    }

    /**
     * Returns a new EnvironmentCheckResult for a check that has failed.
     *
     * @param checkClass class of the check that has failed.
     * @param params     the parameters for the default failed message.
     * @return the created EnvironmentCheckResult object.
     */
    public static EnvironmentCheckResult failed(Class<? extends EnvironmentCheck> checkClass, Object... params)
    {
        final String key = checkClass.getSimpleName() + ".failed";
        return new EnvironmentCheckResult(false, checkClass.getSimpleName(), resolveMessage(key, params), key, params);
    }

    /**
     * Returns a new EnvironmentCheckResult for a check that has failed.
     *
     * @param checkClass class of the check that has failed.
     * @param messageKey the message key for localization.
     * @param params     the parameters for the message.
     * @return the created EnvironmentCheckResult object.
     */
    public static EnvironmentCheckResult failedWithMessage(Class<? extends EnvironmentCheck> checkClass, String messageKey, Object... params)
    {
        final String key = checkClass.getSimpleName() + "." + messageKey;
        return new EnvironmentCheckResult(false, checkClass.getSimpleName(), resolveMessage(key, params), key, params);
    }

    public static EnvironmentCheckResult error(Class<? extends EnvironmentCheck> checkClass, Throwable t)
    {
        final String key = "EnvironmentCheck.error";
        final Object[] params = {t.toString()};
        return new EnvironmentCheckResult(false, checkClass.getSimpleName(), resolveMessage(key, params), key, params);
    }

    private static String resolveMessage(String key, Object[] params)
    {
        try
        {
            final String pattern = bundle.getString(key);
            return MessageFormat.format(pattern, params);
        }
        catch (MissingResourceException e)
        {
            return "unknown";
        }
    }

    private EnvironmentCheckResult(boolean passed, String check, String message, String messageKey, Object... params)
    {
        this.passed = passed;
        this.check = check;
        this.message = message;
        this.messageKey = messageKey;
        this.params = params;
    }

    /**
     * Returns <code>true</code> if the has passed.
     *
     * @return <code>true</code> if the has passed, <code>false</code> otherwise.
     */
    public boolean isPassed()
    {
        return passed;
    }

    /**
     * Returns <code>true</code> if the has failed.
     *
     * @return <code>true</code> if the has failed, <code>false</code> otherwise.
     */
    public boolean isFailed()
    {
        return !passed;
    }

    /**
     * Returns the class name of the check that has passed or failed.
     *
     * @return the class name of the check that has passed or failed.
     */
    public String getCheck()
    {
        return check;
    }

    /**
     * Returns the message key for localization.
     *
     * @return the message key for localization.
     */
    public String getMessageKey()
    {
        return messageKey;
    }

    /**
     * Returns the parameters for the message.
     *
     * @return the parameters for the message.
     */
    public Object[] getParams()
    {
        return params;
    }

    /**
     * Returns the default english message.
     *
     * @return the default english message.
     */
    public String getMessage()
    {
        return message;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append(check).append(" ");
        sb.append(passed ? "passed" : "failed");
        sb.append(": ").append(message);
        sb.append(" (").append(messageKey).append(')');
        return sb.toString();
    }
}

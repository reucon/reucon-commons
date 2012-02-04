package com.reucon.commons.operation;

import com.reucon.commons.operation.checks.CheckJvmVersion;
import org.junit.Test;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;

public class EnvironmentCheckResultTest
{
    @Test
    public void testPassed() throws Exception
    {
        final EnvironmentCheckResult result = EnvironmentCheckResult.passed(CheckJvmVersion.class, "1", "2");
        assertEquals("CheckJvmVersion", result.getCheck());
        assertEquals("Detected JVM version 2 is at least required version 1", result.getMessage());
        assertEquals("CheckJvmVersion.passed", result.getMessageKey());
        assertEquals("1", result.getParams()[0]);
        assertEquals("2", result.getParams()[1]);
        assertEquals("CheckJvmVersion passed: Detected JVM version 2 is at least required version 1 (CheckJvmVersion.passed)", result.toString());
        assertTrue(result.isPassed());
        assertFalse(result.isFailed());
    }

    @Test
    public void testFailed() throws Exception
    {
        final EnvironmentCheckResult result = EnvironmentCheckResult.failed(CheckJvmVersion.class, "2", "1");
        assertEquals("CheckJvmVersion", result.getCheck());
        assertEquals("Detected JVM version 1 is not at least required version 2", result.getMessage());
        assertEquals("CheckJvmVersion.failed", result.getMessageKey());
        assertEquals("2", result.getParams()[0]);
        assertEquals("1", result.getParams()[1]);
        assertEquals("CheckJvmVersion failed: Detected JVM version 1 is not at least required version 2 (CheckJvmVersion.failed)", result.toString());
        assertFalse(result.isPassed());
        assertTrue(result.isFailed());
    }

    @Test
    public void testFailedWithMissingResourceException() throws Exception
    {
        final EnvironmentCheckResult result = EnvironmentCheckResult.failedWithMessage(CheckJvmVersion.class, "nonExistingKey");
        assertEquals("unknown", result.getMessage());
    }
}

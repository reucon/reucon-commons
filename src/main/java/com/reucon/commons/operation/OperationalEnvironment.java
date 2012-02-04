package com.reucon.commons.operation;

/**
 * Operational environment that hosts the application.
 */
public interface OperationalEnvironment
{
    String getOsName();
    
    String getOsVersion();
    
    String getJvmVersion();
    
    String getJvmVendor();
    
    String getContainerInfo();

    String getDatabaseProductName();

    Integer getDatabaseMajorVersion();

    Integer getDatabaseMinorVersion();

    Integer getJdbcDriverMajorVersion();

    Integer getJdbcDriverMinorVersion();
}

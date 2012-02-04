package com.reucon.commons.operation;

import java.io.Serializable;

/**
 * Default implementation of {@link OperationalEnvironment}.
 */
public class OperationalEnvironmentImpl implements OperationalEnvironment, Serializable
{
    private static final long serialVersionUID = 1L;

    private String osName;
    private String osVersion;
    private String jvmVendor;
    private String jvmVersion;
    private String containerInfo;
    private String databaseProductName;
    private Integer databaseMajorVersion;
    private Integer databaseMinorVersion;
    private Integer jdbcDriverMajorVersion;
    private Integer jdbcDriverMinorVersion;

    @Override
    public String getOsName()
    {
        return osName;
    }

    public void setOsName(String osName)
    {
        this.osName = osName;
    }

    @Override
    public String getOsVersion()
    {
        return osVersion;
    }

    public void setOsVersion(String osVersion)
    {
        this.osVersion = osVersion;
    }

    @Override
    public String getJvmVendor()
    {
        return jvmVendor;
    }

    public void setJvmVendor(String jvmVendor)
    {
        this.jvmVendor = jvmVendor;
    }

    @Override
    public String getJvmVersion()
    {
        return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion)
    {
        this.jvmVersion = jvmVersion;
    }

    @Override
    public String getContainerInfo()
    {
        return containerInfo;
    }

    public void setContainerInfo(String containerInfo)
    {
        this.containerInfo = containerInfo;
    }

    @Override
    public String getDatabaseProductName()
    {
        return databaseProductName;
    }

    public void setDatabaseProductName(String databaseProductName)
    {
        this.databaseProductName = databaseProductName;
    }

    @Override
    public Integer getDatabaseMajorVersion()
    {
        return databaseMajorVersion;
    }

    public void setDatabaseMajorVersion(Integer databaseMajorVersion)
    {
        this.databaseMajorVersion = databaseMajorVersion;
    }

    @Override
    public Integer getDatabaseMinorVersion()
    {
        return databaseMinorVersion;
    }

    public void setDatabaseMinorVersion(Integer databaseMinorVersion)
    {
        this.databaseMinorVersion = databaseMinorVersion;
    }

    @Override
    public Integer getJdbcDriverMajorVersion()
    {
        return jdbcDriverMajorVersion;
    }

    public void setJdbcDriverMajorVersion(Integer jdbcDriverMajorVersion)
    {
        this.jdbcDriverMajorVersion = jdbcDriverMajorVersion;
    }

    @Override
    public Integer getJdbcDriverMinorVersion()
    {
        return jdbcDriverMinorVersion;
    }

    public void setJdbcDriverMinorVersion(Integer jdbcDriverMinorVersion)
    {
        this.jdbcDriverMinorVersion = jdbcDriverMinorVersion;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("OperationalEnvironmentImpl");
        sb.append("{osName='").append(osName).append('\'');
        sb.append(", osVersion='").append(osVersion).append('\'');
        sb.append(", jvmVendor='").append(jvmVendor).append('\'');
        sb.append(", jvmVersion='").append(jvmVersion).append('\'');
        sb.append(", containerInfo='").append(containerInfo).append('\'');
        sb.append(", databaseProductName='").append(databaseProductName).append('\'');
        sb.append(", databaseMajorVersion=").append(databaseMajorVersion);
        sb.append(", databaseMinorVersion=").append(databaseMinorVersion);
        sb.append(", jdbcDriverMajorVersion=").append(jdbcDriverMajorVersion);
        sb.append(", jdbcDriverMinorVersion=").append(jdbcDriverMinorVersion);
        sb.append('}');
        return sb.toString();
    }
}

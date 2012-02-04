package com.reucon.commons.operation;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides version number comparison.
 */
public class VersionNumber implements Comparable<VersionNumber>, Serializable
{
    private static final long serialVersionUID = 0L;

    private static final Pattern JVM_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(_(\\d+))?(-(.*))?");

    private int major;
    private int minor;
    private int micro;
    private Integer patchLevel;
    private String qualifier;

    public VersionNumber(final int major, final int minor)
    {
        this.major = major;
        this.minor = minor;
    }

    public VersionNumber(final int major, final int minor, final int micro)
    {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
    }

    public VersionNumber(final int major, final int minor, final int micro, final String qualifier)
    {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.qualifier = qualifier;
    }

    public VersionNumber(final int major, final int minor, final int micro, final int patchLevel, final String qualifier)
    {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.patchLevel = patchLevel;
        this.qualifier = qualifier;
    }

    /**
     * Creates a version number from the Sun JVM identifier.
     *
     * @param jvmString the JVM version string.
     * @return the corresponding VersionNumber.
     */
    public static VersionNumber fromJvmVersion(final String jvmString)
    {
        final Matcher matcher = JVM_PATTERN.matcher(jvmString);

        if (matcher.matches())
        {
            final int major = Integer.valueOf(matcher.group(1));
            final int minor = Integer.valueOf(matcher.group(2));
            final int micro = Integer.valueOf(matcher.group(3));
            final String patchLevelString = matcher.group(5);
            final int patchLevel = patchLevelString == null ? 0 : Integer.valueOf(patchLevelString);
            final String qualifier = matcher.group(7);

            return new VersionNumber(major, minor, micro, patchLevel, qualifier);
        }
        else
        {
            return new VersionNumber(0, 0, 0, 0, null);
        }
    }

    public VersionNumber(final String versionString) throws IllegalArgumentException
    {
        final String[] parts = versionString.split("\\.", 4);
        major = minor = micro = 0;
        try
        {
            if (parts.length > 0 && parts[0].length() > 0)
            {
                major = Integer.valueOf(parts[0]);
            }
            if (parts.length > 1)
            {
                minor = Integer.valueOf(parts[1]);
            }
            if (parts.length > 2)
            {
                micro = Integer.valueOf(parts[2]);
            }
            if (parts.length > 3)
            {
                qualifier = parts[3];
            }
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public boolean isAtLeast(final VersionNumber other)
    {
        return other != null && compareTo(other) >= 0;
    }

    public int getMajor()
    {
        return major;
    }

    public int getMicro()
    {
        return micro;
    }

    public int getMinor()
    {
        return minor;
    }

    public Integer getPatchLevel()
    {
        return patchLevel;
    }

    public String getQualifier()
    {
        return qualifier;
    }


    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }

        final VersionNumber other = (VersionNumber) obj;
        if (this.major != other.major)
        {
            return false;
        }
        if (this.minor != other.minor)
        {
            return false;
        }
        if (this.micro != other.micro)
        {
            return false;
        }
        if (this.patchLevel != other.patchLevel && (this.patchLevel == null || !this.patchLevel.equals(other.patchLevel)))
        {
            return false;
        }
        if ((this.qualifier == null) ? (other.qualifier != null) : !this.qualifier.equals(other.qualifier))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 79 * hash + this.major;
        hash = 79 * hash + this.minor;
        hash = 79 * hash + this.micro;
        hash = 79 * hash + (this.patchLevel != null ? this.patchLevel.hashCode() : 0);
        hash = 79 * hash + (this.qualifier != null ? this.qualifier.hashCode() : 0);
        return hash;
    }


    @Override
    public int compareTo(final VersionNumber other)
    {
        if (major < other.major)
        {
            return -1;
        }
        if (major > other.major)
        {
            return 1;
        }
        if (minor < other.minor)
        {
            return -1;
        }
        if (minor > other.minor)
        {
            return 1;
        }
        if (micro < other.micro)
        {
            return -1;
        }
        if (micro > other.micro)
        {
            return 1;
        }
        if (patchLevel == null)
        {
            if (other.patchLevel == null)
            {
                if (this.qualifier == null && other.qualifier == null)
                {
                    return 0;
                }
                if (this.qualifier != null && other.qualifier == null)
                {
                    return 1;
                }
                if (this.qualifier == null && other.qualifier != null)
                {
                    return -1;
                }
                if (this.qualifier != null && other.qualifier != null)
                {
                    return this.qualifier.compareTo(other.qualifier);
                }
            }
            return -1;
        }
        if (other.patchLevel == null)
        {
            return 1;
        }
        if (this.patchLevel > other.patchLevel)
        {
            return 1;
        }
        if (this.patchLevel < other.patchLevel)
        {
            return -1;
        }
        if (qualifier == null)
        {
            return other.qualifier == null ? 0 : -1;
        }
        if (other.qualifier == null)
        {
            return 1;
        }
        return qualifier.compareTo(other.qualifier);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append(major);
        sb.append(".").append(minor);
        sb.append(".").append(micro);
        if (patchLevel != null)
        {
            sb.append(".").append(patchLevel);
        }
        if (qualifier != null)
        {
            sb.append(".").append(qualifier);
        }
        return sb.toString();
    }
}

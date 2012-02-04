package com.reucon.commons.operation;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class VersionNumberTest
{
    @Test
    public void testFromJvmVersion()
    {
        final VersionNumber versionNumber = VersionNumber.fromJvmVersion("1.6.0_24");

        assertThat(versionNumber.getMajor(), is(1));
        assertThat(versionNumber.getMinor(), is(6));
        assertThat(versionNumber.getMicro(), is(0));
        assertThat(versionNumber.getPatchLevel(), is(24));
        assertThat(versionNumber.getQualifier(), is(nullValue()));
    }
    
    @Test
    public void fromOracleJvmVersion()
    {
        final VersionNumber versionNumber = VersionNumber.fromJvmVersion("1.7.0");

        assertThat(versionNumber.getMajor(), is(1));
        assertThat(versionNumber.getMinor(), is(7));
        assertThat(versionNumber.getMicro(), is(0));
        assertThat(versionNumber.getPatchLevel(), is(0));
        assertThat(versionNumber.getQualifier(), is(nullValue()));
    }

    @Test
    public void fromGarbageJvmVersion()
    {
        final VersionNumber versionNumber = VersionNumber.fromJvmVersion("Hilde.Hirsch");

        assertThat(versionNumber.getMajor(), is(0));
        assertThat(versionNumber.getMinor(), is(0));
        assertThat(versionNumber.getMicro(), is(0));
        assertThat(versionNumber.getPatchLevel(), is(0));
        assertThat(versionNumber.getQualifier(), is(nullValue()));
    }

    @Test
    public void fromOracleJvmVersionWithQualifier()
    {
        final VersionNumber versionNumber = VersionNumber.fromJvmVersion("1.7.0_02-ea");

        assertThat(versionNumber.getMajor(), is(1));
        assertThat(versionNumber.getMinor(), is(7));
        assertThat(versionNumber.getMicro(), is(0));
        assertThat(versionNumber.getPatchLevel(), is(2));
        assertThat(versionNumber.getQualifier(), is("ea"));
    }

    @Test
    public void compareOracleJvmVersion()
    {
        
        final VersionNumber lower = VersionNumber.fromJvmVersion("1.6.0_1");
        final VersionNumber medium = VersionNumber.fromJvmVersion("1.7.0");
        final VersionNumber high = VersionNumber.fromJvmVersion("1.7.0_1");

        assertThat(medium.compareTo(high), is(-1));
        assertThat(lower.compareTo(high), is(-1));
        assertThat(medium.compareTo(lower), is(1));
        assertThat(high.compareTo(lower), is(1));
        assertThat(high.compareTo(high), is(0));

        assertThat(medium.isAtLeast(medium), is(true));
        assertThat(medium.isAtLeast(lower), is(true));
        assertThat(high.isAtLeast(high), is(true));
        assertThat(high.isAtLeast(medium), is(true));
        assertThat(high.isAtLeast(lower), is(true));

        assertThat(medium.isAtLeast(high), is(false));
        assertThat(lower.isAtLeast(high), is(false));
        assertThat(lower.isAtLeast(medium), is(false));
    }

    @Test
    public void compareJvmMicroVersions()
    {
        final VersionNumber lower = VersionNumber.fromJvmVersion("1.6.0_1");
        final VersionNumber medium = VersionNumber.fromJvmVersion("1.6.0_20");
        final VersionNumber high = VersionNumber.fromJvmVersion("1.6.0_24");

        assertThat(medium.compareTo(high), is(-1));
        assertThat(lower.compareTo(high), is(-1));
        assertThat(medium.compareTo(lower), is(1));
        assertThat(high.compareTo(lower), is(1));
        assertThat(high.compareTo(high), is(0));

        assertThat(medium.isAtLeast(medium), is(true));
        assertThat(medium.isAtLeast(lower), is(true));
        assertThat(high.isAtLeast(high), is(true));
        assertThat(high.isAtLeast(medium), is(true));
        assertThat(high.isAtLeast(lower), is(true));

        assertThat(medium.isAtLeast(high), is(false));
        assertThat(lower.isAtLeast(high), is(false));
        assertThat(lower.isAtLeast(medium), is(false));
    }

    @Test
    public void compareJvmVersions()
    {
        final VersionNumber base = VersionNumber.fromJvmVersion("1.2.3_4");
        final VersionNumber patchIncrease = VersionNumber.fromJvmVersion("1.2.3_5");
        final VersionNumber patchDecrease = VersionNumber.fromJvmVersion("1.2.3_3");
        final VersionNumber microDecrease = VersionNumber.fromJvmVersion("1.2.2_4");
        final VersionNumber microIncreasePatchDecrease = VersionNumber.fromJvmVersion("1.2.4_2");

        assertThat(base.isAtLeast(base), is(true));
        assertThat(base.isAtLeast(patchIncrease), is(false));
        assertThat(patchIncrease.isAtLeast(base), is(true));
        assertThat(base.isAtLeast(patchDecrease), is(true));
        assertThat(base.isAtLeast(microDecrease), is(true));
        assertThat(base.isAtLeast(microIncreasePatchDecrease), is(false));
        assertThat(microIncreasePatchDecrease.isAtLeast(base), is(true));

    }
}
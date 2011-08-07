package com.reucon.commons.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MdcInsertingServletFilterTest
{
    private MdcInsertingServletFilter filter;

    @Before
    public void setUp()
    {
        filter = new MdcInsertingServletFilter();
    }

    @After
    public void tearDown() throws Exception
    {
        filter.clearMDC();
    }

    @Test
    public void testInsertIntoMDC() throws Exception
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteHost()).thenReturn("1.2.3.4");
        when(request.getRemoteUser()).thenReturn("Hirsch");
        when(request.getServletPath()).thenReturn("/app");
        when(request.getPathInfo()).thenReturn("/me");
        filter.insertIntoMDC(request);
        assertEquals("1.2.3.4", MDC.get(MdcInsertingServletFilter.REQUEST_REMOTE_HOST_MDC_KEY));
        assertEquals("Hirsch", MDC.get(MdcInsertingServletFilter.REQUEST_REMOTE_USER));
        assertEquals("/app/me", MDC.get(MdcInsertingServletFilter.REQUEST_REQUEST_PATH));
    }

    @Test
    public void testClearMDC() throws Exception
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);

        when(request.getRemoteHost()).thenReturn("1.2.3.4");
        doThrow(new RuntimeException()).when(chain).doFilter(request, response);

        try
        {
            filter.doFilter(request, response, chain);
            fail("No RuntimeException thrown");
        }
        catch (RuntimeException e)
        {

        }
        assertNull("MDC was not cleared", MDC.get(MdcInsertingServletFilter.REQUEST_REMOTE_HOST_MDC_KEY));

        verify(request).getRemoteHost();
        verify(chain).doFilter(request, response);
    }
}

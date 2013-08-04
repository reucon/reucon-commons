package com.reucon.commons.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MdcInsertingServletFilterTest
{
    private MdcInsertingServletFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @Before
    public void setUp()
    {
        filter = new MdcInsertingServletFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @After
    public void tearDown() throws Exception
    {
        MDC.clear();
    }

    @Test
    public void testInsertIntoMDC() throws Exception
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteHost()).thenReturn("1.2.3.4");
        when(request.getRemoteUser()).thenReturn("Hirsch");
        when(request.getServletPath()).thenReturn("/app");
        when(request.getPathInfo()).thenReturn("/me");
        final AtomicBoolean doFilterCalled = new AtomicBoolean(false);
        filter.doFilter(request, response, new FilterChain()
        {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException
            {
                assertEquals("1.2.3.4", MDC.get(MdcInsertingServletFilter.REQUEST_REMOTE_HOST_MDC_KEY));
                assertEquals("Hirsch", MDC.get(MdcInsertingServletFilter.REQUEST_REMOTE_USER));
                assertEquals("/app/me", MDC.get(MdcInsertingServletFilter.REQUEST_REQUEST_PATH));
                doFilterCalled.set(true);
            }
        });
        assertTrue("doFilter() has not been called", doFilterCalled.get());
    }

    @Test
    public void testClearMDC() throws Exception
    {
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

    @Test
    public void testRestoreMDC() throws Exception
    {
        when(request.getRemoteHost()).thenReturn("1.2.3.4");
        when(request.getRemoteUser()).thenReturn("Hirsch");
        when(request.getServletPath()).thenReturn("/app");
        when(request.getPathInfo()).thenReturn("/me");

        MDC.put(MdcInsertingServletFilter.REQUEST_REMOTE_USER, "foo");
        filter.doFilter(request, response, chain);
        assertNull("MDC was not restored", MDC.get(MdcInsertingServletFilter.REQUEST_REMOTE_HOST_MDC_KEY));
        assertEquals("foo", MDC.get(MdcInsertingServletFilter.REQUEST_REMOTE_USER));
    }
}

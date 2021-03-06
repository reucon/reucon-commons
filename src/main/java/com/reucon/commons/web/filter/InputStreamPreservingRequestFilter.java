package com.reucon.commons.web.filter;

import com.reucon.commons.web.request.CachedHttpRequestWrapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

public class InputStreamPreservingRequestFilter extends GenericFilterBean
{
    /**
     * Name of the attribute to get hold of the request content
     */
    public static final String REQUEST_ATTRIBUTE = "com.reucon.commons.web.filter.preservedInputStream";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        
        final CachedHttpRequestWrapper wrappedRequest = new CachedHttpRequestWrapper(request);
        
        wrappedRequest.setAttribute(REQUEST_ATTRIBUTE, wrappedRequest.getBytes());

        chain.doFilter(wrappedRequest, response);
    }
}

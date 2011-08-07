package com.reucon.commons.web;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet filter based on logback's MDCInsertingServletFilter with support the following additional keys:
 * <ul>
 * <li>req.requestPath</li>
 * <li>req.remoteUser</li>
 * <li>req.sessionId</li>
 * </ul>
 * Of course the original keys from MDCInsertingServletFilter are also supported:
 * <ul>
 * <li>req.remoteHost</li>
 * <li>req.userAgent</li>
 * <li>req.requestURI</li>
 * <li>req.queryString</li>
 * <li>req.requestURL</li>
 * <li>req.xForwardedFor</li>
 * </ul>
 *
 * @since 2.0.0
 */
public class MdcInsertingServletFilter implements Filter
{
    public static final String REQUEST_REMOTE_HOST_MDC_KEY = "req.remoteHost";
    public static final String REQUEST_USER_AGENT_MDC_KEY = "req.userAgent";
    public static final String REQUEST_REQUEST_URI = "req.requestURI";
    public static final String REQUEST_QUERY_STRING = "req.queryString";
    public static final String REQUEST_REQUEST_URL = "req.requestURL";
    public static final String REQUEST_X_FORWARDED_FOR = "req.xForwardedFor";

    public static final String REQUEST_REQUEST_PATH = "req.requestPath";
    public static final String REQUEST_REMOTE_USER = "req.remoteUser";
    public static final String REQUEST_SESSION_ID = "req.sessionId";

    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    @Override
    public void destroy()
    {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        insertIntoMDC(request);
        try
        {
            chain.doFilter(request, response);
        }
        finally
        {
            clearMDC();
        }
    }

    void insertIntoMDC(ServletRequest request)
    {
        MDC.put(REQUEST_REMOTE_HOST_MDC_KEY, request.getRemoteHost());

        if (request instanceof HttpServletRequest)
        {
            // from MDCInsertingServletFilter
            final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            MDC.put(REQUEST_REQUEST_URI, httpServletRequest.getRequestURI());
            final StringBuffer requestURL = httpServletRequest.getRequestURL();
            if (requestURL != null)
            {
                MDC.put(REQUEST_REQUEST_URL, requestURL.toString());
            }
            MDC.put(REQUEST_QUERY_STRING, httpServletRequest.getQueryString());
            MDC.put(REQUEST_USER_AGENT_MDC_KEY, httpServletRequest.getHeader(USER_AGENT_HEADER));
            MDC.put(REQUEST_X_FORWARDED_FOR, httpServletRequest.getHeader(X_FORWARDED_FOR_HEADER));

            // additional attributes
            MDC.put(REQUEST_REQUEST_PATH, getRequestPath(httpServletRequest));
            MDC.put(REQUEST_REMOTE_USER, httpServletRequest.getRemoteUser());
            final HttpSession session = httpServletRequest.getSession(false);
            if (session != null)
            {
                MDC.put(REQUEST_SESSION_ID, session.getId());
            }
        }
    }

    void clearMDC()
    {
        MDC.remove(REQUEST_REMOTE_HOST_MDC_KEY);
        MDC.remove(REQUEST_REQUEST_URI);
        MDC.remove(REQUEST_QUERY_STRING);
        MDC.remove(REQUEST_REQUEST_URL);
        MDC.remove(REQUEST_USER_AGENT_MDC_KEY);
        MDC.remove(REQUEST_X_FORWARDED_FOR);

        MDC.remove(REQUEST_REQUEST_PATH);
        MDC.remove(REQUEST_REMOTE_USER);
        MDC.remove(REQUEST_SESSION_ID);
    }

    String getRequestPath(HttpServletRequest request)
    {
        final StringBuilder sb = new StringBuilder();
        final String servletPath = request.getServletPath();
        if (servletPath != null)
        {
            sb.append(servletPath);
        }
        final String pathInfo = request.getPathInfo();
        if (pathInfo != null)
        {
            sb.append(pathInfo);
        }
        return sb.toString();
    }
}
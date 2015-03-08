package com.reucon.commons.web.exception.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Model class holding the data of an exception report.
 */
public class ExceptionReport implements Serializable
{
    private static final long serialVersionUID = 3L;
    private String id;
    private Throwable exception;

    private String remoteAddress;
    private String remoteHost;
    private String remoteUser;
    private String authType;
    private String protocol;
    private String httpMethod;
    private String characterEncoding;
    private String contentType;
    private String contextPath;
    private String pathInfo;
    private String pathTranslated;
    private String queryString;
    private String requestURI;
    private String scheme;
    private String serverName;
    private Integer serverPort;
    private String servletPath;
    private String locale;
    private List<Locale> locales;
    private String principalName;
    
    private Map<String, String[]> requestParameters;
    private Map<String, List<String>> requestHeaders;
    
    private Map<String, Object> requestAttributes;
    
    private Long sessionCreationTime;
    private String sessionId;
    private Long sessionLastAccessedTime;
    private Integer sessionMaxInactiveInterval;
    private Map<String, Object> sessionAttributes;
    
    transient private InputStream inputStream;
            
    public ExceptionReport(Throwable exception, HttpServletRequest request)
    {
        this(createId(request), exception, request);
    }
    
    public ExceptionReport(String id, Throwable exception, HttpServletRequest request)
    {
        this.id = id;
        this.exception = exception;
        
        remoteAddress = request.getRemoteAddr();
        remoteHost = request.getRemoteHost();
        remoteUser = request.getRemoteUser();
        authType = request.getAuthType();
        protocol = request.getProtocol();
        httpMethod = request.getMethod();
        characterEncoding = request.getCharacterEncoding();
        contentType = request.getContentType();
        contextPath = request.getContextPath();
        pathInfo = request.getPathInfo();
        pathTranslated = request.getPathTranslated();
        queryString = request.getQueryString();
        requestURI = request.getRequestURI();
        scheme = request.getScheme();
        serverName = request.getServerName();
        serverPort = request.getServerPort();
        servletPath = request.getServletPath();
        locale = request.getLocale().toString();
        locales = Collections.list(request.getLocales());
        
        if (request.getUserPrincipal() != null)
        {
            principalName = request.getUserPrincipal().getName();
        }
        
        requestParameters = request.getParameterMap();
        
        requestHeaders = new HashMap<>();
        Collections.list(request.getHeaderNames()).forEach(
                h -> requestHeaders.put(h, Collections.list(request.getHeaders(h)))
        );

        requestAttributes = new HashMap<>();
        Collections.list(request.getAttributeNames()).forEach(
                a -> requestAttributes.put(a, request.getAttribute(a))
        );
        
        final HttpSession session = request.getSession(false);
        if (session != null)
        {
            sessionCreationTime = session.getCreationTime();
            sessionId = session.getId();
            sessionLastAccessedTime = session.getLastAccessedTime();
            sessionMaxInactiveInterval = session.getMaxInactiveInterval();
            
            sessionAttributes = new HashMap<>();
            Collections.list(request.getSession().getAttributeNames()).forEach(
                s -> sessionAttributes.put(s, request.getSession().getAttribute(s))
            );
        }
        
        try
        {
            this.inputStream = request.getInputStream();
        }
        catch (IOException ex)
        {
            //
        }
    }
    

    /**
     * Sets the id of this report, must not be {@code null}.
     * @param id the id of the report
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the id of the report. This is usually a unique identifier for the report,
     * but could include additional information like the context path of the web application where
     * the report is generated.
     * @return id of the report
     */
    public String getId()
    {
        return id;
    }

    public Throwable getException()
    {
        return exception;
    }

    public void setException(Throwable exception)
    {
        this.exception = exception;
    }

    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress)
    {
        this.remoteAddress = remoteAddress;
    }

    public String getRemoteHost()
    {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
    }

    public String getRemoteUser()
    {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser)
    {
        this.remoteUser = remoteUser;
    }

    public String getPrincipalName()
    {
        return principalName;
    }

    public void setPrincipalName(String principalName)
    {
        this.principalName = principalName;
    }

    public String getAuthType()
    {
        return authType;
    }

    public void setAuthType(String authType)
    {
        this.authType = authType;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getHttpMethod()
    {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod)
    {
        this.httpMethod = httpMethod;
    }

    public String getCharacterEncoding()
    {
        return characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }

    public String getPathInfo()
    {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo)
    {
        this.pathInfo = pathInfo;
    }

    public String getPathTranslated()
    {
        return pathTranslated;
    }

    public void setPathTranslated(String pathTranslated)
    {
        this.pathTranslated = pathTranslated;
    }

    public String getQueryString()
    {
        return queryString;
    }

    public void setQueryString(String queryString)
    {
        this.queryString = queryString;
    }

    public String getRequestURI()
    {
        return requestURI;
    }

    public void setRequestURI(String requestURI)
    {
        this.requestURI = requestURI;
    }

    public String getScheme()
    {
        return scheme;
    }

    public void setScheme(String scheme)
    {
        this.scheme = scheme;
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public Integer getServerPort()
    {
        return serverPort;
    }

    public void setServerPort(Integer serverPort)
    {
        this.serverPort = serverPort;
    }

    public String getServletPath()
    {
        return servletPath;
    }

    public void setServletPath(String servletPath)
    {
        this.servletPath = servletPath;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public List<Locale> getLocales()
    {
        return locales;
    }

    public void setLocales(List<Locale> locales)
    {
        this.locales = locales;
    }

    public Map<String, List<String>> getRequestHeaders()
    {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, List<String>> requestHeaders)
    {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, String[]> getRequestParameters()
    {
        return requestParameters;
    }

    public void setRequestParameters(Map<String, String[]> requestParameters)
    {
        this.requestParameters = requestParameters;
    }

    public Map<String, Object> getRequestAttributes()
    {
        return requestAttributes;
    }

    public void setRequestAttributes(Map<String, Object> requestAttributes)
    {
        this.requestAttributes = requestAttributes;
    }

    public Map<String, Object> getSessionAttributes()
    {
        return sessionAttributes;
    }

    public void setSessionAttributes(Map<String, Object> sessionAttributes)
    {
        this.sessionAttributes = sessionAttributes;
    }

    public Long getSessionCreationTime()
    {
        return sessionCreationTime;
    }

    public void setSessionCreationTime(Long sessionCreationTime)
    {
        this.sessionCreationTime = sessionCreationTime;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public Long getSessionLastAccessedTime()
    {
        return sessionLastAccessedTime;
    }

    public void setSessionLastAccessedTime(Long sessionLastAccessedTime)
    {
        this.sessionLastAccessedTime = sessionLastAccessedTime;
    }

    public Integer getSessionMaxInactiveInterval()
    {
        return sessionMaxInactiveInterval;
    }

    public void setSessionMaxInactiveInterval(Integer sessionMaxInactiveInterval)
    {
        this.sessionMaxInactiveInterval = sessionMaxInactiveInterval;
    }
    

    /**
     * Creates a new exception id for the given request. The id is made of the
     * request's context path (the path where the webapp is deployed to) and a
     * random {@linkplain UUID}.
     * 
     * @param request the request to create the id for.
     * @return the created exception id.
     */
    static String createId(HttpServletRequest request)
    {
        StringBuffer sb;
        String base;

        sb = new StringBuffer();
        base = request.getContextPath().replace('/', '_');
        if (base.startsWith("_"))
        {
            base = base.substring(1);
        }
        if (base.length() != 0)
        {
            sb.append(base);
        }
        else
        {
            sb.append("ROOT");
        }
        sb.append("-");
        sb.append(UUID.randomUUID().toString());

        return sb.toString();
    }
   

}

package com.reucon.commons.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Proxy filter that delegates execution to a filter discovered through reflection.
 * The target filter is set through the init paramter <code>target-filter-class</code>.
 * If the target filter is not available on the classpath this filter does nothing.
 * <p>
 * This is useful for filters available in jars placed directly into tomcat's
 * common/lib directory used for performance logging or other optional tasks that are
 * not required for production use.  
 * <p>
 * Example:
 * <code>
 * &lt;filter&gt;
 *   &lt;filter-name&gt;JAMonFilter&lt;/filter-name&gt;
 *   &lt;filter-class&gt;com.reucon.commons.web.OptionalFilter&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;target-filter-class&lt;/param-name&gt;
 *     &lt;param-value&gt;com.jamonapi.JAMonFilter&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/filter&gt;
 * </code>
 * 
 * @since 1.2.0
 */
public class OptionalFilter implements Filter
{
    public static final String TARGET_FILTER_CLASS_PARAMETER = "target-filter-class";
    private final Log logger = LogFactory.getLog(getClass());
    private Filter delegate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        final String targetClassName;
        final Object targetObject;

        targetClassName = filterConfig.getInitParameter(TARGET_FILTER_CLASS_PARAMETER);
        if (targetClassName == null)
        {
            logger.warn("You must specify the target filter by setting the " + TARGET_FILTER_CLASS_PARAMETER
                    + " init parameter");
            return;
        }

        try
        {
            targetObject = Class.forName(targetClassName).newInstance();
        }
        catch (ClassNotFoundException e)
        {
            logger.info("Unable to load target filter class '" + targetClassName + "'.");
            return;
        }
        catch (Exception e)
        {
            logger.info("Unable to create instance of filter class '" + targetClassName + "'.", e);
            return;
        }

        if (!(targetObject instanceof Filter))
        {
            logger.warn("Target class '" + targetClassName + "' is not a javax.servlet.Filter.");
            return;
        }

        delegate = (Filter) targetObject;
        delegate.init(filterConfig);
    }

    @Override
    public void destroy()
    {
        if (delegate != null)
        {
            delegate.destroy();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        if (delegate != null)
        {
            delegate.doFilter(request, response, chain);
        }
        else
        {
            chain.doFilter(request, response);
        }
    }
}

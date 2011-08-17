package com.reucon.commons.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * A Spring Web MVC ExceptionResolver that writes an exception report to the log
 * directory and then dispatches to an error view that can display a custom
 * error message including the exception id for reference.
 * <p>
 * By default exception reports are written to ${catalina.base}/logs/exceptions.
 * <p>
 * You can provide different views depending on the exception that is thrown and
 * the spring handler that caused the exception. For details see
 * {@link SimpleMappingExceptionResolver}.
 * <p>
 * To use the SpringExceptionReportGenerator add the following to your servlet
 * context:
 * 
 * <pre>
 * &lt;bean id=&quot;exceptionResolver&quot; class=&quot;com.reucon.commons.web.SpringExceptionReportGenerator&quot;&gt;
 *     &lt;property name=&quot;exceptionMappings&quot;&gt;
 *         &lt;props&gt;
 *             &lt;prop key=&quot;java.lang.Exception&quot;&gt;exception&lt;/prop&gt;
 *         &lt;/props&gt;
 *     &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * @see ExceptionReportGenerator
 * @see SimpleMappingExceptionResolver
 */
public class SpringExceptionReportGenerator extends SimpleMappingExceptionResolver
{
    /**
     * The default exceptionIdAttribute: "exceptionId".
     */
    public static final String DEFAULT_EXCEPTION_ID_ATTRIBUTE = "exceptionId";
    
    /**
     * The default stackTraceAttribute: "stackTrace".
     */
    public static final String DEFAULT_STACK_TRACE_ATTRIBUTE = "stackTrace";
    private ExceptionReportGenerator exceptionReportGenerator;
    private String exceptionIdAttribute = DEFAULT_EXCEPTION_ID_ATTRIBUTE;
    private String stackTraceAttribute = DEFAULT_STACK_TRACE_ATTRIBUTE;

    public SpringExceptionReportGenerator()
    {
        super();
        this.exceptionReportGenerator = new ExceptionReportGenerator();
    }

    /**
     * Set the name of the model attribute as which the exception id should be
     * exposed. Default is "exceptionId".
     * <p>
     * This can be either set to a different attribute name or to
     * <code>null</code> for not exposing an exception id attribute at all.
     * 
     * @see #DEFAULT_EXCEPTION_ID_ATTRIBUTE
     */
    public void setExceptionIdAttribute(String exceptionIdAttribute)
    {
        this.exceptionIdAttribute = exceptionIdAttribute;
    }

    /**
     * Set the name of the model attribute as which the stack trace should be
     * exposed. Default is "stackTrace".
     * <p>
     * This can be either set to a different attribute name or to
     * <code>null</code> for not exposing a stack trace attribute at all.
     * 
     * @see #DEFAULT_STACK_TRACE_ATTRIBUTE
     */
    public void setStackTraceAttribute(String stacktraceAttribute)
    {
        this.stackTraceAttribute = stacktraceAttribute;
    }

    /**
     * Sets the directory where to store the exception reports.
     * <p>
     * Default is <tt>${catalina.base}/logs/exceptions</tt> or if not available
     * <tt>${java.io.tmpdir}</tt>.
     * 
     * @param logDirectory the directory where to store the exception reports.
     * @see ExceptionReportGenerator#setLogDirectory(String)
     */
    public void setLogDirectory(String logDirectory)
    {
        exceptionReportGenerator.setLogDirectory(logDirectory);
    }

    protected ModelAndView getModelAndView(String viewName, Exception ex, HttpServletRequest request)
    {
        ModelAndView mav;
        String exceptionId;

        // write the exception report
        exceptionId = exceptionReportGenerator.writeExceptionReport(ex, request);

        mav = super.getModelAndView(viewName, ex);
        if (mav != null && exceptionId != null && exceptionIdAttribute != null)
        {
            mav.addObject(exceptionIdAttribute, exceptionId);
        }
        if (mav != null && stackTraceAttribute != null)
        {
            mav.addObject(stackTraceAttribute, exceptionReportGenerator.getStackTrace(ex));
        }
        return mav;
    }
}

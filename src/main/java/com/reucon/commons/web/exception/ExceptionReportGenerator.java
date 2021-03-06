package com.reucon.commons.web.exception;

import com.reucon.commons.web.exception.storage.FilesystemStorage;
import com.reucon.commons.web.exception.model.ExceptionReport;
import com.reucon.commons.web.exception.renderer.AbstractExceptionRenderer;
import com.reucon.commons.web.exception.renderer.JacksonExceptionRenderer;
import com.reucon.commons.web.exception.renderer.StringExceptionRenderer;
import com.reucon.commons.web.exception.storage.ExceptionStorage;
import com.reucon.commons.web.exception.storage.ExceptionStorageEntry;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;

/**
 * Writes an exception report providing details about the exception and the {@linkplain HttpServletRequest} to the log directory.
 * A unique exception id is generated that is used as filename and can be exposed to the user to provide a reference when
 * contacting support.
 * <p>
 * The exception report contains the following information:
 * <ul>
 * <li>Request headers
 * <li>Request parameters and attributes
 * <li>Session attributes
 * <li>Full stack trace of the exception
 * </ul>
 */
public class ExceptionReportGenerator
{
    private boolean jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", getClass().getClassLoader() ) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", getClass().getClassLoader());
    
    private final Log logger = LogFactory.getLog(getClass());
    private FilesystemStorage storage = new FilesystemStorage();

    public void setStorage(FilesystemStorage storage)
    {
        this.storage = storage;
    }

    public void setLogDirectory(String logDirectory)
    {
        storage.setLogDirectory(logDirectory);
    }

    /**
     * This should never be used outside of test cases.
     * @param jackson2Present whether jackson is present
     */
    public void setJackson2Present(boolean jackson2Present)
    {
        this.jackson2Present = jackson2Present;
    }
    
    /**
     * Writes an exception report for the given exception and request and returns the exception id.
     *
     * @param ex exception to write the exception report for
     * @param request request to write the exception report for
     * @return the generated exception id.
     */
    public String writeExceptionReport(Exception ex, HttpServletRequest request)
    {

        final ExceptionReport exceptionReport = new ExceptionReport(ex, request);
        try
        {
            return writeExceptionReport(storage, exceptionReport, ex);
        }
        catch (Exception e)
        {
            logger.warn("Unable to write exception report " + exceptionReport.getId(), e);
            return null;
        }
    }

    String writeExceptionReport(final ExceptionStorage storage, final ExceptionReport exceptionReport, Exception ex) throws IOException
    {
        final AbstractExceptionRenderer exceptionRenderer = determineRenderer(exceptionReport);
        final ExceptionStorageEntry report = exceptionRenderer.render(exceptionReport, storage);
        
        final String writtenToDirectoryName = report.location();

        if (writtenToDirectoryName != null)
        {
            logger.info("Exception report " + exceptionReport.getId() + " successfully written to " + writtenToDirectoryName + " for: "
                    + ex.getMessage());
        }
        else
        {
            logger.warn("Unable to write exception report " + exceptionReport.getId(), ex);
        }

        return exceptionReport.getId();
    }

    AbstractExceptionRenderer determineRenderer(ExceptionReport exceptionReport)
    {
        if (! jackson2Present)
        {
            return new StringExceptionRenderer();
        }
        return new JacksonExceptionRenderer();
    }    
}
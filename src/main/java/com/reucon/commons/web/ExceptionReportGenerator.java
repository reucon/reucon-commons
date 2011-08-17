package com.reucon.commons.web;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Writes an exception report providing details about the exception and the
 * {@linkplain HttpServletRequest} to the log directory. A unique exception id
 * is generated that is used as filename and can be exposed to the user to
 * provide a reference when contacting support.
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
    private final Log logger = LogFactory.getLog(getClass());
    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS z");
    private String logDirectory;

    /**
     * Sets the directory where to store the exception reports.
     * <p>
     * Default is <tt>${catalina.base}/logs/exceptions</tt> or if not
     * available <tt>${java.io.tmpdir}</tt>.
     * 
     * @param logDirectory the directory where to store the exception reports.
     */
    public void setLogDirectory(String logDirectory)
    {
        this.logDirectory = logDirectory;
    }

    /**
     * Writes an exception report for the given exception and request and
     * returns the exception id.
     * 
     * @param ex exception to write the exception report for
     * @param request request to write the exception report for
     * @return the generated exception id.
     */
    public String writeExceptionReport(Exception ex, HttpServletRequest request)
    {
        String id;
        String writtenToDirectoryName = null;

        id = createId(request);

        for (String dirname : getDirectoryNamesToTry())
        {
            try
            {
                writeExceptionReport(dirname, id, ex, request);
                writtenToDirectoryName = dirname;
                break;
            }
            catch (IOException e)
            {
            }
        }

        if (writtenToDirectoryName != null)
        {
            logger.info("Exception report " + id + " successfully written to " + writtenToDirectoryName + " for: "
                    + ex.getMessage());
        }
        else
        {
            logger.warn("Unable to write exception report " + id, ex);
        }

        return id;
    }

    void writeExceptionReport(String dirname, String id, Exception ex, HttpServletRequest request) throws IOException
    {
        Date now;
        String filename;
        Writer writer = null;

        now = Calendar.getInstance().getTime();
        filename = dirname + File.separator + id + ".log";

        try
        {
            File exceptionsDir = new File(dirname);
            if (!exceptionsDir.isDirectory())
            {
                exceptionsDir.mkdirs();
            }
            writer = new FileWriter(filename);
            writer.write("Exception Id:          " + id + "\n");
            writer.write("Time:                  " + dateFormat.format(now) + "\n\n");
            writeContext(writer, request);
            writer.write("Exception:\n\n");
            writeThrowable(writer, ex);
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }

    /**
     * Creates a new exception id for the given request. The id is made of the
     * requet's context path (the path where the webapp is deployed to) and a
     * random {@linkplain UUID}.
     * 
     * @param request the request to create the id for.
     * @return the created exception id.
     */
    String createId(HttpServletRequest request)
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

    /**
     * Returns a list of directories to try to store the report in.
     * <p>
     * The following directories are included:
     * <ul>
     * <li>First choice is {@link #logDirectory} if set.
     * <li>Second choice is ${catalina.base}/logs/exceptions if available.
     * <li>Fallback is ${java.io.tmpdir}.
     * </ul>
     * 
     * @return a list of directories to try to store the report in.
     */
    List<String> getDirectoryNamesToTry()
    {
        List<String> directoryNamesToTry;

        directoryNamesToTry = new ArrayList<String>();
        if (logDirectory != null)
        {
            directoryNamesToTry.add(logDirectory);
        }

        // try catalina.base/logs
        try
        {
            String catalinaBase = System.getProperty("catalina.base");
            if (catalinaBase != null && catalinaBase.length() > 0)
            {
                directoryNamesToTry.add(catalinaBase + File.separator + "logs" + File.separator + "exceptions");
            }
        }
        catch (SecurityException e)
        {
            // ignore
        }

        // try tmp dir
        try
        {
            String tmpDir = System.getProperty("java.io.tmpdir");
            if (tmpDir != null && tmpDir.length() > 0)
            {
                directoryNamesToTry.add(tmpDir);
            }
        }
        catch (SecurityException e)
        {
            // ignore
        }

        return directoryNamesToTry;
    }

    void writeContext(Writer writer, HttpServletRequest request) throws IOException
    {
        writer.write("Request:\n\n");

        writer.write("RemoteAddr:            " + request.getRemoteAddr() + "\n");
        writer.write("RemoteHost:            " + request.getRemoteHost() + "\n");
        writer.write("RemoteUser:            " + request.getRemoteUser() + "\n");

        if (request.getUserPrincipal() != null)
        {
            writer.write("UserPrincipal:         " + request.getUserPrincipal().getName() + "\n");
        }
        writer.write("AuthType:              " + request.getAuthType() + "\n");
        writer.write("Method:                " + request.getMethod() + "\n");
        writer.write("CharacterEncoding:     " + request.getCharacterEncoding() + "\n");
        writer.write("ContentType:           " + request.getContentType() + "\n");
        writer.write("ContextPath:           " + request.getContextPath() + "\n");
        writer.write("PathInfo:              " + request.getPathInfo() + "\n");
        writer.write("PathTranslated:        " + request.getPathTranslated() + "\n");
        writer.write("Protocol:              " + request.getProtocol() + "\n");
        writer.write("QueryString:           " + request.getQueryString() + "\n");
        writer.write("RequestURI:            " + request.getRequestURI() + "\n");
        writer.write("Scheme:                " + request.getScheme() + "\n");
        writer.write("ServerName:            " + request.getServerName() + "\n");
        writer.write("ServerPort:            " + request.getServerPort() + "\n");
        writer.write("ServletPath:           " + request.getServletPath() + "\n");

        writer.write("Locale:                " + request.getLocale() + "\n");
        writer.write("Locales:               ");
        Enumeration locales = request.getLocales();
        while (locales.hasMoreElements())
        {
            Locale locale = (Locale) locales.nextElement();
            writer.write(locale + (locales.hasMoreElements() ? ", " : ""));
        }

        writer.write("\n");
        writer.write("\n");

        writer.write("Headers:\n\n");
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String headerName = (String) headerNames.nextElement();
            Enumeration headers = request.getHeaders(headerName);
            while (headers.hasMoreElements())
            {
                String header = (String) headers.nextElement();
                writer.write(headerName + ": " + header + "\n");
            }
        }
        writer.write("\n");

        writer.write("Request Parameters:\n\n");
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String parameterName = (String) parameterNames.nextElement();
            String[] parameterValues = request.getParameterValues(parameterName);
            for (int i = 0; i < parameterValues.length; i++)
            {
                String parameterValue = parameterValues[i];
                writer.write(parameterName + ": " + parameterValue + "\n");
            }
        }
        writer.write("\n\n");

        writer.write("Request Attributes:\n\n");
        Enumeration attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements())
        {
            String attributeName = (String) attributeNames.nextElement();
            writer.write(attributeName + ": " + request.getAttribute(attributeName) + "\n");
        }
        writer.write("\n\n");

        writer.write("Session Attributes:\n\n");
        HttpSession session = request.getSession(false);
        if (session == null)
        {
            writer.write("No session.\n");
        }
        else
        {
            writer.write("Session Id:            " + session.getId() + "\n");
            writer.write("Creation time:         " + session.getCreationTime() + " "
                    + dateFormat.format(new Date(session.getCreationTime())) + "\n");
            writer.write("Last accessed time:    " + session.getLastAccessedTime() + " "
                    + dateFormat.format(new Date(session.getLastAccessedTime())) + "\n");
            writer.write("Max inactive interval: " + session.getMaxInactiveInterval() + " seconds\n\n");

            Enumeration sessionAttributeNames = session.getAttributeNames();
            while (sessionAttributeNames.hasMoreElements())
            {
                String attributeName = (String) sessionAttributeNames.nextElement();
                writer.write(attributeName + ": " + session.getAttribute(attributeName) + "\n");
            }
        }
        writer.write("\n\n");
    }

    void writeThrowable(Writer writer, Throwable throwable) throws IOException
    {
        throwable.printStackTrace(new PrintWriter(writer));
        if (throwable.getCause() != null)
        {
            writer.write("\nCause:\n\n");
            writeThrowable(writer, throwable.getCause());
        }
    }

    /**
     * Returns the full stack trace as a string.
     * 
     * @param throwable the trowable to create the stacktrace for.
     * @return the full stack trace as a string.
     */
    public String getStackTrace(Throwable throwable)
    {
        CharArrayWriter writer;

        writer = new CharArrayWriter();
        try
        {
            writeThrowable(writer, throwable);
        }
        catch (IOException e)
        {
            // ignore
        }

        return writer.toString();
    }
}

package com.reucon.commons.web.exception;

import com.reucon.commons.web.exception.model.ExceptionReport;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.ServletInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.MARSHAL;

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
    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS z"); //FIXME not thread safe
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
        String writtenToDirectoryName = null;
        
        final ExceptionReport exceptionReport = new ExceptionReport(ex, request);

        for (String dirname : getDirectoryNamesToTry())
        {
            try
            {
                writeExceptionReport(dirname, exceptionReport);
                writtenToDirectoryName = dirname;
                break;
            }
            catch (IOException e)
            {
            }
        }

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

    void writeExceptionReport(String dirname, ExceptionReport exceptionReport) throws IOException
    {
        Date now;
        String filename;
        Writer writer = null;

        now = Calendar.getInstance().getTime();
        filename = dirname + File.separator + exceptionReport.getId() + ".log";

        try
        {
            File exceptionsDir = new File(dirname);
            if (!exceptionsDir.isDirectory())
            {
                exceptionsDir.mkdirs();
            }
            writer = new FileWriter(filename);
            writeExceptionReport(writer, now, exceptionReport);
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }

    void writeExceptionReport(Writer writer, Date date, ExceptionReport exceptionReport) throws IOException
    {
        writer.write("Exception Id:          " + exceptionReport.getId() + "\n");
        writer.write("Time:                  " + dateFormat.format(date) + "\n\n");
        writeContext(writer, exceptionReport);
        writer.write("Exception:\n\n");
        writeThrowable(writer, exceptionReport.getException());
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

        directoryNamesToTry = new ArrayList<>();
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

    void writeContext(Writer writer, ExceptionReport report) throws IOException
    {
        writer.write("Request:\n\n");

        writer.write("RemoteAddr:            " + report.getRemoteAddress()+ "\n");
        writer.write("RemoteHost:            " + report.getRemoteHost() + "\n");
        writer.write("RemoteUser:            " + report.getRemoteUser() + "\n");

        writer.write("UserPrincipal:         " + report.getPrincipalName() + "\n");
        writer.write("AuthType:              " + report.getAuthType() + "\n");
        writer.write("Method:                " + report.getHttpMethod()+ "\n");
        writer.write("CharacterEncoding:     " + report.getCharacterEncoding() + "\n");
        writer.write("ContentType:           " + report.getContentType() + "\n");
        writer.write("ContextPath:           " + report.getContextPath() + "\n");
        writer.write("PathInfo:              " + report.getPathInfo() + "\n");
        writer.write("PathTranslated:        " + report.getPathTranslated() + "\n");
        writer.write("Protocol:              " + report.getProtocol() + "\n");
        writer.write("QueryString:           " + report.getQueryString() + "\n");
        writer.write("RequestURI:            " + report.getRequestURI() + "\n");
        writer.write("Scheme:                " + report.getScheme() + "\n");
        writer.write("ServerName:            " + report.getServerName() + "\n");
        writer.write("ServerPort:            " + report.getServerPort() + "\n");
        writer.write("ServletPath:           " + report.getServletPath() + "\n");

        writer.write("Locale:                " + report.getLocale() + "\n");
        writer.write("Locales:               ");
        
        writer.write(report.getLocales()
            .stream()
            .map(l -> l.toString())
            .collect(Collectors.joining(", "))
        );
        
        writer.write("\n");
        writer.write("\n");

        writer.write("Headers:\n\n");
        writer.write(
            report.getRequestHeaders().entrySet().stream()
                .flatMap(e -> e.getValue().stream()
                    .map(v -> e.getKey()+ ": " + v)
                )
                .collect(Collectors.joining("\n")
                )
            );
        writer.write("\n");

        writer.write("Request Parameters:\n\n");
        writer.write(
            report.getRequestParameters().entrySet().stream()
                .flatMap(e -> Arrays.stream(e.getValue())
                    .map(v -> e.getKey()+ ": " + v) )
                .collect(Collectors.joining("\n")
                )
            );
        
        writer.write("\n\n");
        
        writer.write("Request Attributes:\n\n");
        writer.write(
            report.getRequestAttributes().entrySet().stream()
                .map(e -> e.getKey()+ ": " + e.getValue().toString())
                .collect(Collectors.joining("\n"))
            );
        writer.write("\n");
        

//        writer.write("Request Payload:\n\n");
//        
//        final ServletInputStream inputStream = request.getInputStream();
//        try
//        {
//            inputStream.reset();
//            //TODO
//            writer.write("input....");
//        }
//        catch (IOException ex)
//        {
//            writer.write(" -- not supported -- ");
//        }
//        
        writer.write("\n\n");

        writer.write("Session Attributes:\n\n");
        
        
        if (report.getSessionId() == null)
        {
            writer.write("No session.\n");
        }
        else
        {
            writer.write("Session Id:            " + report.getSessionId() + "\n");
            writer.write("Creation time:         " + report.getSessionCreationTime() + " "
                    + dateFormat.format(new Date(report.getSessionCreationTime())) + "\n");
            writer.write("Last accessed time:    " + report.getSessionLastAccessedTime() + " "
                    + dateFormat.format(new Date(report.getSessionLastAccessedTime())) + "\n");
            writer.write("Max inactive interval: " + report.getSessionMaxInactiveInterval() + " seconds\n\n");

            writer.write("Session Attributes:\n\n");
            writer.write(
                report.getSessionAttributes().entrySet().stream()
                    .map(e -> e.getKey()+ ": " + e.getValue().toString())
                    .collect(Collectors.joining("\n"))
                );
            writer.write("\n");
        
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

package com.reucon.commons.web.exception.storage;

import com.reucon.commons.web.exception.model.ExceptionReport;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides storage for exception reports
 */
public class FilesystemStorage
{
    private String logDirectory;
    private Writer writer;

    public FilesystemStorage()
    {
    }

    public FilesystemStorage(Writer writer)
    {
        this.writer = writer;
    }

    public FilesystemStorage(String logDirectory)
    {
        this.logDirectory = logDirectory;
    }

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
    
    public String writeExceptionReport(ExceptionReport exceptionReport)
    {
        final Date now = Calendar.getInstance().getTime();
        if(writer != null)
        {
            try
            {
                writeExceptionReport(writer, now, exceptionReport);
                return "$stream$";
            }
            catch (IOException e)
            {
                return null;
            }
        }
        
        for (String dirname : getDirectoryNamesToTry())
        {
            try
            {
                writeExceptionReport(now, dirname, exceptionReport);
                return dirname;
            }
            catch (IOException e)
            {
                return null;
            }
        }

        return null;
    }
    
    void writeExceptionReport(Date date, String dirname, ExceptionReport exceptionReport) throws IOException
    {
        String filename;
        Writer writer = null;

        filename = dirname + File.separator + exceptionReport.getId() + ".log";

        try
        {
            File exceptionsDir = new File(dirname);
            if (!exceptionsDir.isDirectory())
            {
                exceptionsDir.mkdirs();
            }
            writer = new FileWriter(filename);
            writeExceptionReport(writer, date, exceptionReport);
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
        final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS z");
        
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
        final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS z");
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

    
}

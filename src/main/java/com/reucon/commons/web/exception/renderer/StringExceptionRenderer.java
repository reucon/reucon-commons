package com.reucon.commons.web.exception.renderer;

import com.reucon.commons.web.exception.model.ExceptionReport;
import com.reucon.commons.web.filter.InputStreamPreservingRequestFilter;
import com.reucon.commons.web.request.CachedHttpRequestWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class StringExceptionRenderer extends AbstractExceptionRenderer
{
    
    @Override
    protected void writeExceptionReport(Writer writer, Date date, ExceptionReport exceptionReport) throws IOException
    {
        final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS z");
        
        writer.write("Exception Id:          " + exceptionReport.getId() + "\n");
        writer.write("Time:                  " + dateFormat.format(date) + "\n\n");
        writeContext(writer, exceptionReport);
        writer.write("Exception:\n\n");
        writeThrowable(writer, exceptionReport.getException());
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
                .filter(e -> !e.getKey().equals(InputStreamPreservingRequestFilter.REQUEST_ATTRIBUTE))
                .map(e -> e.getKey()+ ": " + e.getValue().toString())
                .collect(Collectors.joining("\n"))
            );
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

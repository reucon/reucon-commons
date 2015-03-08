package com.reucon.commons.web.exception;

import com.reucon.commons.web.exception.model.ExceptionReport;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.containsString;

public class ExceptionReportGeneratorTest
{
    private ExceptionReportGenerator reportGenerator;
    private MockHttpServletRequest httpServletRequest;
    private Date date;
    private String exceptionId;
    private Exception exception;
    private CharArrayWriter writer;

    @Before
    public void setUp() throws Exception
    {
        this.reportGenerator = new ExceptionReportGenerator();
        this.httpServletRequest = new MockHttpServletRequest();
        
        date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2000-01-02 01:02:03");
        exception = new Exception("sample exception");
        exceptionId = "ID:123:456";
        
        writer = new CharArrayWriter();
    }

    
    void writeExceptionReport() throws IOException
    {
        reportGenerator.writeExceptionReport(writer, date, new ExceptionReport(exceptionId, exception, httpServletRequest));
    }
    
    @Test
    public void writeShouldNotThrow() throws Exception
    {
        writeExceptionReport();
    }

    
    @Test
    public void reportContainsExceptionId() throws Exception
    {
        writeExceptionReport();
        
        final String report = writer.toString();
        
        assertThat(report, containsString("Exception Id:          ID:123:456"));
    }
    
    @Test
    public void reportContainsHeader() throws Exception
    {
        httpServletRequest.addHeader("firstHeader", "first Value");
        writeExceptionReport();
        
        final String report = writer.toString();
        
        assertThat(report, containsString("firstHeader: first Value"));
    }
    
    @Test
    public void reportContainsHeaders() throws Exception
    {
        httpServletRequest.addHeader("firstHeader", "first Value");
        httpServletRequest.addHeader("firstHeader", "second Value");
        writeExceptionReport();
        
        final String report = writer.toString();
        
        assertThat(report, containsString("firstHeader: first Value"));
        assertThat(report, containsString("firstHeader: second Value"));
    }
    
    @Test
    public void reportContainsParameter() throws Exception
    {
        httpServletRequest.setParameter("param", "parameter value");
        writeExceptionReport();
        
        final String report = writer.toString();
        
        assertThat(report, containsString("param: parameter value"));
    }
    
    @Test
    public void reportContainsParameterArray() throws Exception
    {
        httpServletRequest.setParameter("param", new String[]{"parameter value", "value 2"});
        writeExceptionReport();
        
        final String report = writer.toString();
        
        assertThat(report, containsString("param: parameter value"));
        assertThat(report, containsString("param: value 2"));
    }

    @Test
    public void reportContainsAttributes() throws Exception
    {
        httpServletRequest.setAttribute("attribute 1", "value1");
        httpServletRequest.setAttribute("attribute 2", "value2");
        writeExceptionReport();
        
        final String report = writer.toString();
        
        assertThat(report, containsString("attribute 1: value1"));
        assertThat(report, containsString("attribute 2: value2"));
    }
    
    @Test
    public void reportContainsSessionId() throws Exception
    {
        final MockHttpSession session = new MockHttpSession();
        httpServletRequest.setSession(session);
        
        writeExceptionReport();
        
        final String report = writer.toString();
        
        assertThat(report, containsString("Session Id:"));
    }
    
    @Test
    public void reportContainsSessionAttributes() throws Exception
    {
        final MockHttpSession session = new MockHttpSession();
        session.setAttribute("sessionAttribute 1", "value1");
        session.setAttribute("sessionAttribute 2", "value2");
        
        httpServletRequest.setSession(session);
        
        writeExceptionReport();
        
        final String report = writer.toString();
        
        assertThat(report, containsString("sessionAttribute 1: value1"));
        assertThat(report, containsString("sessionAttribute 2: value2"));
    }
    
}

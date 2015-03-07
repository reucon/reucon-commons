package com.reucon.commons.web;

import java.io.CharArrayWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

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

    
    @Test
    public void writeShouldNotThrow() throws Exception
    {
        reportGenerator.writeExceptionReport(writer, exceptionId, date, httpServletRequest, exception);
    }
    
    @Test
    public void reportContainsExceptionId() throws Exception
    {
        reportGenerator.writeExceptionReport(writer, exceptionId, date, httpServletRequest, exception);
        
        final String report = writer.toString();
        
        assertThat(report, containsString("Exception Id:          ID:123:456"));
    }
    
    @Test
    public void reportContainsParameter() throws Exception
    {
        httpServletRequest.setParameter("param", "parameter value");
        reportGenerator.writeExceptionReport(writer, exceptionId, date, httpServletRequest, exception);
        
        final String report = writer.toString();
        
        assertThat(report, containsString("param: parameter value"));
    }
    
    @Test
    public void reportContainsParameterArray() throws Exception
    {
        httpServletRequest.setParameter("param", new String[]{"parameter value", "value 2"});
        reportGenerator.writeExceptionReport(writer, exceptionId, date, httpServletRequest, exception);
        
        final String report = writer.toString();
        
        assertThat(report, containsString("param: parameter value"));
        assertThat(report, containsString("param: value 2"));
    }

    
}

package com.reucon.commons.web.exception.storage;

import com.reucon.commons.web.exception.model.ExceptionReport;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public abstract class ExceptionStorage
{
    public abstract ExceptionStorageEntry saveReport(ExceptionReport exceptionReport) throws IOException;
    
    
}

package com.reucon.commons.web.exception.storage;

import com.reucon.commons.web.exception.model.ExceptionReport;
import java.io.IOException;

public abstract class ExceptionStorage
{
    /**
     * Provides access to the storage for an exception report.
     * @param exceptionReport
     * @return an ExceptionStorageEntry where the exception report can be stored
     * @throws IOException 
     */
    public abstract ExceptionStorageEntry allocate(ExceptionReport exceptionReport) throws IOException;
}

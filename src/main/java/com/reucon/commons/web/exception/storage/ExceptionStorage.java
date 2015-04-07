package com.reucon.commons.web.exception.storage;

import com.reucon.commons.web.exception.model.ExceptionReport;
import java.io.IOException;

public abstract class ExceptionStorage
{
    /**
     * Provides access to the storage for an exception report.
     * @param exceptionReport exception report to store
     * @return an ExceptionStorageEntry where the exception report can be stored
     * @throws IOException in case the storage encounters an io exception
     */
    public abstract ExceptionStorageEntry allocate(ExceptionReport exceptionReport) throws IOException;
}

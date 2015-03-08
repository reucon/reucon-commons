package com.reucon.commons.web.exception.storage;

import java.io.IOException;
import java.io.Writer;

public interface ExceptionStorageEntry
{
    public abstract String location();
    public abstract Writer exceptionMetadataWriter() throws IOException;
    public abstract Writer exceptionPayloadWriter() throws IOException;
    
}

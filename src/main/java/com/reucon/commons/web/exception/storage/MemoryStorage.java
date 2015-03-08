package com.reucon.commons.web.exception.storage;

import com.reucon.commons.web.exception.model.ExceptionReport;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryStorage extends ExceptionStorage
{
    final private Map<String, MemoryStorageEntry> entries;
    private MemoryStorageEntry lastAdded;
    private int capacity = 20;
    
    public MemoryStorage()
    {
        entries = Collections.synchronizedMap(new LinkedHashMap<>(capacity));
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    public Map<String, MemoryStorageEntry> getEntries()
    {
        return entries;
    }
    
    public MemoryStorageEntry getLastReport()
    {
        return lastAdded;
    }
    
    @Override
    public ExceptionStorageEntry saveReport(ExceptionReport exceptionReport) throws IOException
    {
        MemoryStorageEntry entry = new MemoryStorageEntry();
        synchronized(entries)
        {
            if(entries.size() > capacity)
            {
                Map.Entry<String, MemoryStorageEntry> first = entries.entrySet().iterator().next();
                entries.remove(first.getKey());
            }
            entries.put(exceptionReport.getId(), entry);
            lastAdded = entry;
        }
        return entry;
    }
    
    public static class MemoryStorageEntry implements ExceptionStorageEntry
    {
        final private CharArrayWriter metadataWriter;
        final private CharArrayWriter payloadWriter;

        public MemoryStorageEntry()
        {
            this.metadataWriter = new CharArrayWriter();
            this.payloadWriter = new CharArrayWriter();
        }
        
        @Override
        public String location()
        {
            return "$mem$";
        }

        @Override
        public CharArrayWriter exceptionMetadataWriter()
        {
            return payloadWriter;
        }

        @Override
        public CharArrayWriter exceptionPayloadWriter()
        {
            return metadataWriter;
        }
    }

}

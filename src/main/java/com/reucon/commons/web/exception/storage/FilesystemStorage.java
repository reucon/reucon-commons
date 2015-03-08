package com.reucon.commons.web.exception.storage;

import com.reucon.commons.web.exception.model.ExceptionReport;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Provides storage for exception reports
 */
public class FilesystemStorage extends ExceptionStorage
{
    private String logDirectory;

    public FilesystemStorage()
    {
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
    
    @Override
    public ExceptionStorageEntry saveReport(ExceptionReport exceptionReport)
    {
        for (String dirname : getDirectoryNamesToTry())
        {
            File exceptionsDir = new File(dirname);
            if (!exceptionsDir.isDirectory())
            {
                exceptionsDir.mkdirs();
            }
            
            final String filename = exceptionReport.getId() + ".log";
            final FilesystemStorageEntry filesystemStorageEntry = new FilesystemStorageEntry(dirname, filename);
            return filesystemStorageEntry;
            
        }

        return null;
    }

    class FilesystemStorageEntry implements ExceptionStorageEntry
    {
        final private String basename;
        final private String path;

        public FilesystemStorageEntry(String path, String basename)
        {
            this.basename = basename;
            this.path = path;
        }

        @Override
        public String location()
        {
            return path + File.separator + basename + ".log";
        }

        @Override
        public Writer exceptionMetadataWriter() throws IOException
        {
            FileWriter fileWriter = new FileWriter(location());
            return fileWriter;
        }

        @Override
        public Writer exceptionPayloadWriter() throws IOException
        {
            FileWriter fileWriter = new FileWriter(path + File.separator + basename + ".bin");
            return fileWriter;
        }
        
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
}

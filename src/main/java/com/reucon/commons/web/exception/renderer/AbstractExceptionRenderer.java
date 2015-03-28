
package com.reucon.commons.web.exception.renderer;

import com.reucon.commons.web.exception.model.ExceptionReport;
import com.reucon.commons.web.exception.storage.ExceptionStorage;
import com.reucon.commons.web.exception.storage.ExceptionStorageEntry;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

public abstract class AbstractExceptionRenderer {

    public ExceptionStorageEntry render(ExceptionReport exceptionReport, ExceptionStorage storage) throws IOException
    {
        final Date now = Calendar.getInstance().getTime();
        
        final ExceptionStorageEntry storageEntry = storage.allocate(exceptionReport);
        writeExceptionReport(now, storageEntry, exceptionReport);
        
        return storageEntry;
    }
    
    protected void writeExceptionReport(Date date, ExceptionStorageEntry entry, ExceptionReport exceptionReport) throws IOException
    {
        try (Writer writer = entry.exceptionMetadataWriter())
        {
            writeExceptionReport(writer, date, exceptionReport);
        }
        try (OutputStream os = entry.exceptionPayloadOutputStream())
        {
            writePayload(os, exceptionReport);
        }
    }

    protected abstract void writeExceptionReport(Writer writer, Date date, ExceptionReport exceptionReport) throws IOException;

    void writePayload(OutputStream os, ExceptionReport exceptionReport) throws IOException
    {
        final Writer writer = new OutputStreamWriter(os);
        final InputStream inputStream = exceptionReport.getInputStream();
        if (inputStream == null)
        {
            writer.write("--null--");
            writer.close();
            return;
        }
        try
        {
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1)
            {
                os.write(bytes, 0, read);
            }
        }
        catch (IOException ex)
        {
            writer.write(" --io-error-- ");
            writer.write(ex.getMessage());
            writer.write("\n\n");
            ex.printStackTrace(new PrintWriter(writer));
        }
        finally
        {
            try
            {
                writer.close();
            }
            catch (IOException e)
            {
                writer.close();
            }
        }
    }
}

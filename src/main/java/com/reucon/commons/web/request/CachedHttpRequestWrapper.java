package com.reucon.commons.web.request;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.FileCopyUtils;
import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_POST;
import org.springframework.web.util.WebUtils;

public class CachedHttpRequestWrapper extends HttpServletRequestWrapper
{
    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final byte[] bytes;

    public CachedHttpRequestWrapper(HttpServletRequest request) throws IOException
    {
        super(request);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (!isFormPost())
        {
            FileCopyUtils.copy(request.getInputStream(), bos);
            this.bytes = bos.toByteArray();
            return;
        }
        writeRequestParamsToContent(bos);
        this.bytes = bos.toByteArray();
    }

    public byte[] getBytes()
    {
        return bytes;
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        return new CachedInputStream(bytes);
    }

    @Override
    public String getCharacterEncoding()
    {
        return super.getCharacterEncoding() != null ? super.getCharacterEncoding() : WebUtils.DEFAULT_CHARACTER_ENCODING;
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }
    
    private boolean isFormPost()
    {
        return (getContentType() != null && getContentType().contains(FORM_CONTENT_TYPE) && METHOD_POST.equalsIgnoreCase(getMethod()));
    }
    
    private void writeRequestParamsToContent(ByteArrayOutputStream bos)
    {
        try
        {
            if (bos.size() == 0)
            {
                String requestEncoding = getCharacterEncoding();
                Map<String, String[]> form = getParameterMap();
                for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();)
                {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext();)
                    {
                        String value = valueIterator.next();
                        bos.write(URLEncoder.encode(name, requestEncoding).getBytes());
                        if (value != null)
                        {
                            bos.write('=');
                            bos.write(URLEncoder.encode(value, requestEncoding).getBytes());
                            if (valueIterator.hasNext())
                            {
                                bos.write('&');
                            }
                        }
                    }
                    if (nameIterator.hasNext())
                    {
                        bos.write('&');
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private class CachedInputStream extends ServletInputStream
    {
        private final ByteArrayInputStream bis;

        private CachedInputStream(byte[] bytes)
        {
            this.bis = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read()
        {
            return bis.read();
        }

        @Override
        public int read(byte[] b, int off, int len)
        {
            return bis.read(b, off, len);
        }

        @Override
        public long skip(long n)
        {
            return bis.skip(n);
        }

        @Override
        public int available()
        {
            return bis.available();
        }

        @Override
        public boolean markSupported()
        {
            return bis.markSupported();
        }

        @Override
        public void mark(int readAheadLimit)
        {
            bis.mark(readAheadLimit);
        }

        @Override
        public void reset()
        {
            bis.reset();
        }

        @Override
        public void close() throws IOException
        {
            bis.close();
        }
    }
}

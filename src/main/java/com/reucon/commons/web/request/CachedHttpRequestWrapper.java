package com.reucon.commons.web.request;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.WebUtils;

public class CachedHttpRequestWrapper extends HttpServletRequestWrapper
{
    private final byte[] bytes;

    public CachedHttpRequestWrapper(HttpServletRequest request) throws IOException
    {
        super(request);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileCopyUtils.copy(request.getInputStream(), bos);
        this.bytes = bos.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        return new CachedInputStream(bytes);
    }

    @Override
    public String getCharacterEncoding()
    {
        return super.getCharacterEncoding() != null ? super.getCharacterEncoding()
                : WebUtils.DEFAULT_CHARACTER_ENCODING;
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
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

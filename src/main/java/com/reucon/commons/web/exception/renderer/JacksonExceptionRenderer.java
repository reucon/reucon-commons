package com.reucon.commons.web.exception.renderer;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.reucon.commons.web.exception.model.ExceptionReport;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;

/**
 * This renderer uses Jackson2 to serialize the exception report as JSON.
 * It uses a heuristic to determine which fields should not be serialized, this is necessary
 * to prevent endless recursion with a stack overflow. Therefore it may skip fields
 * you are interested in.
 */
public class JacksonExceptionRenderer extends AbstractExceptionRenderer
{
    private static final String FILTER_ID_REF = "filter properties by name";
    final static String[] ignoredFields =
    {
        "taskExecutor", 
        "asyncWebRequest",
        "inputStream",
        "callableInterceptors",
        "annotatedClasses",
        "container",
        "pipeline",
        "classloader",
        "classLoader",
        "beanFactory",
        "beanClassLoader",
        "inputStream",
        "servletContext"
//            "requestAttributes",
//            "sessionAttributes"
    };
    
    private final Log logger = LogFactory.getLog(getClass());
    private final ObjectMapper objectMapper;
    
    
    
    public JacksonExceptionRenderer()
    {
        final Jackson2ObjectMapperFactoryBean jackson2ObjectMapperFactoryBean = new Jackson2ObjectMapperFactoryBean();
        jackson2ObjectMapperFactoryBean.setIndentOutput(true);
        jackson2ObjectMapperFactoryBean.setFeaturesToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        jackson2ObjectMapperFactoryBean.setFeaturesToEnable(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID);
        jackson2ObjectMapperFactoryBean.afterPropertiesSet();

        objectMapper = jackson2ObjectMapperFactoryBean.getObject();
        objectMapper.addMixIn(Object.class, PropertyFilter.class);
        
        final FilterProvider filters = new SimpleFilterProvider().addFilter(FILTER_ID_REF, SimpleBeanPropertyFilter.serializeAllExcept(ignoredFields));
        objectMapper.setFilters(filters);
    }

    @Override
    protected void writeExceptionReport(Writer writer, Date date, ExceptionReport exceptionReport) throws IOException
    {
        if (exceptionReport == null)
        {
            logger.warn("Exception report to render is null");
            return;
        }
        if (objectMapper == null)
        {
            logger.warn("Can not render exception report, no object mapper to render exception report.");
            return;
        }
        if (writer == null)
        {
            logger.warn("Can not write exception report, no writer provided.");
            return;
        }
        exceptionReport.setDate(date);

        objectMapper.writeValue(writer, exceptionReport);
    }

    @JsonFilter(FILTER_ID_REF)
    class PropertyFilter
    {

    }

}

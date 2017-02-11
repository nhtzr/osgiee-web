package mx.nhtzr.osgiee.web.internal;

import mx.nhtzr.osgiee.web.MyFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by hero on 1/24/2017.
 */
@Component("myFilter")
@PreMatching
public class MyFilterImpl implements ContainerRequestFilter, ContainerResponseFilter, MyFilter {

    private static final Log log = LogFactory.getLog(MyFilterImpl.class);
    private final ObjectWriter objectWriter = new ObjectMapper()
            .disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS)
            .writerWithDefaultPrettyPrinter();

    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        System.out.println("request.toString() = " + request.toString());
        System.out.println("request.getMethod() = " + request.getMethod());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    }

    private String json(Object o) throws IOException {
        return objectWriter.writeValueAsString(o);
    }

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }

    @Context
    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

}

package mx.nhtzr.osgiee.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.*;
import java.io.IOException;

/**
 * Created by hero on 1/24/2017.
 */
@Component("myFilter")
@PreMatching
public class MyFilterImpl implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Log log = LogFactory.getLog(MyFilterImpl.class);
    private static final ObjectWriter objectWriter = new ObjectMapper()
            .disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS)
            .writerWithDefaultPrettyPrinter();

    private final HttpServletRequest request;

    public MyFilterImpl (HttpServletRequest request) {
        this.request = request;
    }

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

}

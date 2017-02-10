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
        log.info("request = " + json(context));
        final String prePath = context.getUriInfo().getPath();
        final String preQueryParams = objectWriter.writeValueAsString(context.getUriInfo().getQueryParameters());
        context.setRequestUri(context
                .getUriInfo()
                .getRequestUriBuilder()
                .queryParam("token", "tokenValue")
                .build());
        System.out.println("pre context.getUriInfo().getPath() = " + prePath);
        System.out.println("context.getUriInfo().getPath() = " + context.getUriInfo().getPath());
        System.out.println("pre context.getUriInfo().getQueryParameters() = " + preQueryParams);
        System.out.println("context.getUriInfo().getQueryParameters() = " + objectWriter.writeValueAsString(context.getUriInfo().getQueryParameters()));
        try {
            System.out.println("request.getMethod() = " + request.getMethod());
        } catch (Exception e) {
            System.out.println("e = ");
            e.printStackTrace();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.info("response = " + json(responseContext));
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

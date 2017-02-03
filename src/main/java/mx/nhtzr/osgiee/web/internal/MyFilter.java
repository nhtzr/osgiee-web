package mx.nhtzr.osgiee.web.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.container.*;
import java.io.IOException;

/**
 * Created by hero on 1/24/2017.
 */
@Component("myFilter")
@PreMatching
public class MyFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Log log = LogFactory.getLog(MyFilter.class);
    private final ObjectWriter objectWriter = new ObjectMapper()
            .disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS)
            .writerWithDefaultPrettyPrinter();

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        log.info("request = " + json(context));
        context.setRequestUri(context
                .getUriInfo()
                .getAbsolutePathBuilder()
                .queryParam("token", "tokenValue")
                .build());
        System.out.println("context.getUriInfo().getPath() = " + context.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.info("response = " + json(responseContext));
    }

    private String json(Object o) throws IOException {
        return objectWriter.writeValueAsString(o);
    }

}

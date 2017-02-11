package mx.nhtzr.osgiee.web;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by hero on 2/9/2017.
 */
@PreMatching
public interface MyFilter extends ContainerRequestFilter, ContainerResponseFilter {


    HttpServletRequest getRequest();

    @Context
    void setRequest(HttpServletRequest request);

}

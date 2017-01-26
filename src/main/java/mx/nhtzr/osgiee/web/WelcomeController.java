/*******************************************************************************
 * Copyright (c) 2014 Jens Reimann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package mx.nhtzr.osgiee.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

@Controller
@Path("/")
public class WelcomeController {

    private static final Log logger = LogFactory.getLog(WelcomeController.class);
    private ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

    @POST
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    public String postHandler(MultivaluedMap<String, String> params ) throws IOException {
        logger.info("params = " + writer.writeValueAsString(params));
        return params.getFirst("body");
    }

    @GET
    public String main() {
        return "Holi";
    }
}

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WelcomeController {

    private static final Log logger = LogFactory.getLog(WelcomeController.class);

    public WelcomeController() {
        logger.info("Holy shit it really is reading from that weird folder");
    }

    @RequestMapping("/")
    @ResponseBody
    public String main() {
        return "Holi";
    }
}

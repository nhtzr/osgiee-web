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

import mx.nhtzr.osgiee.web.internal.Configurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SetupController {

    @Autowired
    private SetupService setupService;

    @RequestMapping(value = "/setup", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> main() {
        final Map<String, Object> model = new HashMap<>();
        try (Configurator cfg = Configurator.create()) {
            model.put("command", cfg.getDatabaseSettings());
        }
        model.put("jdbcDrivers", setupService.getJdbcDrivers());
        return model;
    }


}

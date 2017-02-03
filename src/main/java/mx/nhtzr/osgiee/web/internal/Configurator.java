package mx.nhtzr.osgiee.web.internal;

import mx.nhtzr.osgiee.web.model.SetupData;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

@Service
public class Configurator {
    private static final String GEMINI_FACTORY_PID = "gemini.jpa.punit";

    @Autowired(required = false)
    private ConfigurationAdmin configurationAdmin;


    public SetupData getDatabaseSettings() {
        if (!isAvailable()) {
            return null;
        }

        try {
            final Configuration[] cfgs = configurationAdmin.listConfigurations(String.format("(%s=%s)", "service.factoryPid", GEMINI_FACTORY_PID));
            if (cfgs == null || cfgs.length <= 0) {
                return new SetupData();
            }

            final SetupData result = new SetupData();

            final Dictionary<String, Object> props = cfgs[0].getProperties();

            result.setJdbcDriver(getString(props, "javax.persistence.jdbc.driver"));
            result.setUrl(getString(props, "javax.persistence.jdbc.url"));
            result.setUser(getString(props, "javax.persistence.jdbc.user"));
            result.setPassword(getString(props, "javax.persistence.jdbc.password"));

            final Properties p = new Properties();
            final Enumeration<String> i = props.keys();
            while (i.hasMoreElements()) {
                final String key = i.nextElement();
                if (!key.startsWith("javax.persistence.jdbc.")) {
                    continue;
                }
                p.put(key.substring("javax.persistence.jdbc.".length()), props.get(key));
            }

            final StringWriter sw = new StringWriter();
            p.store(sw, null);
            sw.close();
            result.setAdditionalProperties(sw.getBuffer().toString().replaceAll("^#.*", ""));

            return result;
        } catch (IOException | InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDatabaseSettings(final SetupData data) {
        if (isAvailable()) {
            throw new IllegalStateException(String.format("Configuration Admin not found"));
        }

        try {
            final Configuration[] result = configurationAdmin.listConfigurations(String.format("(%s=%s)", "service.factoryPid", GEMINI_FACTORY_PID));
            // delete old
            if (result != null) {
                for (final Configuration cfg : result) {
                    cfg.delete();
                }
            }

            final Configuration cfg = configurationAdmin.createFactoryConfiguration(GEMINI_FACTORY_PID, null);

            final Dictionary<String, Object> props = new Hashtable<>();

            props.put("gemini.jpa.punit.name", "jpa1");

            props.put("javax.persistence.jdbc.driver", data.getJdbcDriver());
            props.put("javax.persistence.jdbc.url", data.getUrl());
            props.put("javax.persistence.jdbc.user", data.getUser());
            props.put("javax.persistence.jdbc.password", data.getPassword());

            final Properties p = new Properties();
            p.load(new StringReader(data.getAdditionalProperties()));

            for (final Map.Entry<Object, Object> entry : p.entrySet()) {
                props.put("javax.persistence.jdbc." + entry.getKey(), entry.getValue());
            }

            cfg.update(props);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getString(final Dictionary<String, Object> props, final String string) {
        final Object o = props.remove(string);
        if (o instanceof String) {
            return (String) o;
        }

        return null;
    }

    public boolean isAvailable() {
        return configurationAdmin != null;
    }

}

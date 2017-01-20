package mx.nhtzr.osgiee.web;

import mx.nhtzr.osgiee.web.model.JdbcDriverInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;

/**
 * Created by hero on 1/19/2017.
 */
@Component
public class SetupService {

    @Autowired
    private BundleContext context;

    public List<JdbcDriverInformation> getJdbcDrivers() {
        final List<JdbcDriverInformation> result = new ArrayList<>();

        try {
            final Collection<ServiceReference<DataSourceFactory>> refs = context.getServiceReferences(DataSourceFactory.class, null);
            for (final ServiceReference<DataSourceFactory> ref : refs) {
                final String className = getString(ref.getProperty("osgi.jdbc.driver.class"));
                String name = getString(ref.getProperty("osgi.jdbc.driver.name"));
                final String version = getString(ref.getProperty("osgi.jdbc.driver.version"));

                if (className == null) {
                    continue;
                }

                if (name == null) {
                    name = className;
                }

                result.add(new JdbcDriverInformation(className, name, version));
            }
        } catch (final InvalidSyntaxException e) {
        }

        result.sort(comparing(JdbcDriverInformation::getName));

        return result;
    }

    private String getString(final Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }
}

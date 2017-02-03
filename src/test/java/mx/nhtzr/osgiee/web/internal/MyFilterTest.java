package mx.nhtzr.osgiee.web.internal;

import mx.nhtzr.osgiee.web.WelcomeController;
import mx.nhtzr.osgiee.web.internal.MyFilter;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.servlet.ServletConfig;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by hero on 2/2/2017.
 */
@RunWith(Parameterized.class)
public class MyFilterTest {

    public static final String STATUS_OK = "200 OK";
    public static final String APPLICATION_SLASH_RESPONSE = "main()";
    public static final String APPLICATION_NON_SLASH_RESPONSE = "sub()";

    private static LocalConnector localConnector;
    private static Server server;

    @Parameterized.Parameter(value = 0)
    public String request;
    @Parameterized.Parameter(value = 1)
    public String expectedResponse;

    @Parameterized.Parameters
    public static Collection<String[]> parameters() {
        return asList(
                new String[]{"GET /?myQueryParam=myQueryParamValue HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "User-Agent: curl/7.44.0\n" +
                        "Accept: */*\n\n\n", APPLICATION_SLASH_RESPONSE},

                new String[]{"GET / HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "User-Agent: curl/7.44.0\n" +
                        "Accept: */*\n\n\n", APPLICATION_SLASH_RESPONSE},
                new String[]{"GET /sub HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "User-Agent: curl/7.44.0\n" +
                        "Accept: */*\n\n\n", APPLICATION_NON_SLASH_RESPONSE},
                new String[]{"GET /sub?myQueryParam=myQueryParamValue HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "User-Agent: curl/7.44.0\n" +
                        "Accept: */*\n\n\n", APPLICATION_NON_SLASH_RESPONSE});
    }

    @Test
    public void test() throws Exception {
        String response;
        response = localConnector.getResponse(request);
        Assert.assertThat(response, containsString(STATUS_OK));
        Assert.assertThat(response, containsString(expectedResponse));
    }

    @BeforeClass
    public static void setUp() throws Exception {
        final CXFNonSpringServlet servlet = new CXFNonSpringServlet() {
            @Override
            protected void loadBus(ServletConfig sc) {
                super.loadBus(sc);
                final JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();
                bean.setServiceBean(new WelcomeController());
                bean.setProvider(new MyFilter());
                bean.setBus(getBus());
                bean.setAddress("/");
                bean.create();
            }
        };
        final ServletHolder holder = new ServletHolder(servlet);
        holder.setName("service");

        final ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(holder, "/*");

        server = new Server();
        localConnector = new LocalConnector(server);
//        final ServerConnector serverConnector = new ServerConnector(server);
//        serverConnector.setPort(8080);
        server.setConnectors(new Connector[]{
                localConnector
//                ,serverConnector
        });
        server.setHandler(handler);
        server.start();
//        System.out.println(" === CALLING SERVER JOIN === ");
//        server.join();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stop();
        server.destroy();
    }

}

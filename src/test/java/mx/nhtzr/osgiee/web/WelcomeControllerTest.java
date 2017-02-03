package mx.nhtzr.osgiee.web;

import mx.nhtzr.osgiee.web.internal.MyFilter;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletConfig;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by hero on 2/2/2017.
 */
public class WelcomeControllerTest {

    @Test
    public void test() throws Exception {

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

        final Server server = new Server();
        final LocalConnector localConnector = new LocalConnector(server);
//        final ServerConnector localConnector = new ServerConnector(server);
//        localConnector.setPort(8080);
        server.setConnectors(new Connector[]{localConnector});
        server.setHandler(handler);
        server.start();

        try {
            String response;
            response = localConnector.getResponse(
                    "GET /?myQueryParam=myQueryParamValue HTTP/1.1\n" +
                            "Host: localhost:8080\n" +
                            "User-Agent: curl/7.44.0\n" +
                            "Accept: */*\n\n\n");
            Assert.assertThat(response, containsString("200"));
            Assert.assertThat(response, containsString("main()"));

            response = localConnector.getResponse(
                    "GET / HTTP/1.1\n" +
                            "Host: localhost:8080\n" +
                            "User-Agent: curl/7.44.0\n" +
                            "Accept: */*\n\n\n");
            Assert.assertThat(response, containsString("200"));
            Assert.assertThat(response, containsString("main()"));

            response = localConnector.getResponse(
                    "GET /sub?myQueryParam=myQueryParamValue HTTP/1.1\n" +
                            "Host: localhost:8080\n" +
                            "User-Agent: curl/7.44.0\n" +
                            "Accept: */*\n\n\n");
            Assert.assertThat(response, containsString("200"));
            Assert.assertThat(response, containsString("sub()"));

            response = localConnector.getResponse(
                    "GET /sub HTTP/1.1\n" +
                            "Host: localhost:8080\n" +
                            "User-Agent: curl/7.44.0\n" +
                            "Accept: */*\n\n\n");
            Assert.assertThat(response, containsString("200"));
            Assert.assertThat(response, containsString("sub()"));
        } finally {
            server.stop();
            server.destroy();
        }
    }
}

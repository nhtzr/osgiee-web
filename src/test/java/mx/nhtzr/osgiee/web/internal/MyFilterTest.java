package mx.nhtzr.osgiee.web.internal;

import mx.nhtzr.osgiee.web.MyFilter;
import mx.nhtzr.osgiee.web.WelcomeController;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.impl.tl.ThreadLocalHttpServletRequest;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletConfig;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by hero on 2/2/2017.
 */
public class MyFilterTest {

    public static final String STATUS_OK = "200 OK";
    public static final String APPLICATION_SLASH_RESPONSE = "main()";

    private static LocalConnector localConnector;
    private static Server server;
    private static UpdateableFilterProxy handler = new UpdateableFilterProxy();


    @Test
    public void test() throws Exception {
        final MyFilterImpl reloadedFilter = new MyFilterImpl(); // Filter is reloaded by OSGI
        reloadedFilter.setRequest(new ThreadLocalHttpServletRequest()); // New instance receives a new ThreadLocal
        handler.set(reloadedFilter); // This is updated on the fly by OSGI

        String response;
        response = localConnector.getResponse("GET / HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "User-Agent: curl/7.44.0\n" +
                "Accept: */*\n\n\n");
        Assert.assertThat(response, containsString(STATUS_OK));
        Assert.assertThat(response, containsString(APPLICATION_SLASH_RESPONSE));
    }

    @BeforeClass
    public static void setUp() throws Exception {
        final CXFNonSpringServlet servlet = new CXFNonSpringServlet() {
            @Override
            protected void loadBus(ServletConfig sc) {
                super.loadBus(sc);
                final MyFilterImpl provider = new MyFilterImpl();
                final ClassLoader loader = Thread.currentThread().getContextClassLoader();
                final Class[] interfaces = {MyFilter.class};
                final Object proxyProvider = Proxy.newProxyInstance(loader, interfaces, handler.set(provider));
                // This is meant to implement a provider obtained by OSGI
                // OSGI can update implementation on the fly in case a bundle hot deploys

                JAXRSServerFactoryBean bean;
                bean = new JAXRSServerFactoryBean();
                bean.setServiceBean(new WelcomeController());
                bean.setProvider(proxyProvider);
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

    private static class UpdateableFilterProxy implements InvocationHandler {

        private MyFilterImpl provider;

        public UpdateableFilterProxy set(MyFilterImpl provider) {
            this.provider = provider;
            return this;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(provider, args);
        }

    }
}

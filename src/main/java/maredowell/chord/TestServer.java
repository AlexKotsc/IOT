package maredowell.chord;

import maredowell.rest.InitListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

public class TestServer {

    ServletHolder sh;

    public static void main(String[] args) throws Exception {

        TestServer t = new TestServer(Integer.parseInt(args[0]));

       /* ServletHolder sh = new ServletHolder(ServletContainer.class);

        sh.setInitParameter("jersey.config.server.provider.packages", "maredowell.rest");

        ResourceHandler resHandler = new ResourceHandler();
        resHandler.setDirectoriesListed(true);


        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(sh, "/*");
        context.addEventListener(new InitListener());


        server.start();
        server.join();*/
    }

    public TestServer(int port){
        sh = new ServletHolder(ServletContainer.class);

        sh.setInitParameter("jersey.config.server.provider.packages", "maredowell.rest");

        ResourceHandler resHandler = new ResourceHandler();
        resHandler.setDirectoriesListed(true);

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(sh, "/*");
        context.addEventListener(new InitListener());

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Servlet getServlet(){
        try {
            return sh.getServlet();
        } catch (ServletException e) {
            e.printStackTrace();
        }

        return null;
    }
}
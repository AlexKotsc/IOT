package maredowell.rest;


import maredowell.chord.Node;
import maredowell.impl.NodeImpl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by AlexKotsc on 22-02-2015.
 */
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing...");

        //Create NodeImpl object, and initialize
        Node n = new NodeImpl();
        boolean created = false;

        sce.getServletContext().setAttribute("node",n);
        sce.getServletContext().setAttribute("created", created);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Node destroyed");
    }
}

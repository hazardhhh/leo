package com.hhh.server.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @Description Log4jConfigListener
 * @Author HHH
 * @Date 2023/5/27 2:55
 */
public class Log4jConfigListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        Log4jConfig.initLogging(event.getServletContext());
    }

    public void contextDestroyed(ServletContextEvent event) {
        Log4jConfig.shutdownLogging(event.getServletContext());
    }

}

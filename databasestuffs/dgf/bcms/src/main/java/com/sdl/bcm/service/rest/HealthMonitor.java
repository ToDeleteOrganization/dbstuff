package com.sdl.bcm.service.rest;

import com.sdl.bcm.service.monitoring.Status;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HealthMonitor {
    private static final Logger LOG = Logger.getLogger(HealthMonitor.class);

    @Autowired
    private MongoDbFactory mongoDbFactory;

    /**
     * Return the status of the mongo db.
     * 
     * @return
     */
    @RequestMapping(value = "/hsummary/health", method = RequestMethod.GET)
    @ResponseBody
    public Status getDBStatus() {
        try {
            mongoDbFactory.getDb().getStats();
            return Status.UP;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Status.DOWN;
        }
    }

    /**
     * Checks if the BCMS is up and running.
     * 
     * @return
     */
    @RequestMapping(value = "/ping/health", method = RequestMethod.GET)
    @ResponseBody
    public Status pingApp() {
        return Status.UP;
    }

    /**
     * Check if all BCMs dependencies are up and running;
     * @return
     */
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    @ResponseBody
    public Status generalPing() {
    	boolean isDown = Status.DOWN.equals(getDBStatus());
    	isDown |= Status.DOWN.equals(pingApp());

        return isDown ? Status.DOWN : Status.UP;
    }

}

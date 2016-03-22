package com.sdl.bcm.service.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdl.bcm.rest.RestEndPoint;


/**
 * Abstract controller inherited by all controllers that provides general purpose methods.
 *
 * @author alex
 * @since May 2, 2012
 */
public abstract class AbstractController {
    private static final Logger LOG = Logger.getLogger(AbstractController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Object getDescription() {
    	LOG.info("Returning APIs description.");

    	List<RestEndPoint> retVal = new ArrayList<>();
        Method[] methods = this.getClass().getMethods();

        RequestMapping controllerMapping = this.getClass().getAnnotation(RequestMapping.class);

        for (Method method : methods) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                retVal.add(new RestEndPoint(requestMapping, controllerMapping));
            }
        }

        return retVal;
    }

}

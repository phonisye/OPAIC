package nz.co.spark.intg.ms.devops.config;

import java.text.MessageFormat;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.GenericBeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import nz.co.spark.intg.ms.devops.manage.route.DynamicRouteBuilder;

import static nz.co.spark.intg.ms.devops.constants.DevOpsConstants.*;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    
    @Autowired
    CamelContext camelContext;
    
    @Autowired
    SchedulerJobsLookup schedulerJobsLookup;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) 
    {	
	
	if (!schedulerJobsLookup.isEmpty())
	{
	    schedulerJobsLookup.getJobs().forEach(job -> {
			if (!job.isValid())
			{			    
			    throw new GenericBeansException(ERR_JOB_CONFIG_MISSING);
			}
			    
			try {
			    camelContext.addRoutes(new DynamicRouteBuilder(camelContext, job));
			} catch (Exception e) {
			    String errorMsg= MessageFormat.format(ERR_UNABLE_TO_CREATE_ROUTE, job.getName());
			    throw new GenericBeansException(errorMsg, e);			    
			}
		});
	}	
    }
}

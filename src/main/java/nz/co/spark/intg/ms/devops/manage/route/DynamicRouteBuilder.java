package nz.co.spark.intg.ms.devops.manage.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import nz.co.spark.intg.ms.devops.config.SchedulerJob;
import nz.co.spark.intg.ms.devops.manage.errqueue.bean.DevOpsLogger;

import static nz.co.spark.intg.ms.devops.constants.DevOpsConstants.*;


public class DynamicRouteBuilder extends RouteBuilder {

	@Autowired
	DevOpsLogger devOpsLogger;

    private SchedulerJob schedulerJob;
    
    public DynamicRouteBuilder(CamelContext context, SchedulerJob schedulerJob) {
        super(context);
        this.schedulerJob = schedulerJob;       
    }

    @Override
    public void configure() throws Exception {
		
		onException(Throwable.class).id(GLOBAL_EXCP).handled(true)
		.to("bean:schedulerProcessor?method=processServiceFaultAsync")	
		.end();
		
		fromF("wmq:queue:%s?disableReplyTo=true", schedulerJob.getErrQueue())
		.routeId(schedulerJob.getCamelRouteName())	
		.autoStartup(false)
		.bean("devOpsLogger", "logMessageHandled")	
		.toF("wmq:queue:%s?disableReplyTo=true", schedulerJob.getBusQueue());

		fromF("quartz2://%s/%s?cron=%s", OP_GENERIC_SCHEDULER, schedulerJob.getName(), schedulerJob.getCron())
		.setHeader("RouteID", simple(schedulerJob.getCamelRouteName()))
		.bean("devOpsProcessor", "startCamelRoute");

    }
}

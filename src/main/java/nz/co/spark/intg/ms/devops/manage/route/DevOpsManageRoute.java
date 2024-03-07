package nz.co.spark.intg.ms.devops.manage.route;

import javax.ws.rs.core.MediaType;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfComponent;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import nz.co.spark.intg.ms.devops.manage.errqueue.bean.DevOpsProcessor;
import nz.co.spark.intg.ms.devops.manage.errqueue.bean.DevOpsFaultProcessor;
import nz.co.spark.intg.ms.devops.manage.errqueue.bean.DevOpsLogger;

@Component
public class DevOpsManageRoute extends RouteBuilder {
	final CxfComponent cxfComponent = new CxfComponent(getContext());

	@Autowired
	DevOpsLogger devOpsLogger;

	@Autowired
	DevOpsProcessor devOpsProcessor;

	@Value("${devops.service.endpoint}")
	private String devopsServiceEndpoint;

	@Autowired
	DevOpsFaultProcessor devOpsFaultProcessor;

	@Override
	public void configure() throws Exception {

		getContext().setStreamCaching(true);
		getContext().setUseMDCLogging(true);
        restConfiguration()
        .component("jetty")
        .scheme("{{common.http.protocol}}")
        .bindingMode(RestBindingMode.json)
        .skipBindingOnErrorCode(false)
        .enableCORS(true)
        .dataFormatProperty("prettyPrint", "true")
        .host("{{common.http.host}}")
        .port("{{common.http.port}}")

        // Swagger Documentation
        .apiContextPath("/")
        .apiContextRouteId("swagger")
        // Swagger Documentation
        .apiContextPath("/").apiContextRouteId("swagger")
        .apiProperty("api.title", "Spark Value Added Service").apiProperty("api.version", "1.0.0")
        .apiProperty("api.description", "API definition for FUSE intg-devops-v1")
        .apiProperty("api.termsOfService", "http://www.spark.co.nz/help/other/terms/")
        .apiProperty("api.contact.name", "Spark Integration Chapter")
        .apiProperty("api.contact.email", "apisupport@spark.co.nz")		
        .apiProperty("api.license.name", "Spark New Zealand")
        .apiProperty("api.license.url", "http://www.spark.co.nz");
		

		onException(Throwable.class).id("DEVOPS_GLOBAL_EXCP").handled(true)
		.bean(devOpsFaultProcessor, "processServiceFault").end();

		
		rest().tag("DevOps Main").consumes(MediaType.APPLICATION_JSON).produces(MediaType.APPLICATION_JSON)			
			.post(devopsServiceEndpoint.trim())

		.route().routeId("DEVOPSMAIN")
			.bean("devOpsProcessor", "testStopCamelFromRoute")
			.endRest();

		from("wmq:queue:{{ibm.mq.qname.devops.errqueue.error}}?disableReplyTo=true")
			.autoStartup("false")
			.routeId("DEVOPS_ERRQUEUE_ROUTE_001")
			.bean("devOpsLogger", "logMessageHandled")	
			.to("wmq:{{ibm.mq.qname.devops.errqueue}}?disableReplyTo=true")
		.end();
		
		from("quartz2://DevOps.Scheduler/ReprocessErrorMsg?cron=0+0/10+*+*+*+?")
			.bean("devOpsProcessor", "testStopCamelFromRoute");

	}
}

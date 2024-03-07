package nz.co.spark.intg.ms.devops.manage.errqueue.bean;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;

import org.springframework.stereotype.Component;

@Component
public class DevOpsProcessor {

	public void testStopCamelFromRoute(Exchange exchange) throws Exception {
        CamelContext context = exchange.getContext(); 

        context.startRoute("DEVOPS_ERRQUEUE_ROUTE_001");

        Thread.sleep(10000);

        context.stopRoute("DEVOPS_ERRQUEUE_ROUTE_001");
	}

	public void startCamelRoute(Exchange exchange) throws Exception {
        CamelContext context = exchange.getContext(); 

        String routeId = exchange.getIn().getHeader("RouteID", String.class);
        context.startRoute(routeId);

        Thread.sleep(30000);

        context.stopRoute(routeId);
	}

}

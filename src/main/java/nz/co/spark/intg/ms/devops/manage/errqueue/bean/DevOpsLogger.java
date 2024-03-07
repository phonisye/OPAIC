package nz.co.spark.intg.ms.devops.manage.errqueue.bean;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.util.Json;
import nz.co.spark.intg.ms.logger.MSLogger;
import nz.co.spark.intg.ms.logger.ProvidersEnum;

import static nz.co.spark.intg.ms.devops.constants.DevOpsConstants.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DevOpsLogger {

	/** The Constant LOGGER. */
	private static final MSLogger LOGGER = MSLogger.getLogger();

	public void logRouteStart(String operationName, String routeName) {
		LOGGER.logRouteStart(operationName, routeName);
	}

	public void logRouteEnd(String operationName, String routeName) {
		LOGGER.logRouteEnd(operationName, routeName);
	}

	public void logReceive(Exchange exchange) {
		LOGGER.logReceive(PROP_OPERATION, assembleLogMessage(exchange, true));
	}

	public void logMessageHandled(Exchange exchange) {
		LOGGER.logMessage(DEVOPS_MANAGE_ROUTE + " " + exchange.getIn().getBody());
	}

	private String assembleLogMessage(Exchange exchange, boolean logMethod) {
		if (logMethod) {
			return "httpMethod=" + exchange.getIn().getHeader(Exchange.HTTP_METHOD) + " headers= " + filterHeaders(exchange.getIn().getHeaders())
					+ ((exchange.getIn().getBody() != null) ? " body=" + exchange.getIn().getBody() : "");
		} else {
			return "headers= " + filterHeaders(exchange.getIn().getHeaders()) + ((exchange.getIn().getBody() != null) ? " body=" + exchange.getIn().getBody() : "");
		}
	}

	public void logReply(Exchange exchange) {
		LOGGER.logReply("PROP_OPERATION", assembleLogMessage(exchange, false));

	}

	public void logFault(String operation, Object object) {
		LOGGER.logFault(operation, object);
	}

	public void logError(String operation, String faultMessage, Object object) {
		LOGGER.logError(operation, faultMessage, object);
	}

	public void logProviderError(String operation, ProvidersEnum provider, String providerOperation, Object object) {
		LOGGER.logProviderError(operation, provider, providerOperation, object);
	}

	public void logProviderTimeout(String operation, ProvidersEnum provider, String providerOperation, Object object) {
		LOGGER.logProviderTimeout(operation, provider, providerOperation, object);
	}

	public void logProviderFault(String operation, ProvidersEnum provider, String providerOperation, Object object) {
		LOGGER.logProviderFault(operation, provider, providerOperation, object);
	}

	public void logGlobalError(String operation, String message, Object object) {
		LOGGER.logError(operation, message, object);
	}

	public static Map<String, Object> filterHeaders(Map<String, Object> inHeaders) {
		Map<String, Object> outHeaders = new HashMap<>();
		List<String> headerList = Arrays.asList("application", "timestamp", "transactionid", "camelhttpquery", "camelhttpuri",
				Exchange.HTTP_RESPONSE_CODE.toLowerCase(), Exchange.HTTP_RESPONSE_TEXT.toLowerCase());

		Set<String> keySet = inHeaders.keySet();
		for (Iterator<String> mapItr = keySet.iterator(); mapItr.hasNext();) {
			String key = mapItr.next();
			if (headerList.contains(key.toLowerCase())) {
				outHeaders.put(key, inHeaders.get(key));
			}
		}
		return outHeaders;
	}

	public static Map<String, Object> filterRequestHeaders(Map<String, Object> inHeaders) {
		Map<String, Object> outHeaders = new HashMap<>();
		List<String> headerList = Arrays.asList("application", "timestamp", "transactionid", "camelhttpquery", "camelhttpuri");

		Set<String> keySet = inHeaders.keySet();
		for (Iterator<String> mapItr = keySet.iterator(); mapItr.hasNext();) {
			String key = mapItr.next();
			if (headerList.contains(key.toLowerCase())) {
				outHeaders.put(key, inHeaders.get(key));
			}
		}
		return outHeaders;
	}

	public void logProviderRequest(Exchange exchange) throws JsonProcessingException {
		LOGGER.logProviderRequest(PROP_OPERATION, ProvidersEnum.MQ_ABS,
				"PROP_PROVIDER_OPERATION", constructLogMessage(filterRequestHeaders(exchange.getIn().getHeaders()), convertToJsonString(exchange.getIn().getBody())));
	}

	public void logProviderResponse(Exchange exchange) throws JsonProcessingException {
		LOGGER.logProviderResponse(PROP_OPERATION, ProvidersEnum.MQ_ABS,
				"PROP_PROVIDER_OPERATION", constructLogMessage(filterHeaders(exchange.getIn().getHeaders()), convertToJsonString(exchange.getIn().getBody())));
	}

	private String constructLogMessage(Map<String, Object> headers, String body) {
		return StringUtils.join("headers=" + StringUtils.join(headers, ", "), "body=" + body);
	}

	private String convertToJsonString(Object body) throws JsonProcessingException {
		return Json.mapper().writeValueAsString(body);
	}

}

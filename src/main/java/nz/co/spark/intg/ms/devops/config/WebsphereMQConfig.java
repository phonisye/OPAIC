package nz.co.spark.intg.ms.devops.config;

import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.camel.component.jms.JmsComponent;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import nz.co.spark.intg.ms.logger.MSLogger;

/**
 * In dev mode we deploy on Tomcat and connect to WebSphere MQ directly
 *
 */
@Configuration
public class WebsphereMQConfig {
	
	private static final MSLogger logger = MSLogger.getLogger();
	
	/** Websphere MQ broker Connection Name List . */
	@Value("${ibm.mq.connName}")
	private String connectionName;
	
	/** Websphere MQ user name . */
	@Value("${ibm.mq.channel}")
	private String svrChannel;
	
	/** Websphere MQ password . */
	@Value("${ibm.mq.queueManager}")
	private String queueManager;

	/**
	 * Creates a connection to the  queue manager
	 * @return
	 * @throws JMSException
	 */	
	@Bean(name="wmq")
	public JmsComponent jmsComponent1() {
		MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
		JmsComponent jmsComponent = new JmsComponent();
		try
		{
			connectionFactory.setConnectionNameList(connectionName);
			connectionFactory.setChannel(svrChannel);
			connectionFactory.setQueueManager(queueManager);
			connectionFactory.setTransportType(1);		
			jmsComponent.setConnectionFactory(connectionFactory);	
			jmsComponent.setTransacted(true);
		}
		catch(JMSException ex)
		{
			logger.error("Exception occurred in JMSConnection ", ex);
		}
		return jmsComponent;
	}

}
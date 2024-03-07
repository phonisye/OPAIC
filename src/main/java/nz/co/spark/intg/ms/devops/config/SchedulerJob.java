package nz.co.spark.intg.ms.devops.config;

import org.apache.commons.lang3.StringUtils;

public class SchedulerJob {

    private String name;
    private String camelRouteName;
    private String cron;
    private String busQueue;
    private String errQueue;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCron() {
        return cron;
    }
    
    public void setCron(String cron) {
        this.cron = cron;
    }
    
    public String getBusQueue() {
        return busQueue;
    }
    
    public void setBusQueue(String busQueue) {
        this.busQueue = busQueue;
    }
    
    public String getErrQueue() {
        return errQueue;
    }
    
    public void setErrQueue(String errQueue) {
        this.errQueue = errQueue;
    }

    public String getCamelRouteName() {
	if (StringUtils.isBlank(camelRouteName))
	    camelRouteName = name+"-Route";
        return camelRouteName;
    }

    public void setCamelRouteName(String camelRouteName) {
        this.camelRouteName = camelRouteName;
    }
    
    public boolean isValid()
    {
	return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(cron) && StringUtils.isNotBlank(busQueue) && StringUtils.isNotBlank(errQueue);	
    }
    
}

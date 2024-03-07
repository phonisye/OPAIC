package nz.co.spark.intg.ms.devops.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "devops.errqueue")
@Component
public class SchedulerJobsLookup {
    
	private List<SchedulerJob> jobs;

	public List<SchedulerJob> getJobs() {
		return jobs;
	}

	public void setJobs(List<SchedulerJob> jobs) {
		this.jobs = jobs;
	}
	
	public boolean isEmpty()
	{
	    if (jobs!=null)
		return jobs.isEmpty();
	    else
		return true;
	}
}

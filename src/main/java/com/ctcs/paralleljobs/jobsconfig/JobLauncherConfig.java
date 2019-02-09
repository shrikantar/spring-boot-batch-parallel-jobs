package com.ctcs.paralleljobs.jobsconfig;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class JobLauncherConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(JobLauncherConfig.class);
	@Autowired
    JobLauncher jobLauncher;
	
	@Autowired
	@Qualifier("Job1")
	Job productRatingJob;

	@Autowired
	@Qualifier("Job2")
	Job invoiceProcessingJob;

	@Scheduled(fixedDelay = 2000000)
	public void lanchJobs() {
		Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(maps);

        try {
			LOGGER.info("About to launch job....");
			JobExecution productRatingJobExecution = jobLauncher.run(productRatingJob, parameters);
			LOGGER.info("product rating job execution completed, status : {} " , productRatingJobExecution.getExitStatus());

			JobExecution invoiceProcessingJobExecution = jobLauncher.run(invoiceProcessingJob, parameters);
			LOGGER.info("invoice processing job execution completed, status : {} " , invoiceProcessingJobExecution.getExitStatus());

		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOGGER.error("Error message : {} " ,  e.getMessage());
		}

	}

}

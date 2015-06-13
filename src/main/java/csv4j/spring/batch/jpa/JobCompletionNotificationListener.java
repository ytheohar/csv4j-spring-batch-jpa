package csv4j.spring.batch.jpa;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import csv4j.spring.batch.jpa.domain.DomainType;

@Component
public class JobCompletionNotificationListener extends
		JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory
			.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			List<DomainType> results = jdbcTemplate.query(
					"SELECT field0, att1, att2 FROM csvdata",
					(rs, row) -> DomainType.of(rs.getInt(1), rs.getString(2),
							rs.getDouble(3)));

			results.stream().forEach(
					r -> log.info("Found <{}> in the database.", r));
		}
	}
}

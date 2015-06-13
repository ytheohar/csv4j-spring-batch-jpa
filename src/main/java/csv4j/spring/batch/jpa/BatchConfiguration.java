package csv4j.spring.batch.jpa;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import csv4j.Hydrator;
import csv4j.spring.batch.jpa.domain.DomainType;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	private static final String DATA = "data";

	@Bean
	public ItemReader<DomainType> reader() {
		Hydrator<DomainType> hydrator = Hydrator.of(DomainType.class);
		String dataPath = this.getClass().getClassLoader().getResource(DATA)
				.getFile();
		File dataDirAsFile = new File(dataPath);
		List<File> files = Arrays.asList(dataDirAsFile.listFiles());
		Iterator<DomainType> iterator = files.stream()
				.flatMap(f -> hydrator.fromCSV(f.toPath()).stream()).iterator();
		return new IteratorItemReader<DomainType>(iterator);
	}

	@Bean
	public ItemWriter<DomainType> writer() throws Exception {
		JpaItemWriter<DomainType> writer = new JpaItemWriter<DomainType>();
		EntityManagerFactory emf = entityManagerFactory(dataSource())
				.getObject();
		writer.setEntityManagerFactory(emf);
		writer.afterPropertiesSet();
		return writer;
	}

	@Bean
	public Job importUserJob(JobBuilderFactory jobs, Step s1,
			JobExecutionListener listener) {
		return jobs.get("importCSVDataJob").incrementer(new RunIdIncrementer())
				.listener(listener).flow(s1).end().build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory,
			ItemReader<DomainType> reader, ItemWriter<DomainType> writer) {
		return stepBuilderFactory.get("step1")
				.<DomainType, DomainType> chunk(10).reader(reader)
				.writer(writer).build();
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
				.build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource dataSource) {
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("csv4j.spring.batch.jpa.domain");
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setPersistenceUnitName("csv4jdomain");
		em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		em.afterPropertiesSet();
		return em;
	}

}

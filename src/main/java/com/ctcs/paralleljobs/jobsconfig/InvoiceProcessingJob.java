package com.ctcs.paralleljobs.jobsconfig;

import com.ctcs.paralleljobs.model.Invoice;
import com.ctcs.paralleljobs.processors.InvoiceProcessor;
import com.ctcs.paralleljobs.model.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.ResultSet;

@Configuration
@EnableBatchProcessing
public class InvoiceProcessingJob {
	
	@Bean("Job2")
	public  Job invoiceJob(
			JobBuilderFactory jobBuilderFactory,
			StepBuilderFactory stepBuilderFactory,
            @Qualifier("invoiceReader")ItemReader<Product> itemReader,
            @Qualifier("invoiceProcessor") ItemProcessor<Product, Invoice> itemProcessor,
            @Qualifier("invoiceWriter")ItemWriter<Invoice> itemWriter) {
		
		Step productRating = stepBuilderFactory.get("invoicingJobStep")
				.<Product, Invoice>chunk(5)
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.build();
		
		return jobBuilderFactory.get("invoiceRatingBuilder")
				.incrementer(new RunIdIncrementer())
				.start(productRating)
				.build();
	}
	
	@Bean("invoiceReader")
	public ItemReader<Product> reader(DataSource dataSource) {
		JdbcCursorItemReader<Product> reader = new JdbcCursorItemReader<>();
		reader.setFetchSize(5);
		reader.setDataSource(dataSource);
		reader.setSql("SELECT ID, NAME  FROM PRODUCT");
		reader.setRowMapper((ResultSet rs, int rowNumber) -> {
			if (!(rs.isAfterLast()) && !(rs.isBeforeFirst())) {
				Product product = new Product();
				product.setId(rs.getLong("ID"));
				product.setName(rs.getString("NAME"));
				return product;
			} else {
				return null;
			}
		});

		return reader;
	}
	
	@Bean("invoiceProcessor")
	public ItemProcessor<Product, Invoice> processing(){
		return new InvoiceProcessor();
	}
	
	@Bean("invoiceWriter")
	public ItemWriter<Invoice> writer(DataSource dataSource, ItemPreparedStatementSetter<Invoice> invoiceSetter) {
		JdbcBatchItemWriter<Invoice> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.setItemPreparedStatementSetter(invoiceSetter);
		writer.setSql("INSERT INTO INVOICES(INVOICE_ID, INVOICE_DATE) VALUES (?, ?)");
		writer.setDataSource(dataSource);
		return writer;
	}
	
	@Bean
	public ItemPreparedStatementSetter<Invoice> invoiceSetter() {
		return (invoice, ps) -> {
			ps.setLong(1, invoice.getId());
			ps.setTimestamp(2, invoice.getInvoiceDate());
		};
	}
}

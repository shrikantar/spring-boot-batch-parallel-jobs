package com.ctcs.paralleljobs.jobsconfig;

import java.sql.ResultSet;

import javax.sql.DataSource;

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

import com.ctcs.paralleljobs.model.Product;
import com.ctcs.paralleljobs.model.ProductRating;
import com.ctcs.paralleljobs.processors.ProductProcessor;

@Configuration
@EnableBatchProcessing
public class ProductProcessingJob {
	
	@Bean("Job1")
	public Job productRatingJob(
			JobBuilderFactory jobBuilderFactory,
			StepBuilderFactory stepBuilderFactory,
            @Qualifier("productReader")ItemReader<Product> itemReader,
            @Qualifier("productProcessor") ItemProcessor<Product, ProductRating> itemProcessor,
            @Qualifier("productRatingWriter")ItemWriter<ProductRating> itemWriter
			) {
		
		Step productRating = stepBuilderFactory.get("productRatingJobStep")
				.<Product, ProductRating>chunk(5)
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.build();
		
		return jobBuilderFactory.get("productRatingBuilder")
				.incrementer(new RunIdIncrementer())
				.start(productRating)
				.build();
	}
	
	@Bean("productReader")
	public ItemReader<Product> reader(DataSource dataSource) {
		JdbcCursorItemReader<Product> reader = new JdbcCursorItemReader<>();
		reader.setFetchSize(5);
		reader.setDataSource(dataSource);
		reader.setSql("SELECT ID, NAME, ORDER_COUNT FROM PRODUCT");
		reader.setRowMapper((ResultSet rs, int rowNumber) -> {
			if (!(rs.isAfterLast()) && !(rs.isBeforeFirst())) {
				Product product = new Product();
				product.setId(rs.getLong("ID"));
				product.setName(rs.getString("NAME"));
				product.setOrderCount(rs.getLong("ORDER_COUNT"));
				return product;
			} else {
				return null;
			}
		});
		return reader;
	}
	
	@Bean("productProcessor")
	public ItemProcessor<Product, ProductRating> processing(){
		return new ProductProcessor();
	}
	
	@Bean("productRatingWriter")
	public ItemWriter<ProductRating> writer(DataSource dataSource, ItemPreparedStatementSetter<ProductRating> productSetter) {
		JdbcBatchItemWriter<ProductRating> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.setItemPreparedStatementSetter(productSetter);
		writer.setSql("INSERT INTO PRODUCT_RATINGS(PRODUCT_ID, RATING) VALUES (?, ?)");
		writer.setDataSource(dataSource);
		return writer;
	}
	
	@Bean
	public ItemPreparedStatementSetter<ProductRating> productSetter() {
		return (product, ps) -> {
			ps.setLong(1, product.getId());
			ps.setString(2, product.getRating());
		};
	}
}

package com.ctcs.paralleljobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication
public class ParallelJobsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParallelJobsApplication.class, args);
	}

}

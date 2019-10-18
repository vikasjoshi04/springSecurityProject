package com.springSecurity;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.springSecurity.constant.SqlQueries;
import com.springSecurity.repository.CountryRepository;

//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@SpringBootApplication
public class SpringSecurityApplication implements CommandLineRunner {
	
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CountryRepository countryRepository;
	
	private static final Logger LOGGER = Logger.getLogger(SpringSecurityApplication.class.getName());
	
	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		String methodName = new Throwable().getStackTrace()[0].getMethodName();
		
		// inserting data in country table if table is empty
		if (countryRepository.count() == 0) {
			LOGGER.info("Inserting data in country table...");
			Query query = entityManager.createNativeQuery(SqlQueries.INSERT_DATA_IN_COUNTRY);
			query.executeUpdate();
			LOGGER.info("Data inseted in country table successfully...");
		} else {
			LOGGER.info("Data alrady exist in country table...");
		}

	}

}

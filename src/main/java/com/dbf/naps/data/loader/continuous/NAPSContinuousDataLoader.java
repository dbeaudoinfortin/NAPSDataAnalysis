package com.dbf.naps.data.loader.continuous;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.NAPSActionBase;
import com.dbf.naps.data.loader.LoadOptions;
import com.zaxxer.hikari.HikariDataSource;

public class NAPSContinuousDataLoader extends NAPSActionBase {

	private static final Logger log = LoggerFactory.getLogger(NAPSContinuousDataLoader.class);
	
	private static LoadOptions CONFIG = null;
	private static HikariDataSource dbDataSource;
	private static SqlSessionFactory sqlSessionFactory;
	
	public static void main(String[] args) {
		log.info("Welcome! 🙂");
		
		try
		{
			initConfig(args);
			NAPSActionBase.init(CONFIG);
			initDB();
			initThreadPool(CONFIG.getThreadCount());
			loadContinousFiles();
			
		} catch (Throwable t) {
			log.error("Unexpected failure.", t);
		}
		
		log.info("Goodbye! 🙂");
	}
	
	private static void initConfig(String[] args) {
		try {
			CONFIG = new LoadOptions(args);
		} catch (IllegalArgumentException e) {
			log.error("Error reading command line options: ", e);
			log.info("Command line usage:\n" + LoadOptions.printOptions());
			System.exit(0);
		}
	}
	
	private static void initDB() throws IOException, SQLException {
		dbDataSource = new HikariDataSource();
		dbDataSource.setUsername(CONFIG.getDbUser());
		dbDataSource.setPassword(CONFIG.getDbPass());
		dbDataSource.setSchema(CONFIG.getDbName());
		dbDataSource.setDriverClassName("org.postgresql.Driver");
		dbDataSource.setMaximumPoolSize(CONFIG.getThreadCount() + 1);
		dbDataSource.setMinimumIdle(CONFIG.getThreadCount());
		dbDataSource.setJdbcUrl("jdbc:postgresql://" + CONFIG.getDbHost() + ":" + CONFIG.getDbPort() + "/");
		dbDataSource.setAutoCommit(true);
		
		JdbcTransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("local", transactionFactory, dbDataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(ContinuousDataMapper.class);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		try (Reader reader = Resources.getResourceAsReader(NAPSContinuousDataLoader.class.getClassLoader(),"schema/schema.sql")) {
			ScriptRunner scriptRunner = new ScriptRunner(dbDataSource.getConnection());
			scriptRunner.runScript(reader);
		}
	}
	
	private static void loadContinousFiles() throws IOException {

		List<Future<?>> futures = new ArrayList<Future<?>>();
		final Path rawPath = CONFIG.getDataPath();
		
		if(!rawPath.toFile().isDirectory()) {
			log.error("The path to the raw data is not valid: " + rawPath);
			return;
		}
		
		Files.list(rawPath).forEach(path -> {
			File csvFile = path.toFile();
			if (!csvFile.isFile()) return;
			if(!csvFile.getName().toLowerCase().endsWith(".csv")) return;
			futures.add(submitTask(new ContinuousFileLoader(getThreadID(), CONFIG, sqlSessionFactory, csvFile)));
		});
		
		waitForTaskCompletion(futures);
	}

}

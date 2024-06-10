package com.dbf.naps.data.loader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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
import com.zaxxer.hikari.HikariDataSource;

public abstract class NAPSDataLoader extends NAPSActionBase<LoaderOptions> {

	private static final Logger log = LoggerFactory.getLogger(NAPSDataLoader.class);
	
	private HikariDataSource dbDataSource;
	private SqlSessionFactory sqlSessionFactory;
	
	public NAPSDataLoader(String[] args) {
		super(args);
	}
	
	protected void run() {
		log.info("Welcome! 🙂");
		
		try
		{
			initDB();
			loadFiles();
		} catch (Throwable t) {
			log.error("Unexpected failure.", t);
		}
		
		log.info("Goodbye! 🙂");
		System.exit(0);
	}
	
	private void initDB() throws IOException, SQLException {
		dbDataSource = new HikariDataSource();
		dbDataSource.setUsername(getOptions().getDbUser());
		dbDataSource.setPassword(getOptions().getDbPass());
		dbDataSource.setSchema(getOptions().getDbName());
		dbDataSource.setDriverClassName("org.postgresql.Driver");
		dbDataSource.setMaximumPoolSize(getOptions().getThreadCount() + 1);
		dbDataSource.setMinimumIdle(getOptions().getThreadCount());
		dbDataSource.setJdbcUrl("jdbc:postgresql://" + getOptions().getDbHost() + ":" + getOptions().getDbPort() + "/");
		dbDataSource.setAutoCommit(true);
		
		JdbcTransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("local", transactionFactory, dbDataSource);
		
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(DataMapper.class);
		
		for(Class<?> clazz :  getDBMappers()) {
			configuration.addMapper(clazz);
		}
	
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		try (Reader reader = Resources.getResourceAsReader(NAPSDataLoader.class.getClassLoader(),"schema/schema.sql")) {
			ScriptRunner scriptRunner = new ScriptRunner(dbDataSource.getConnection());
			scriptRunner.runScript(reader);
		}
	}
	
	protected abstract List<Class<?>> getDBMappers();
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	private void loadFiles() throws IOException {
		List<Future<?>> futures = new ArrayList<Future<?>>();
		final Path rawPath = getOptions().getDataPath();
		
		log.info("Examining all files at the path " + rawPath);

		if(!rawPath.toFile().isDirectory()) {
			log.error("The path to the raw data is not valid: " + rawPath);
			return;
		}
		
		recurseDir(rawPath, futures);
		log.info("All files have been examined. " + futures.size() + " task(s) have been created. Waiting for completion...");
		
		waitForTaskCompletion(futures);
	}
	
	private void recurseDir(final Path dirPath, Collection<Future<?>> futures) throws IOException {
		Files.list(dirPath).forEach(path -> {
			File dataFile = path.toFile();
			if (dataFile.isDirectory()) {
				try {
					recurseDir(path, futures);
				} catch (IOException e) {
					log.error("Failed to read directory: " + path, e);
					return;	//Don't prevent other files from processing
				}
			} else if (dataFile.isFile()) {
				try {
					Collection<Runnable> tasks = processFile(dataFile);
					if(null != tasks) futures.addAll(submitTasks(tasks));
				} catch (Exception e) {
					log.error("Failed to queue up task for file: " + dataFile, e);
					return; //Don't prevent other files from processing
				}
			}//ignore symbolic links
		});
	}
	
	public Class<LoaderOptions> getOptionsClass(){
		return LoaderOptions.class;
	}
	
	protected abstract Collection<Runnable> processFile(File dataFile);
}

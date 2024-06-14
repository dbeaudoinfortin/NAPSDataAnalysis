package com.dbf.naps.data.db;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;
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
import com.dbf.naps.data.db.mappers.MethodMapper;
import com.dbf.naps.data.db.mappers.PollutantMapper;
import com.dbf.naps.data.db.mappers.SiteMapper;
import com.zaxxer.hikari.HikariDataSource;

public abstract class NAPSDBAction<O extends DBOptions> extends NAPSActionBase<O> {

	private static final Logger log = LoggerFactory.getLogger(NAPSDBAction.class);
	
	private HikariDataSource dbDataSource;
	private SqlSessionFactory sqlSessionFactory;
	
	public NAPSDBAction(String[] args) {
		super(args);
	}
	
	protected void run() {
		try
		{
			initDB();
		} catch (Throwable t) {
			log.error("Unexpected failure initializing the DB.", t);
			throw new RuntimeException(t);
		}
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
		configuration.addMapper(PollutantMapper.class);
		configuration.addMapper(MethodMapper.class);
		configuration.addMapper(SiteMapper.class);
		
		for(Class<?> clazz :  getDBMappers()) {
			configuration.addMapper(clazz);
		}
	
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		try (Reader reader = Resources.getResourceAsReader(NAPSDBAction.class.getClassLoader(),"schema/schema.sql")) {
			ScriptRunner scriptRunner = new ScriptRunner(dbDataSource.getConnection());
			scriptRunner.runScript(reader);
		}
	}
	
	protected abstract List<Class<?>> getDBMappers();
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
}

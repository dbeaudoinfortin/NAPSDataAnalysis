package com.dbf.naps.data.db;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.BaseRunner;

public abstract class DBRunner<O extends DBOptions> extends BaseRunner<O> {
	
	private final SqlSessionFactory sqlSessionFactory;
	
	public DBRunner(int threadId, O config, SqlSessionFactory sqlSessionFactory) {
		super(threadId, config);
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
}

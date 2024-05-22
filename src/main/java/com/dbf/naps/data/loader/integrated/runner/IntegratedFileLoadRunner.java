package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.FileLoadRunner;
import com.dbf.naps.data.loader.LoaderOptions;

public abstract class IntegratedFileLoadRunner extends FileLoadRunner {

	public IntegratedFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}
}

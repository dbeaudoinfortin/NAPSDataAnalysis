package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;

public class HCBFileLoadRunner extends PAHFileLoadRunner {
	//HCB is identical to PAH in logic
	public HCBFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}
}

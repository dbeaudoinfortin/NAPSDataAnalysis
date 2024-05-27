package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;

public class PCDDFileLoadRunner extends PAHFileLoadRunner {

	//PCDD is identical to PAH except the first column is named "Congener" instead of "Compound"
	public PCDDFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}

	@Override
	protected String getFirstColumnHeader() {
		return "CONGENER";
	}
}

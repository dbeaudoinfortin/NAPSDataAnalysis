package com.dbf.naps.data.utilities;

import java.io.IOException;
import java.nio.file.Path;
import net.lingala.zip4j.ZipFile;

public class ZipUtil {
	
	public static void unzip(Path zipFilePath, Path destDirPath) throws IOException {
		try(ZipFile zipFile = new ZipFile(zipFilePath.toFile())){
			zipFile.extractAll(destDirPath.toAbsolutePath().toString());
		}
    }
}

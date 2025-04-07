package com.dbf.naps.data.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import net.lingala.zip4j.ZipFile;

public class ZipUtil {
	
	/**
	 * Extracted the provided zip file to the provided destination directory.
	 * 
	 * @param zipFilePath zip file to extract
	 * @param destDirPath destination directory to extract to.
	 * @throws IOException
	 */
	public static void unzip(Path zipFilePath, Path destDirPath) throws IOException {
		try(ZipFile zipFile = new ZipFile(zipFilePath.toFile())){
			zipFile.extractAll(destDirPath.toAbsolutePath().toString());
		}
    }
	
	public static void zipFile(File sourceFile, File destZipFile, boolean overwrite) throws IOException {
		zipFiles(List.of(sourceFile), destZipFile, overwrite);
	}

	public static void zipFiles(List<File> sourceFiles, File destZipFile, boolean overwrite) throws IOException {
		if(destZipFile.exists()) {
			if(destZipFile.isDirectory()) {
				throw new IOException("Destination zip file already exists and is a directory: " + destZipFile.getAbsolutePath());
			}
			if(!overwrite) {
				throw new IOException("Destination zip file already exists and overwriting is not allowed: " + destZipFile.getAbsolutePath());
			}
			if(!destZipFile.delete()) {
				throw new IOException("Failed to delete existing destination zip file: " + destZipFile.getAbsolutePath());
			}
		}
		try (FileOutputStream fos = new FileOutputStream(destZipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
			zos.setLevel(Deflater.BEST_COMPRESSION);
			for (File fileToZip : sourceFiles) {
				try (FileInputStream fis = new FileInputStream(fileToZip)) {
					// Create a new zip entry with the file name.
					final ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
					zos.putNextEntry(zipEntry);
					IOUtils.copy(fis, zos);
					zos.closeEntry();
				}
			}
		}
		
		//Now delete the original files
		for (File fileToZip : sourceFiles) {
			fileToZip.delete();
		}
	}
}

package com.aem.fmdita.utilities;

/*
XML Documentation for AEM
Copyright 2020 Adobe Systems Incorporated

This software is licensed under the Apache License, Version 2.0 (see
LICENSE file).

This software uses the following third party libraries that may have
licenses differing from that of the software itself. You can find the
libraries and their respective licenses below.

======================================================================
PLEASE REMOVE THESE INSTRUCTIONS BEFORE SUBMITTING THE FILE TO YOUR
REPO.

Include any other notices as required by third party licenses.

Example of how the attributions should look are here:
https://github.com/adobe/brackets/blob/master/NOTICE
======================================================================
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUnzipper {
	List fileList;
	/** Default log. */
	protected final static Logger log = LoggerFactory.getLogger(FileUnzipper.class);

	/*
	 * private static final String INPUT_ZIP_FILE = "C:\MyFile.zip"; private static
	 * final String OUTPUT_FOLDER = "C:\outputzip";
	 *
	 * public static void main( String[] args ) { FileUnzipper unZip = new
	 * FileUnzipper(); unZip.unZipIt(INPUT_ZIP_FILE,OUTPUT_FOLDER); }
	 *
	 */
	/**
	 * Unzip it
	 *
	 * @param zipFile input zip file
	 * @param output  zip file output folder
	 */
	public static boolean unZipIt(String zipFile, String outputFolder) {
		boolean tmpRet = true;
		byte[] buffer = new byte[1024];

		try {

			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				String tmpCheckFN = FilenameUtils.removeExtension(fileName);

				if (tmpCheckFN.equalsIgnoreCase(fileName)) {
					// — Directory, create if needed
					if (!newFile.exists()) {
						log.info("Create dir " + outputFolder + File.separator + fileName);
						boolean success = newFile.mkdirs();
						if (!success) {
							log.error("Could not create directory for " + fileName + ".");
							return false;
						}
					}
				} else {
					log.info("file unzip : " + newFile.getAbsoluteFile());

					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			log.info("Done Unzipping the file");

		} catch (IOException ex) {
			tmpRet = false;
			ex.printStackTrace();
		}

		return tmpRet;
	}
}
package cmabreu.sagitarii.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WrapperHelper {
	private String wrapperAlias;
	private String wrapperFolder;
	private String workFolder;

	public WrapperHelper(String wrapperAlias, String wrapperFolder,
			String workFolder) {
		this.wrapperAlias = wrapperAlias;
		this.wrapperFolder = wrapperFolder;
		this.workFolder = workFolder;
	}

	public String getInboxFolder() {
		return workFolder + "/inbox/";
	}

	public String getOutboxFolder() {
		return workFolder + "/outbox/";
	}

	public String getWorkFolder() {
		return workFolder + "/";
	}

	public String getWrapperFolder() {
		return wrapperFolder;
	}

	public List<String> readFromLibraryFolder(String file) throws Exception {
		System.out.println("[" + wrapperAlias + "] Read library: " + file);
		ArrayList<String> list = new ArrayList<String>();
		try {
			String line = "";
			file = wrapperFolder + "/" + file;
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			if (br != null) {
				br.close();
			}
			System.out.println("[" + wrapperAlias + "] Read library: "
					+ list.size() + " lines");
		} catch (Exception e) {
			System.out.println("[" + wrapperAlias + "] Error reading library: "
					+ file);
			throw e;
		}
		return list;
	}

	public void storeToZIP(List<String> sourceFiles, String zipFile) {
		System.out.println("[" + wrapperAlias + "] Creating ZIP file " + zipFile);
		try {
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (String src : sourceFiles) {
				File srcFile = new File(src);
				FileInputStream fis = new FileInputStream(srcFile);

				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int length;

				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			System.out.println("[" + wrapperAlias + "] Done.");
		} catch (IOException ioe) {
			System.out.println("[" + wrapperAlias + "] Error creating zip file: " + ioe);
		}

	}

	public void decompress(String compressedFile, String decompressedFile)
			throws Exception {
		System.out.println("[" + wrapperAlias + "] uncompressing "
				+ compressedFile + "...");
		byte[] buffer = new byte[1024];
		try {
			FileInputStream fileIn = new FileInputStream(compressedFile);
			GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
			FileOutputStream fileOutputStream = new FileOutputStream(
					decompressedFile);
			int bytes_read;
			while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, bytes_read);
			}
			gZIPInputStream.close();
			fileOutputStream.close();
			System.out.println("[" + wrapperAlias
					+ "] file was decompressed successfully");
		} catch (IOException ex) {
			System.out.println("[" + wrapperAlias
					+ "] error decompressing file: " + ex.getMessage());
			throw new Exception(ex.getMessage());
		}
	}

	public void compress(String source_filepath, String destinaton_zip_filepath)
			throws Exception {
		System.out.println("[" + wrapperAlias + "] compressing file ...");
		byte[] buffer = new byte[1024];
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(
					destinaton_zip_filepath);
			GZIPOutputStream gzipOuputStream = new GZIPOutputStream(
					fileOutputStream);
			FileInputStream fileInput = new FileInputStream(source_filepath);
			int bytes_read;
			while ((bytes_read = fileInput.read(buffer)) > 0) {
				gzipOuputStream.write(buffer, 0, bytes_read);
			}
			fileInput.close();
			gzipOuputStream.finish();
			gzipOuputStream.close();
			fileOutputStream.close();

			System.out.println("[" + wrapperAlias
					+ "] file was compressed successfully");

		} catch (IOException ex) {
			System.out.println("[" + wrapperAlias
					+ "] error compressing file: " + ex.getMessage());
			throw new Exception(ex.getMessage());
		}
	}

	public void runExternal(String application) throws Exception {
		Process process = null;
		System.out.println("[" + wrapperAlias + "] start external application");
		try {
			// process = Runtime.getRuntime().exec( application );
			List<String> args = new ArrayList<String>();
			args.add("/bin/sh");
			args.add("-c");
			args.add(application);

			process = new ProcessBuilder(args).start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			BufferedReader readerErr = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println("[" + wrapperAlias + ":EXTERNAL] : " + line);
			}

			line = "";
			while ((line = readerErr.readLine()) != null) {
				System.out.println("[" + wrapperAlias + ":EXTERNAL] : " + line);
			}

			process.waitFor();
			System.out.println("[" + wrapperAlias + "] Done ");
		} catch (Exception e) {
			System.out.println("[" + wrapperAlias
					+ "] Error runnig external application at ");
			System.out.println(application);
			System.out.println(e.getCause());
			for (StackTraceElement ste : e.getStackTrace()) {
				System.out.println(ste.getClassName());
			}
			throw e;
		}
	}

	public void moveFile(String source, String dest) throws Exception {
		System.out.println("[" + wrapperAlias + "] Move file ");
		System.out.println("[" + wrapperAlias + "]  > from " + source);
		System.out.println("[" + wrapperAlias + "]  > to   " + dest);
		try {
			File src = new File(source);
			File trgt = new File(dest);
			if (src.exists()) {
				Files.copy(src.toPath(), trgt.toPath());
				src.delete();
				System.out.println("[" + wrapperAlias + "] Moved");
			} else {
				System.out.println("[" + wrapperAlias
						+ "] Source file not found");
			}
		} catch (Exception e) {
			System.out.println("[" + wrapperAlias + "] Error when moving file");
			throw e;
		}
	}

	public List<String> scanFolder(String folder) {
		System.out.println("[" + wrapperAlias + "] Scanning folder " + folder
				+ "...");
		List<String> folderContent = new ArrayList<String>();
		File file = new File(folder);
		for (final File fileEntry : file.listFiles()) {
			if (!fileEntry.isDirectory()) {
				folderContent.add(fileEntry.getName());
			}
		}
		System.out.println("[" + wrapperAlias + "] Found "
				+ folderContent.size() + " files. ");
		return folderContent;
	}

	public void copyFile(String source, String dest) throws Exception {
		System.out.println("[" + wrapperAlias + "] Copy file ");
		System.out.println("[" + wrapperAlias + "]  > from " + source);
		System.out.println("[" + wrapperAlias + "]  > to   " + dest);
		try {
			File src = new File(source);
			File trgt = new File(dest);
			if (src.exists()) {
				Files.copy(src.toPath(), trgt.toPath());
				src.delete();
				System.out.println("[" + wrapperAlias + "] Copied");
			} else {
				System.out.println("[" + wrapperAlias
						+ "] Source file not found");
			}
		} catch (Exception e) {
			System.out
					.println("[" + wrapperAlias + "] Error when copying file");
			throw e;
		}
	}

}

package com.aboutJava.javaIoUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 文件与文件夹的操作。
 * 
 * @author zhangFeng
 * @since 0.1_2012-8-2
 */
public class FileOperatorUtil {
	private static FileOperatorUtil instance = null;

	public static FileOperatorUtil getInstance() {
		if (instance == null) {
			instance = new FileOperatorUtil();
		}
		return instance;
	}

	// 删除指定路径下所有txt文件 删除TXT文件
	public void deleteAllTxt(String path) {
		File folder = new File(path);
		String fileName = null;
		if (folder.isDirectory()) {
			// 列出所有文件保存发到File[]
			File[] files = folder.listFiles();
			// 遍历得到的文件信息File[]
			for (File file : files) {
				// 得到文件名然后判断是否带有.txt后缀 有则删除
				fileName = file.getName();
				if (fileName.lastIndexOf(".txt") != -1) {
					file.delete();
				} else {
					System.out.println("没有txt文件或者已被删除");
				}
			}
		} else {
			System.out.println("不能对非文件夹进行操作！！！");
		}
		if (-1 == fileName.lastIndexOf(".txt")) {
			System.out.println("没有txt文件或者已被删除");
		}
	}

	// 删除文件
	public void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else {
				System.out.println("输入有误，不是文件！");
			}
		} else {
			System.out.println("文件不存在");
		}
	}

	// 删除文件夹
	public void deleteFolder(String path) {
		File Folder = new File(path); // 用File类表示出源文件夹
		File[] Folders = Folder.listFiles(); // 用File类型数组表示文件夹下的文件
		if (Folder.isDirectory()) {
			for (File file : Folders) {
				if (file.isDirectory()) {
					deleteFolder(path + File.separator + file.getName());
				} else {
					// deleteFile(file);//这是个经验 参数类型不匹配 利用类库方法 减少错误
					file.delete(); // delete()删除文件或空文件夹 删除 自己以外的 空子文件夹
				}
			}
			Folder.delete(); // 删除自己 最后一个空文件夹
			// System.out.println("删除成功");
		} else
			System.out.println("不是文件夹或者文件夹不存在！");
	}

	// 以字节为单位 根据源文件和目标文件的路径 复制文件
	// fromPath:源文件路径 toPath：目标文件路径
	public void copyFile(String fromPath, String toPath) {
		// 用File表示出源文件和目标文件
		File fromFile = new File(fromPath);
		File toFile = new File(toPath);
		InputStream is = null;
		OutputStream os = null;
		try {
			// 建立输入输出流 分别连接一个文件
			is = new FileInputStream(fromFile);
			os = new FileOutputStream(toFile);
			int readByte = 0;
			while ((readByte = is.read()) != -1) { // 循环读取 和写入
				os.write(readByte); // 才输入到管道而已
				os.flush(); // 真正写入文件
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally { // 释放文件资源
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 以自定义缓冲区为单位 根据源文件和目标文件的路径复制文件：
	public void copyFileWithBuffer(String fromPath, String toPath) {
		// 用File表示出源文件和目标文件
		File fromFile = new File(fromPath);
		File toFile = new File(toPath);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			// 建立输入输出流 分别连接一个文件 用了装饰者设计模式
			bis = new BufferedInputStream(new FileInputStream(fromFile)); // ~~小管子套进大管子
			bos = new BufferedOutputStream(new FileOutputStream(toFile));
			byte[] buf = new byte[102400]; // 100K
			int readLen = 0;
			while ((readLen = bis.read(buf)) != -1) { // 循环读取一个缓冲区并写入
				// System.out.println(readLen);
				bos.write(buf, 0, readLen); // 才输入到管道而已
				bos.flush(); // 真正写入文件
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally { // 释放文件资源
			try {
				if (bis != null | bos != null) {
					bis.close();
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * 把文件夹里面的东西全部复制到另外一个文件夹。
	 * 
	 * @param fromPath 源文件夹路径
	 * @param toPath 目标文件夹路径
	 * @since 0.1_2012-8-2
	 */
	public void copyFolder(String fromPath, String toPath) {
		File fromFolder = new File(fromPath); // 用File类表示出源文件夹
		File[] fromFolders = fromFolder.listFiles(); // 用File类型数组表示文件夹下的文件
		File toFolder = new File(toPath); // 用File类表示出目标文件夹路径
		toFolder.mkdirs(); // 创建文件夹
		if (fromFolders == null) {
			return;
		} else {
			for (File file : fromFolders) {
				if (file.isDirectory()) {
					copyFolder(fromPath + File.separator + file.getName(), toPath + File.separator + file.getName());
				} else {
					copyFileWithBuffer(fromPath + File.separator + file.getName(), toPath + File.separator + file.getName());
				}
			}
		}
	}
	
	/**
	 *  移动文件。
	 * 
	 * @param fromPath 源文件夹路径
	 * @param toPath 目标文件夹路径
	 * @since 0.1_2012-8-2
	 */
	public void removeFile(String fromPath, String toPath) {
		File fromFile = new File(fromPath);
		File tOFile = new File(toPath);
		if (fromFile.isDirectory()) {
			System.out.println("不是文件");
		} else if (fromFile.exists()) {
			copyFileWithBuffer(fromPath, toPath);
			deleteFile(fromPath);
		} else {
			System.out.println("源文件不存在");
		}
	}

	/**
	 *  移动文件夹
	 *
	 * @param fromPath 源文件夹路径
	 * @param toPath 目标文件夹路径
	 * @since 0.1_2012-8-2
	 */
	public void removeFolder(String fromPath, String toPath) {
		File fromFolder = new File(fromPath);
		File tOFolder = new File(toPath);
		if (fromFolder.isDirectory()) {
			copyFolder(fromPath, toPath);
			deleteFolder(fromPath);
		} else {
			System.out.println("不是文件夹");
		}
	}

	// 新建文件 问题 这里面的判断条件
	public void createFile(String newFilePath) {
		File file = new File(newFilePath);
		File baseDir = new File(file.getParent());
		if (file.exists()) { // 是否已经存在
			System.out.println("目标文件已经存在，创建" + newFilePath + "失败");
		} else {
			if (!baseDir.exists()) {
				System.out.println("目标文件父目录不存在，准备创建...");
				file.getParentFile().mkdirs(); // 创建父目录
				try {
					file.createNewFile();
					if (file.exists()) {
						System.out.println("文件创建成功");
					} else {
						System.out.println("文件创建失败");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 新建文件夹
	public void createNewFolder(String newFilePath) {
		File file = new File(newFilePath);
		File baseDir = new File(file.getParent());
		if (!file.exists()) { // exists()即能判断文件又能判断目录是否存在
			file.mkdirs(); // 创建 文件的 所有上层目录 不创建文件
			if (file.exists()) {
				System.out.println("文件夹创建成功");
			} else {
				System.out.println("文件夹创建失败");
			}
		} else {
			System.out.println("目标文件已经存在，创建" + newFilePath + "失败");
		}
	}

	// 下载网络资源
	// 建立连接 得到流 流连接Internet和程序 这里的url->urlCon 相当于 用File类表示第一个参数表示的文件
	public void downloadNetFile(String urlPath, String toPath) {
		URL url = null;
		URLConnection urlCon = null;
		File toFile = null;
		InputStream is = null;
		OutputStream os = null;
		int readContent = 0;
		try {
			url = new URL(urlPath);
			urlCon = url.openConnection();
			toFile = new File(toPath);
			is = urlCon.getInputStream();
			os = new FileOutputStream(toPath); // InputStream OutputStream+FileOutputStream
			// readContent = is.read(); 在这里赋值不好
			while ((readContent = is.read()) != -1) {
				os.write(readContent);
				os.flush();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 创建文件夹目录
	 * 
	 * @param destDirName
	 * @return
	 */
	public boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			return true;
		}
		if (!destDirName.endsWith(File.separator))
			destDirName = destDirName + File.separator;
		// 创建单个目录
		if (dir.mkdirs()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		FileOperatorUtil.getInstance().copyFolder("G:/2/", "G:/1");
	}
}

package org.guaji.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.guaji.app.App;
import org.guaji.cryption.Md5;
import org.guaji.log.Log;
import org.guaji.nativeapi.NativeApi;
import org.guaji.os.MyException;

/**
 * 所有服务对象管理器
 */
public class ServiceManager {
	/**
	 * service的jar文件路径
	 */
	private String serviceJarFile;
	/**
	 * jar对应的classloader
	 */
	private ClassLoader serviceClassLoader;
	/**
	 * service对应的实例
	 */
	private Map<String, IService> serveiceInstances = new ConcurrentHashMap<String, IService>();
	/**
	 * 管理器单例
	 */
	private static ServiceManager instance = null;

	/**
	 * 获取管理器实例
	 * 
	 * @return
	 */
	public static ServiceManager getInstance() {
		if (instance == null) {
			instance = new ServiceManager();
		}
		return instance;
	}
	
	/**
	 * 否则函数
	 */
	public ServiceManager() {
	}
	
	/**
	 * 初始化
	 * 
	 * @param serviceJarFile
	 * @return
	 */
	public boolean init(String serviceJarFile) {
		// 检测
//		if (!NativeApi.checkHawk()) {
//			return false;
//		}
		
		if (serviceJarFile == null) {
			Log.errPrintln("service manager init failed");
			return false;
		}

		this.serviceJarFile = serviceJarFile;
		File file = new File(serviceJarFile);
		if (!file.exists() || !file.isFile()) {
			Log.errPrintln("service manager init failed");
			return false;
		}

		
		// 更新
		if (!update()) {
			return false;
		}
		return true;
	}

	/**
	 * 获取class存储路径
	 * 
	 * @return
	 */
	private String getClassFilePath() {
		return App.getInstance().getWorkPath() + "service" + File.separator;
	}

	/**
	 * 获取service对象
	 * 
	 * @param service
	 * @return
	 */
	public IService getService(String service) {
		return serveiceInstances.get(service);
	}
	
	/**
	 * 获取所有的service对象
	 * @return
	 */
	public Collection<IService> getServices() {
		return serveiceInstances.values();
	}

	/**
	 * 更新jar
	 */
	public boolean update() {
		try {
			String url = null;
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				url = "file:/" + getClassFilePath();
			} else {
				url = "file:" + getClassFilePath();
			}
			serviceClassLoader = new URLClassLoader(new URL[] { new URL(url) });
		} catch (MalformedURLException e) {
			MyException.catchException(e);
			return false;
		}
		
		List<ClassBlock> blockList = loadJarClassBlock(serviceJarFile);
		// 判断哪些文件需要替换加载
		for (ClassBlock block : blockList) {
			block.writeToFile();
			// 需要重新加载 $表示内部类
			if (block.getFilePath().indexOf("$") < 0) {
				String className = block.getFilePath().replace(getClassFilePath(), "").replaceAll("/", ".").replaceAll("\\\\", ".").replace(".class", "");
				try {
					Class<?> clazz = serviceClassLoader.loadClass(className);
					if (clazz != null) {
						Object obj = clazz.newInstance();
						if (obj instanceof IService) {
							IService service = (IService) obj;
							String name = service.getName();
							if (name != null && name.length() > 0) {
								serveiceInstances.put(name, service);
								Log.logPrintln(String.format("service update class: %s success,instance: %s", className,service.toString()));
							}
						}
					}
				} catch (Exception e) {
					MyException.catchException(e);
					Log.errPrintln(String.format("service load class: %s failed", className));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 从jar包加载class文件
	 * 
	 * @param jarFilePath
	 * @return
	 */
	private List<ClassBlock> loadJarClassBlock(String jarFilePath) {
		List<ClassBlock> blockList = new LinkedList<>();
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(jarFilePath);
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) enumeration.nextElement();
				try {
					if (entry.isDirectory()) {
						File directory = new File(getClassFilePath() + entry.getName());
						directory.mkdirs();
						continue;
					}

					String targetFilePath = getClassFilePath() + entry.getName();
					if (!entry.getName().endsWith(".class")) {
						continue;
					}

					int count = 0;
					byte data[] = new byte[4096];
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
					while ((count = bis.read(data, 0, data.length)) > 0) {
						bos.write(data, 0, count);
					}
					blockList.add(ClassBlock.valueOf(targetFilePath, bos.toByteArray()));
					bis.close();
					bos.close();
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}

		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					MyException.catchException(e);
				}
			}
		}
		return blockList;
	}

	/**
	 * service class 存储块
	 * 
	 * @author xulinqs
	 */
	private static class ClassBlock {
		private String filePath;
		private ByteBuffer buffer;
		private String md5sum;

		/**
		 * 创建class块
		 * 
		 * @param filePath
		 * @param bytes
		 * @return
		 */
		protected static ClassBlock valueOf(String filePath, byte[] bytes) {
			ClassBlock block = new ClassBlock();
			block.md5sum = Md5.makeMD5(bytes);
			block.buffer = ByteBuffer.allocate(bytes.length);
			block.buffer.put(bytes);
			block.buffer.flip();
			block.setFilePath(filePath);
			return block;
		}

		/**
		 * 获取文件路径
		 * 
		 * @return
		 */
		protected String getFilePath() {
			return filePath;
		}

		/**
		 * 设置文件路径
		 * 
		 * @param filePath
		 */
		protected void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		/**
		 * md5是否改变
		 * 
		 * @return
		 */
		@SuppressWarnings("unused")
		protected boolean isMd5Changed() {
			return !md5sum.equals(Md5.makeMD5(new File(filePath)));
		}

		/**
		 * 存储到文件
		 */
		protected void writeToFile() {
			File file = new File(filePath);
			File parent = file.getParentFile();
			if (parent != null && (!parent.exists())) {
				parent.mkdirs();
			}

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.remaining());
				bos.write(buffer.array(), buffer.position(), buffer.remaining());
				bos.flush();
				bos.close();
				fos.close();
			} catch (IOException e) {
				MyException.catchException(e);
			}
		}
	}
}

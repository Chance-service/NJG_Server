package org.guaji.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.App;
import org.guaji.log.Log;
import org.guaji.nativeapi.NativeApi;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;

/**
 * 配置文件管理器
 */
public class ConfigManager {
	/**
	 * xml类型配置注解
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ java.lang.annotation.ElementType.TYPE })
	public @interface XmlResource {
		/**
		 * 文件路径
		 * 
		 * @return
		 */
		public String file() default "";

		/**
		 * 存储结构, "map" | "list"
		 * 
		 * @return
		 */
		public String struct() default "map";
	}

	/**
	 * kv类型配置注解
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ java.lang.annotation.ElementType.TYPE })
	public @interface KVResource {
		/**
		 * 文件路径
		 * 
		 * @return
		 */
		public String file() default "";
	}

	/**
	 * json类型配置注解
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ java.lang.annotation.ElementType.TYPE })
	public @interface JsonResource {
		/**
		 * 文件路径
		 * 
		 * @return
		 */
		public String file() default "";
		
		/**
		 * 存储结构, "map" | "list"
		 * 
		 * @return
		 */
		public String struct() default "map";
	}
	
	/**
	 * 配置对象存储器
	 */
	private ConcurrentHashMap<Class<?>, ConfigStorage> storages = new ConcurrentHashMap<Class<?>, ConfigStorage>();
	/**
	 * 存储备份配置对象
	 */
	private Map<Class<?>, ConfigStorage> backupStorages = new ConcurrentHashMap<Class<?>, ConfigStorage>();
	/**
	 * 自动清理static容器数据
	 */
	private boolean autoClearStaticData = true;
	/**
	 * 配置管理器实例
	 */
	private static ConfigManager instance;

	/**
	 * 获取配置管理器实例
	 * 
	 * @return
	 */
	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	/**
	 * 初始化配置管理器
	 * 
	 * @param packages
	 *            , 多个包以逗号分隔
	 * @throws Exception
	 */
	public boolean init(String configPackages) {
		// 检测
//		if (!NativeApi.checkHawk()) {
//			return false;
//		}

		try {
			String[] configPackageArray = configPackages.split(",");
			if (configPackageArray != null) {
				for (String configPackage : configPackageArray) {
					Log.logPrintln("init config package: " + configPackage);
					List<Class<?>> classList = ClassScaner.scanClassesFilter(configPackage, XmlResource.class, KVResource.class, JsonResource.class);
					for (Class<?> configClass : classList) {
						storages.put(configClass, new ConfigStorage(configClass));
					}
				}
				Iterator<Class<?>> iterator = storages.keySet().iterator();
				while(iterator.hasNext()){
					ConfigStorage hcs=storages.get(iterator.next());
					hcs.assemble();
				}
			}
			
			// 最终校验配置文件数据
			if (!checkConfigData()) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
	
	
	
	
	/**
	 * 初始化配置管理器
	 * 
	 * @param packages
	 *            , 多个包以逗号分隔
	 * @throws Exception
	 */
	public boolean init(String configPackages,String dir) {
		// 检测
//		if (!NativeApi.checkHawk()) {
//			return false;
//		}

		try {
			String[] configPackageArray = configPackages.split(",");
			if (configPackageArray != null) {
				for (String configPackage : configPackageArray) {
					Log.logPrintln("init config package: " + configPackage);
					List<Class<?>> classList = ClassScaner.scanClassesFilter(configPackage, XmlResource.class, KVResource.class, JsonResource.class);
					for (Class<?> configClass : classList) {
						storages.put(configClass, new ConfigStorage(configClass,dir));
					}
				}
				Iterator<Class<?>> iterator = storages.keySet().iterator();
				while(iterator.hasNext()){
					ConfigStorage hcs=storages.get(iterator.next());
					hcs.assemble();
				}
			}
			
			// 最终校验配置文件数据
			if (!checkConfigData()) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	/**
	 * 自动清理静态数据
	 * 
	 * @param auto
	 */
	public void autoClearStaticData(boolean auto) {
		this.autoClearStaticData = auto;
	}
	
	/**
	 * 检测配置数据
	 * @return
	 */
	private boolean checkConfigData() {
		for (Map.Entry<Class<?>, ConfigStorage> entry : storages.entrySet()) {
			ConfigStorage storage = entry.getValue();
			if (!storage.checkValid()) {
				Log.errPrintln("config check valid failed: " + entry.getKey().getName());
				return false;
			}
		}
		return App.getInstance().checkConfigData();
	}
	
	/**
	 * 清理静态数据
	 * 
	 * @param configClass
	 * @return
	 */
	private boolean clearConfigStaticData(Class<?> configClass) {
		try {
			if (autoClearStaticData) {
				for (Field field : configClass.getDeclaredFields()) {
					if (!Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					
					for (Method method : field.getType().getDeclaredMethods()) {
						if (method.getName().equals("clear")) {
							try {
								field.setAccessible(true);
								method.invoke(field.get(null));
							} catch (Exception e) {
								MyException.catchException(e);
							}
						}
					}
				}
			}
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
	
	/**
	 * 更新加载
	 */
	public boolean updateReload() {
		backupStorages.clear();
		backupStorages.putAll(storages);
		List<ConfigStorage> needCheckList = new LinkedList<>();
		try {
			for (Entry<Class<?>, ConfigStorage> entry : backupStorages.entrySet()) {
				if (entry.getValue().checkUpdate()) {
					Log.logPrintln("check config update: " + entry.getValue().getFilePath());
					// 清理静态数据
					if (!clearConfigStaticData(entry.getKey())) {
						continue;
					}
					
					// 加载新配置信息
					ConfigStorage configStorage = new ConfigStorage(entry.getKey());
					configStorage.assemble();
					storages.put(entry.getKey(), configStorage);
					
					// 添加待检测列表
					needCheckList.add(configStorage);
				}
			}
		} catch (Exception e) {
			// 出现异常即恢复备份对象库
			storages.clear();
			storages.putAll(backupStorages);
			// 打印异常
			MyException.catchException(e);
			return false;
		}
		
		for(ConfigStorage storage : needCheckList) {
			// 校验失败即恢复备份配置信息
			if (!storage.checkValid()) {
				storages.clear();
				storages.putAll(backupStorages);
				Log.errPrintln("storage check failed: " + storage.getFilePath());
				return false;
			}
			Log.logPrintln("update config success: " + storage.getFilePath());
		}
		
		Log.logPrintln("check config finish: " + GuaJiTime.getTimeString());
		return true;
	}

	/**
	 * 更新加载
	 */
	public boolean updateReload(String confDir) {
		backupStorages.clear();
		backupStorages.putAll(storages);
		List<ConfigStorage> needCheckList = new LinkedList<>();
		try {
			for (Entry<Class<?>, ConfigStorage> entry : backupStorages.entrySet()) {
				if (entry.getValue().checkUpdate()) {
					Log.logPrintln("check config update: " + entry.getValue().getFilePath());
					// 清理静态数据
					if (!clearConfigStaticData(entry.getKey())) {
						continue;
					}
					
					// 加载新配置信息
					ConfigStorage configStorage = new ConfigStorage(entry.getKey(),confDir);
					configStorage.assemble();
					storages.put(entry.getKey(), configStorage);
					
					// 添加待检测列表
					needCheckList.add(configStorage);
				}
			}
		} catch (Exception e) {
			// 出现异常即恢复备份对象库
			storages.clear();
			storages.putAll(backupStorages);
			// 打印异常
			MyException.catchException(e);
			return false;
		}
		
		for(ConfigStorage storage : needCheckList) {
			// 校验失败即恢复备份配置信息
			if (!storage.checkValid()) {
				storages.clear();
				storages.putAll(backupStorages);
				Log.errPrintln("storage check failed: " + storage.getFilePath());
				return false;
			}
			Log.logPrintln("update config success: " + storage.getFilePath());
		}
		
		Log.logPrintln("check config finish: " + GuaJiTime.getTimeString());
		return true;
	}

	/**
	 * 获取配置列表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getConfigList(Class<T> cfgClass) {
		ConfigStorage storage = storages.get(cfgClass);
		if (storage != null) {
			return (List<T>) storage.getConfigList();
		}
		return null;
	}

	/**
	 * 获取配置表映射
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<Object, T> getConfigMap(Class<T> cfgClass) {
		ConfigStorage storage = storages.get(cfgClass);
		if (storage != null) {
			return (Map<Object, T>) storage.getConfigMap();
		}
		return null;
	}

	/**
	 * 获取指定配置文件中特定索引的配置
	 * 
	 * @param cfgClass
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getConfigByIndex(Class<T> cfgClass, int index) {
		ConfigStorage storage = storages.get(cfgClass);
		if (storage != null) {
			List<T> cfgList = (List<T>) storage.getConfigList();
			if (cfgList != null && cfgList.size() > index) {
				return cfgList.get(index);
			}
		}
		return null;
	}

	/**
	 * 获取指定配置文件中特定key的配置
	 * 
	 * @param cfgClass
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getConfigByKey(Class<T> cfgClass, Object key) {
		ConfigStorage storage = storages.get(cfgClass);
		if (storage != null) {
			Map<Object, T> cfgMap = (Map<Object, T>) storage.getConfigMap();
			if (cfgMap != null) {
				return cfgMap.get(key);
			}
		}
		return null;
	}
}

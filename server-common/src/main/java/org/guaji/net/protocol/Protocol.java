package org.guaji.net.protocol;

import java.nio.ByteBuffer;

import net.sf.json.JSONObject;

import org.apache.mina.core.buffer.IoBuffer;
import org.guaji.cache.Cache;
import org.guaji.cache.CacheObj;
import org.guaji.net.GuaJiSession;
import org.guaji.octets.OctetsStream;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.Builder;
import com.google.protobuf.ProtocolMessageEnum;

/**
 * 协议包装类
 */
public class Protocol extends CacheObj {
	/**
	 * 协议头字节数
	 */
	public static final int HEADER_SIZE = 16;
	/**
	 * 缓存对象
	 */
	private static Cache protocolCache = null;
	
	/*
	 * 协议头格式
	 */
	public static class ProtocolHeader {
		/**
		 * 类型
		 */
		public int type;
		/**
		 * 字节数
		 */
		public int size;
		/**
		 * 保留字段
		 */
		public int reserve;
		/**
		 * 校验码
		 */
		public int crc;

		/**
		 * 构造函数
		 * 
		 * @param type
		 */
		ProtocolHeader(int type) {
			this.type = type;
		}

		/**
		 * 清空信息
		 */
		public void clear() {
			type = 0;
			size = 0;
			reserve = 0;
			crc = 0;
		}
		
		@Override
		public String toString() {
			return String.format("[type: %d, size: %d, reserve: %d, crc: %d]", type, size, reserve, crc);
		}
	}

	/**
	 * 协议头
	 */
	private ProtocolHeader header;
	/**
	 * 协议数据
	 */
	private OctetsStream octets;
	/**
	 * 来源会话
	 */
	private GuaJiSession session;
	/**
	 * 需要发送的protobuf的builder对象
	 */
	private Builder<?> builder;
	/**
	 * 解析后的协议对象
	 */
	private Object parseObj;
	
	/**
	 * 设置对象缓存
	 * @param cache
	 */
	public static void setCache(Cache cache) {
		protocolCache = cache;
	}
	
	/**
	 * 释放对象
	 * @param protocol
	 */
	public static void release(Protocol protocol) {
		if (protocolCache != null) {
			protocol.clear();
			protocolCache.release(protocol);
		}
	}
	
	/**
	 * 静态构建
	 * 
	 * @return
	 */
	public static Protocol valueOf() {
		if (protocolCache != null) {
			return protocolCache.create();
		}
		return new Protocol();
	}
	
	/**
	 * 静态构建
	 * 
	 * @param type
	 * @return
	 */
	public static Protocol valueOf(int type) {
		Protocol protocol = valueOf();
		protocol.setType(type);
		return protocol;
	}

	/**
	 * 静态构建
	 * 
	 * @param type
	 * @param bytes
	 * @return
	 */
	public static Protocol valueOf(int type, byte[] bytes) {
		Protocol protocol = valueOf(type);
		try {
			protocol.writeOctets(bytes);
		} catch (Exception e) {
			MyException.catchException(e);
			return null;
		}
		return protocol;
	}

	/**
	 * 静态构建
	 * 
	 * @param type
	 * @param builder
	 * @return
	 */
	public static Protocol valueOf(int type, Builder<?> builder) {
		Protocol protocol = valueOf(type);
		try {
			protocol.writeOctets(builder.build().toByteArray());
			protocol.setBuilder(builder);
		} catch (Exception e) {
			MyException.catchException(e);
			return null;
		}
		return protocol;
	}

	/**
	 * 静态构建
	 * 
	 * @param type
	 * @param builder
	 * @return
	 */
	public static Protocol valueOf(ProtocolMessageEnum type, Builder<?> builder) {
		Protocol protocol = valueOf(type.getNumber());
		try {
			protocol.writeOctets(builder.build().toByteArray());
			protocol.setBuilder(builder);
		} catch (Exception e) {
			MyException.catchException(e);
			return null;
		}
		return protocol;
	}

	/**
	 * Packet静态构建
	 * 
	 * @param type
	 * @param builder
	 * @return
	 */
	public static Protocol valueOf(int type, Packet packet) {
		Protocol protocol = valueOf(type);
		try {
			OctetsStream stream = OctetsStream.create();
			packet.marshal(stream);
			stream.flip();
			protocol.writeOctets(stream.getBuffer());
		} catch (Exception e) {
			MyException.catchException(e);
			return null;
		}
		return protocol;
	}
	
	/**
	 * Packet静态构建
	 * 
	 * @param packet
	 * @return
	 */
	public static Protocol valueOf(Packet packet){
		Protocol protocol = valueOf(packet.getType());
		try {
			OctetsStream stream = OctetsStream.create();
			packet.marshal(stream);
			stream.flip();
			protocol.writeOctets(stream.getBuffer());
		} catch (Exception e) {
			MyException.catchException(e);
			return null;
		}
		return protocol;
	}
	
	/**
	 * 构造函数
	 */
	protected Protocol() {
		header = new ProtocolHeader(0);
	}

	/**
	 * 构造函数
	 * 
	 * @param type
	 */
	protected Protocol(int type) {
		header = new ProtocolHeader(type);
	}

	/**
	 * 数据清理
	 */
	public void clear() {
		if (octets != null) {
			octets.clear();
		}
		header.clear();
		parseObj = null;
		session  = null;
	}

	/**
	 * 对象克隆
	 */
	@Override
	protected CacheObj clone() {
		return new Protocol();
	}
	
	/**
	 * 格式化字符串
	 */
	@Override
	public String toString() {
		if (octets == null) {
			return header.toString() + "\r\ndata: null\r\n";
		}
		return header.toString() + "\r\n" + "data: \r\n" + OSOperator.bytesToHexString(octets.getBuffer().array(), octets.position(), octets.remaining());
	}
	
	/**
	 * 绑定会话来源
	 * 
	 * @param session
	 */
	public void bindSession(GuaJiSession session) {
		this.session = session;
	}

	/**
	 * 获得绑定的会话
	 * 
	 * @return
	 */
	public GuaJiSession getSession() {
		return session;
	}

	/**
	 * 检测协议类型
	 * 
	 * @return
	 */
	public boolean checkType(int type) {
		if (header != null) {
			return header.type == type;
		}
		return false;
	}

	/**
	 * 检测协议类型
	 * 
	 * @return
	 */
	public boolean checkType(ProtocolMessageEnum type) {
		if (header != null) {
			return header.type == type.getNumber();
		}
		return false;
	}

	/**
	 * 获得协议类型
	 * 
	 * @return
	 */
	public int getType() {
		if (header != null) {
			return header.type;
		}
		return 0;
	}

	/**
	 * 设置协议类型
	 * 
	 * @return
	 */
	public Protocol setType(int type) {
		if (header != null) {
			header.type = type;
		}
		return this;
	}
	
	/**
	 * 获得协议大小
	 * 
	 * @return
	 */
	public int getSize() {
		if (header != null) {
			return header.size;
		}
		return 0;
	}

	/**
	 * 获取保留字段
	 * 
	 * @return
	 */
	public int getReserve() {
		if (header != null) {
			return header.reserve;
		}
		return 0;
	}

	/**
	 * 设置保留字段
	 * 
	 * @return
	 */
	public Protocol setReserve(int reserve) {
		if (header != null) {
			header.reserve = reserve;
		}
		return this;
	}

	/**
	 * 获得协议校检码
	 * 
	 * @return
	 */
	public int getCrc() {
		if (header != null) {
			return header.crc;
		}
		return 0;
	}

	/**
	 * 获取协议buffer
	 * 
	 * @return
	 */
	public OctetsStream getOctets() {
		return octets;
	}

	/**
	 * 获取待发送pb的builder对象
	 * 
	 * @return
	 */
	public Builder<?> getBuilder() {
		return builder;
	}

	/**
	 * 设置待发送pb的builder对象
	 * 
	 * @param builder
	 */
	public void setBuilder(Builder<?> builder) {
		this.builder = builder;
	}
	
	/**
	 * crc校验
	 */
	public boolean checkCrc(int crc) {
		return header.crc == crc;
	}

	/**
	 * 协议打包: type + size + reserve + crc + data
	 * 
	 * @param os
	 * @return
	 */
	public boolean encode(OctetsStream os) {
		// 协议编码
		try {
			// 有效协议头
			os.writeInt(header.type);
			os.writeInt(header.size);
			os.writeInt(header.reserve);
			os.writeInt(header.crc);
			
			if (header.size > 0) {
				os.pushBytes(octets.getBuffer().array(), octets.position(), octets.remaining());
			}
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	/**
	 * 协议打包: type + size + reserve + crc + data
	 * 
	 * @param buffer
	 * @return
	 */
	public boolean encode(IoBuffer buffer) {
		// 协议编码
		try {
			// 有效协议头
			buffer.putInt(header.type);
			buffer.putInt(header.size);
			buffer.putInt(header.reserve);
			buffer.putInt(header.crc);
			
			if (header.size > 0) {
				buffer.put(octets.getBuffer().array(), octets.position(), octets.remaining());
			}
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	/**
	 * 协议解包
	 * 
	 * @param os
	 * @return
	 * @throws MyException
	 */
	public boolean decode(OctetsStream os) throws MyException {
		// 检测协议完整性
		if (os.remaining() < HEADER_SIZE) {
			return false;
		}

		// 重置状态
		header.clear();
		int crc = 0;

		// 标记
		os.mark();
		try {
			header.type = os.readInt();
			header.size = os.readInt();
			header.reserve = os.readInt();
			header.crc = os.readInt();

			// 数据体大小判断
			if (os.remaining() >= header.size) {
				// 读协议数据
				if (header.size > 0) {
					if (octets == null) {
						octets = OctetsStream.create(header.size);
					} else {
						octets.clear().expand(header.size);
					}
					octets.pushBytes(os.popBytes(header.size));
					octets.flip();
					crc = octets.calcCrc();
				}
			} else {
				// 数据包不完全,回滚
				os.rollback();
				return false;
			}
		} catch (Exception e) {
			// 数据包不完全,回滚
			os.rollback();
			MyException.catchException(e);
			return false;
		}

		// crc校验
		if (!checkCrc(crc)) {
			throw new MyException(String.format("protocol crc verify failed, type: %d", header.type));
		}

		return true;
	}

	/**
	 * 协议解包
	 * 
	 * @param buffer
	 * @return
	 * @throws MyException
	 */
	public boolean decode(IoBuffer buffer) throws MyException {
		// 检测协议完整性
		if (buffer.remaining() < HEADER_SIZE) {
			return false;
		}

		// 重置状态
		header.clear();
		int crc = 0;

		// 标记
		buffer.mark();
		try {
			header.type = buffer.getInt();
			header.size = buffer.getInt();
			header.reserve = buffer.getInt();
			header.crc = buffer.getInt();

			// 数据体大小判断
			if (buffer.remaining() >= header.size) {
				// 读协议数据
				if (header.size > 0) {
					if (octets == null) {
						octets = OctetsStream.create(header.size);
					} else {
						octets.clear().expand(header.size);
					}
					buffer.get(octets.getBuffer().array(), 0, header.size);
					// 移动postion
					octets.position(header.size);
					octets.flip();
					crc = octets.calcCrc();
				}
			} else {
				// 数据包不完全,回滚
				buffer.reset();
				return false;
			}
		} catch (Exception e) {
			// 数据包不完全,回滚
			buffer.reset();
			// 捕获异常
			MyException.catchException(e);
			return false;
		}

		// crc校验
		if (!checkCrc(crc)) {
			throw new MyException(String.format("protocol crc verify failed, type: %d", header.type));
		}

		return true;
	}

	/**
	 * 协议解包
	 * 
	 * @param jsonData
	 * @return
	 * @throws MyException
	 */
	public boolean decodeFromJson(String jsonData) throws MyException {
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonData);
			if (!jsonObject.containsKey("type") || !jsonObject.containsKey("protocol")) {
				return false;
			}
			
			// 重置状态
			header.clear();
			header.type = jsonObject.getInt("type");
			if (writeOctets(jsonObject.getString("protocol").getBytes())) {
				if (jsonObject.containsKey("crc")) {
					// crc校验
					int crc = jsonObject.getInt("crc");
					if (!checkCrc(crc)) {
						throw new MyException(String.format("protocol crc verify failed, type: %d", header.type));
					}
				}
				return true;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
	
	/**
	 * 写缓冲区
	 * 
	 * @param bytes
	 * @return
	 */
	public boolean writeOctets(byte[] bytes) {
		if (bytes == null) {
			return true;
		}

		try {
			if (octets == null) {
				octets = OctetsStream.create(bytes, false);
			} else {
				octets.clear().pushBytes(bytes).flip();
			}
			calcOctets();
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	/**
	 * 写缓冲区
	 * 
	 * @param buffer
	 * @return
	 */
	public boolean writeOctets(ByteBuffer byteBuffer) {
		if (byteBuffer == null) {
			return true;
		}

		try {
			if (octets == null) {
				octets = OctetsStream.create(byteBuffer);
			} else {
				octets.clear().pushBuffer(byteBuffer).flip();
			}
			calcOctets();
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	/**
	 * 计算协议信息
	 */
	public void calcOctets() {
		if (octets != null) {
			header.size = octets.remaining();
			header.crc = octets.calcCrc();
		}
	}

	/**
	 * 解析协议
	 * 
	 * @param template
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends GeneratedMessage> T parseProtocol(T template) {
		if (parseObj == null) {
			if (session != null && session.isJsonWebSession()) {
				parseObj = ProtocolManager.getInstance().parseFromJson(this, template);
			} else {
				parseObj = ProtocolManager.getInstance().parseProtocol(this, template);
			}
		}
		return (T) parseObj;
	}
	
	/**
	 * 解析协议
	 * 
	 * @return
	 */
	public Packet parsePacket() {
		if (parseObj == null) {
			parseObj = ProtocolManager.getInstance().parsePacket(this);
		}
		return (Packet) parseObj;
	}
}

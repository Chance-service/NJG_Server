package org.guaji.cryption;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Inflater;

import org.guaji.net.GuaJiNetManager;
import org.guaji.os.MyException;

/**
 * 解密组件
 */
public class Decryption {
	/**
	 * 定义默认buffer大小
	 */
	static int BUFFER_SIZE = 10;
	/**
	 * zlib流加密
	 */
	private Inflater inflater;
	/**
	 * 默认缓冲器大小
	 */
	private int bufferSize = 4096;
	/**
	 * 缓冲buffer
	 */
	private byte[] buffer;

	/**
	 * 初始化
	 */
	public Decryption() {
		if (GuaJiNetManager.getInstance().getSessionBufSize() < 0) {
			bufferSize = GuaJiNetManager.getInstance().getSessionBufSize();
		}
		buffer = new byte[bufferSize];
		inflater = new Inflater();
	}

	/**
	 * 重置
	 */
	public void resetInflater() {
		inflater.reset();
	}
	
	/**
	 * 解密
	 * 
	 * @param input
	 * @return
	 */
	public ByteBuffer update(byte[] input) {
		ByteBuffer output = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			inflater.setInput(input);
			int count = inflater.inflate(buffer);
			while (count > 0) {
				bos.write(buffer, 0, count);
				count = inflater.inflate(buffer);
			}
			output = ByteBuffer.wrap(bos.toByteArray());
			bos.close();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return output;
	}

	/**
	 * 解密
	 * 
	 * @param input
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public boolean update(byte[] input, ByteBuffer output) throws Exception {
		output.mark();
		try {
			inflater.setInput(input);
			int count = inflater.inflate(buffer);
			while (count > 0) {
				output.put(buffer, 0, count);
				count = inflater.inflate(buffer);
			}
			return true;
		} catch (Exception e) {
			output.reset();
			throw e;
		}
	}

	/**
	 * 解密
	 * 
	 * @param input
	 * @return
	 */
	public ByteBuffer update(ByteBuffer input) {
		ByteBuffer output = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			inflater.setInput(input.array(), input.position(), input.remaining());
			// 设置buffer全部被读取
			input.position(input.limit());
			int count = inflater.inflate(buffer);
			while (count > 0) {
				bos.write(buffer, 0, count);
				count = inflater.inflate(buffer);
			}
			output = ByteBuffer.wrap(bos.toByteArray());
			bos.close();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return output;
	}

	/**
	 * 解密
	 * 
	 * @param input
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public boolean update(ByteBuffer input, ByteBuffer output) throws Exception {
		output.mark();
		try {
			inflater.setInput(input.array(), input.position(), input.remaining());
			// 设置buffer全部被读取
			input.position(input.limit());
			int count = inflater.inflate(buffer);
			while (count > 0) {
				output.put(buffer, 0, count);
				count = inflater.inflate(buffer);
			}
			return true;
		} catch (Exception e) {
			output.reset();
			throw e;
		}
	}
}

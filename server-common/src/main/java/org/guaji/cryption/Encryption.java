package org.guaji.cryption;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

import org.guaji.net.GuaJiNetManager;
import org.guaji.os.MyException;
import org.guaji.util.GuaJiZlib;

/**
 * 加密组件
 */
public class Encryption {
	/**
	 * zlib流加密
	 */
	private Deflater deflater;
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
	public Encryption() {
		if (GuaJiNetManager.getInstance().getSessionBufSize() < 0) {
			bufferSize = GuaJiZlib.zlibBound(GuaJiNetManager.getInstance().getSessionBufSize());
		}
		buffer = new byte[bufferSize];
		deflater = new Deflater();
	}

	/**
	 * 重置
	 */
	public void resetDeflater() {
		deflater.reset();
	}
	
	/**
	 * 加密
	 * @param input
	 * @return
	 */
	public ByteBuffer update(byte[] input) {
		ByteBuffer output = null;
		try {
			if (bufferSize < GuaJiZlib.zlibBound(input.length)) {
				bufferSize = GuaJiZlib.zlibBound(input.length);
				buffer = new byte[bufferSize];
			}
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			deflater.setInput(input);
			int count = deflater.deflate(buffer, 0, buffer.length, Deflater.SYNC_FLUSH);
			while (count > 0) {
				bos.write(buffer, 0, count);
				count = deflater.deflate(buffer, 0, buffer.length, Deflater.SYNC_FLUSH);				
			}
			output = ByteBuffer.wrap(bos.toByteArray());
			bos.close();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return output;
	}
	
	/**
	 * 加密
	 * @param input
	 * @return
	 */
	public ByteBuffer update(ByteBuffer input) {
		ByteBuffer output = null;
		try {
			if (bufferSize < GuaJiZlib.zlibBound(input.remaining())) {
				bufferSize = GuaJiZlib.zlibBound(input.remaining());
				buffer = new byte[bufferSize];
			}
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			deflater.setInput(input.array(), input.position(), input.remaining());
			// 设置buffer全部被读取
			input.position(input.limit());
			int count = deflater.deflate(buffer, 0, buffer.length, Deflater.SYNC_FLUSH);
			while (count > 0) {
				bos.write(buffer, 0, count);
				count = deflater.deflate(buffer, 0, buffer.length, Deflater.SYNC_FLUSH);				
			}
			output = ByteBuffer.wrap(bos.toByteArray());
			bos.close();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return output;
	}
}

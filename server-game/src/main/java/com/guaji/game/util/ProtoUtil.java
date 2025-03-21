package com.guaji.game.util;

import java.nio.ByteBuffer;

import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;
import org.guaji.util.GuaJiZlib;

import com.guaji.game.GsConfig;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.SysProtocol.HPErrorCode;

public class ProtoUtil {
	private static int RESERVE_ZLIB_COMPRESS = 1;
	private static int RESERVE_XOR_MIN_VALUE = 9;
	private static int RESERVE_XOR_MAX_VALUE = 99;
	/**
	 * 协议压缩
	 * 
	 * @return
	 */
	public static Protocol compressProtocol(Protocol protocol) {
		try {
			if (GsConfig.getInstance().isWebSocket()) {
				return protocol;
			}
			
			if (protocol.getSize() > 0) {
				ByteBuffer buffer = protocol.getOctets().getBuffer();
				byte[] bytes = GuaJiZlib.zlibDeflate(buffer.array(), buffer.position(), buffer.remaining());
				protocol.writeOctets(bytes);
				protocol.setReserve(RESERVE_ZLIB_COMPRESS);
			}
			return protocol;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}
	
	/**
	 * 协议解密
	 * @return
	 */
	public static Protocol decryptionProtocol(GuaJiSession session, Protocol protocol) {
		int lowMask = 0x000000FF & protocol.getReserve();
		int highMask = (0xFFFF0000 & protocol.getReserve()) >> 16;
		if (GsConfig.getInstance().isWebSocket()) {
			if (lowMask != 0) {
				return null;
			}
		} else {
			if (protocol.getReserve() != 0) {
				if (lowMask < RESERVE_XOR_MIN_VALUE || lowMask > RESERVE_XOR_MAX_VALUE)
					return null;
				
				if (highMask <= session.getProtocolOrder())
					return null;
				
				session.setProtocolOrder(highMask);
			}
		}
		
		if (lowMask >= RESERVE_XOR_MIN_VALUE && lowMask <= RESERVE_XOR_MAX_VALUE) {
			if (protocol.getSize() > 0) {
				ByteBuffer buffer = protocol.getOctets().getBuffer();
				for (int i=0; i< protocol.getSize(); i++) {
					buffer.array()[i] ^= lowMask;
				}
				protocol.calcOctets();
			}
		}
		return protocol;
	}
	
	/**
	 * 生成错误码协议
	 * @param hpCode
	 * @param errCode
	 * @param errFlag
	 * @return
	 */
	public static Protocol genErrorProtocol(int hpCode, int errCode, int errFlag) {
		HPErrorCode.Builder builder = HPErrorCode.newBuilder();
		builder.setHpCode(hpCode);
		builder.setErrCode(errCode);
		builder.setErrFlag(errFlag);
		return Protocol.valueOf(HP.sys.ERROR_CODE, builder);
	}
}

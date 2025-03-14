package org.guaji.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.guaji.app.App;
import org.guaji.cryption.Decryption;
import org.guaji.log.Log;
import org.guaji.nativeapi.NativeApi;
import org.guaji.net.NetStatistics;
import org.guaji.net.GuaJiSession;
import org.guaji.net.client.ClientSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.websocket.WebSocketUtil;
import org.guaji.os.MyException;

/**
 * 协议解码器
 */
public class Decoder extends ProtocolDecoderAdapter {
	/**
	 * 协议解码
	 */
	@Override
	public void decode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput output) throws Exception {
		// 通知统计信息
		NetStatistics.getInstance().onRecvBytes(buffer.remaining());
		
		// 协议解码
		Object attrObject = session.getAttribute(GuaJiSession.SESSION_ATTR);
		if (attrObject instanceof GuaJiSession) {
			GuaJiSession guaJiSession = (GuaJiSession) attrObject;
			if (App.getInstance().getAppCfg().isWebSocket()) {
				if (guaJiSession.isWebSession()) {
					decodeWebSocketProtocol(guaJiSession, buffer, output);
				} else {
					// http握手请求判定
					int pos = buffer.position();
					boolean handShake = WebSocketUtil.responseWebSockeHandShake(guaJiSession, buffer);
					if (!handShake && guaJiSession.isActive()) {
						buffer.position(pos);
						decodeProtocol(guaJiSession, buffer, output);
					}
					
					// 调试打印
					try {
						Log.debugPrintln("websocket handshake: " + new String(buffer.array()));
					} catch (Exception e) {
						MyException.catchException(e);
					}
				} 
			} else {
				decodeProtocol(guaJiSession, buffer, output);
			}
		} else if (attrObject instanceof ClientSession) {
			decodeProtocol((ClientSession) attrObject, buffer, output);
		}
	}

	/**
	 * 服务器接收到协议进行解码
	 * 
	 * @param session
	 * @param buffer
	 * @param output
	 * @throws Exception
	 */
	private void decodeProtocol(GuaJiSession session, IoBuffer buffer, ProtocolDecoderOutput output) throws Exception {
		if (session != null) {
			IoBuffer inBuffer = session.getInBuffer();
			if (inBuffer != null) {
				try {
					// 输入缓冲区接收数据
					Decryption decryption = session.getDecryption();
					if (decryption != null) {
						decryption.update(buffer.buf(), inBuffer.buf());
					} else {
						inBuffer.put(buffer);
					}
					inBuffer.flip();
					
					// 协议解码
					while (inBuffer.remaining() >= Protocol.HEADER_SIZE) {
						// 协议解码
						Protocol protocol = Protocol.valueOf();
						// 绑定协议会话
						protocol.bindSession(session);
						// 协议解码
						if (!protocol.decode(inBuffer)) {
							break;
						}
						
//						if (!NativeApi.protocol(protocol.getType(), protocol.getSize(), protocol.getReserve(), protocol.getCrc())) {
//							return;
//						}
						
						// 解码成协议返回
						output.write(protocol);
					}
				} catch (Exception e) {
					// 协议解码异常
					session.onDecodeFailed();
					
					MyException.catchException(e);
				}

				// 缓冲区整理
				int pos = inBuffer.position();
				int remaining = inBuffer.remaining();
				inBuffer.clear();
				if (remaining > 0) {
					inBuffer.put(inBuffer.array(), pos, remaining);
				}
			}
		}
	}
	
	/**
	 * 服务器接收到协议进行解码
	 * 
	 * @param session
	 * @param buffer
	 * @param output
	 * @throws Exception
	 */
	private void decodeWebSocketProtocol(GuaJiSession session, IoBuffer buffer, ProtocolDecoderOutput output) throws Exception {
		if (session != null) {
			IoBuffer inBuffer = session.getInBuffer();
			if (inBuffer != null) {
				try {
					// 输入缓冲区接收数据
					inBuffer.put(buffer);
					inBuffer.flip();
					
					// 解析出帧数据
					IoBuffer frameBuffer = WebSocketUtil.decodeWebSocketDataBuffer(inBuffer, session);
					
					// 协议解码
					while (frameBuffer != null && frameBuffer.remaining() >= Protocol.HEADER_SIZE) {
						// 协议解码
						Protocol protocol = Protocol.valueOf();
						// 绑定协议会话
						protocol.bindSession(session);
						// 协议解码(支持文本模式和二进制模式)
						if (session.isJsonWebSession()) {
							if (!protocol.decodeFromJson(new String(frameBuffer.array(), 0, frameBuffer.remaining()))) {
								break;
							}
							frameBuffer.position(frameBuffer.limit());
						} else {
							if (!protocol.decode(frameBuffer)) {
								break;
							}
						}
						
//						if (!NativeApi.protocol(protocol.getType(), protocol.getSize(), protocol.getReserve(), protocol.getCrc())) {
//							return;
//						}
						
						// 解码成协议返回
						output.write(protocol);
					}
					
					if (inBuffer.hasRemaining() && App.getInstance().getAppCfg().isDebug()) {
						Log.logPrintln("websocket decode buffer uncompleted");
					}
				} catch (Exception e) {
					// 协议解码异常
					session.onDecodeFailed();
					
					MyException.catchException(e);
				}

				// 缓冲区整理
				int pos = inBuffer.position();
				int remaining = inBuffer.remaining();
				inBuffer.clear();
				if (remaining > 0) {
					inBuffer.put(inBuffer.array(), pos, remaining);
				}
			}
		}
	}
	
	/**
	 * 服务器接收到协议进行解码
	 * 
	 * @param session
	 * @param buffer
	 * @param output
	 * @throws Exception
	 */
	private void decodeProtocol(ClientSession session, IoBuffer buffer, ProtocolDecoderOutput output) throws Exception {
		if (session != null) {
			IoBuffer inBuffer = session.getInBuffer();
			if (inBuffer != null) {
				try {
					// 输入缓冲区接收数据
					Decryption decryption = session.getDecryption();
					if (decryption != null) {
						decryption.update(buffer.buf(), inBuffer.buf());
					} else {
						inBuffer.put(buffer);
					}
					inBuffer.flip();
	
					// 协议解码
					while (inBuffer.remaining() >= Protocol.HEADER_SIZE) {
						// 协议解码
						Protocol protocol = Protocol.valueOf();
						if (!protocol.decode(inBuffer)) {
							break;
						}
						
						// 解码成协议返回
						output.write(protocol);
					}
				} catch (Exception e) {
					// 协议解码异常
					session.onDecodeError();
					
					MyException.catchException(e);
				}

				// 缓冲区整理
				int pos = inBuffer.position();
				int remaining = inBuffer.remaining();
				inBuffer.clear();
				if (remaining > 0) {
					inBuffer.put(inBuffer.array(), pos, remaining);
				}
			}
		}
	}
}

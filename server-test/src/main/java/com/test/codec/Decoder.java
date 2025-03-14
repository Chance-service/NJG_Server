package com.test.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.guaji.app.App;
import org.guaji.cryption.Decryption;
import org.guaji.log.Log;
import org.guaji.net.GuaJiSession;
import org.guaji.net.NetStatistics;
import org.guaji.net.client.ClientSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.websocket.WebSocketUtil;
import org.guaji.os.MyException;

public class Decoder  extends ProtocolDecoderAdapter {
	/**
	 *
	 */
	@Override
	public void decode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput output) throws Exception {
		NetStatistics.getInstance().onRecvBytes(buffer.remaining());
		

		Object attrObject = session.getAttribute(GuaJiSession.SESSION_ATTR);
		if (attrObject instanceof GuaJiSession) {
			GuaJiSession guaJiSession = (GuaJiSession) attrObject;
			decodeProtocol(guaJiSession, buffer, output);
		} else if (attrObject instanceof ClientSession) {
			decodeProtocol((ClientSession) attrObject, buffer, output);
		}
	}

	/**
	 * 
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
					Decryption decryption = session.getDecryption();
					if (decryption != null) {
						decryption.update(buffer.buf(), inBuffer.buf());
					} else {
						inBuffer.put(buffer);
					}
					inBuffer.flip();
					

					while (inBuffer.remaining() >= Protocol.HEADER_SIZE) {

						Protocol protocol = Protocol.valueOf();

						protocol.bindSession(session);

						if (!protocol.decode(inBuffer)) {
							break;
						}
						output.write(protocol);
					}
				} catch (Exception e) {
					
					session.onDecodeFailed();
					
					MyException.catchException(e);
				}


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
	 * 
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
				
					Decryption decryption = session.getDecryption();
					if (decryption != null) {
						decryption.update(buffer.buf(), inBuffer.buf());
					} else {
						inBuffer.put(buffer);
					}
					inBuffer.flip();

					while (inBuffer.remaining() >= Protocol.HEADER_SIZE) {

						Protocol protocol = Protocol.valueOf();
						if (!protocol.decode(inBuffer)) {
							break;
						}

						output.write(protocol);
					}
				} catch (Exception e) {
					session.onDecodeError();
					
					MyException.catchException(e);
				}

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

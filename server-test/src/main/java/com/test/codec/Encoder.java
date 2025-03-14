package com.test.codec;

import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.guaji.cryption.Encryption;
import org.guaji.net.GuaJiSession;
import org.guaji.net.NetStatistics;
import org.guaji.net.client.ClientSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

public class Encoder  extends ProtocolEncoderAdapter {
	/**
	 *
	 */
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput output) throws Exception {
		try {
			Object attrObject = session.getAttribute(GuaJiSession.SESSION_ATTR);
			//
			IoBuffer buffer = null;
			if (attrObject instanceof GuaJiSession) {
				GuaJiSession guaJiSession = (GuaJiSession) attrObject;
				buffer = encodeProtocol(guaJiSession, message);
			} else if (attrObject instanceof ClientSession) {
				buffer = encodeProtocol((ClientSession) attrObject, message);
			}
			
			if (buffer != null) {
				output.write(buffer);
				//
				NetStatistics.getInstance().onSendBytes(buffer.remaining());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * 
	 * @param session
	 * @param message
	 * @throws Exception
	 */
	private IoBuffer encodeProtocol(GuaJiSession session, Object message) throws Exception {
		if (message instanceof Protocol) {			
			
			session.lock();
			Protocol protocol = (Protocol) message;
			try {
				IoBuffer outBuffer = session.getOutBuffer();
				if (outBuffer != null) {
					outBuffer.clear();
					if (!protocol.encode(outBuffer)) {
						throw new MyException("protocol encode failed");
					}
					outBuffer.flip();
					ByteBuffer byteBuffer = null;
					Encryption encryption = session.getEncryption();
					if (encryption != null) {
						byteBuffer = encryption.update(outBuffer.buf());
					} else {
						byteBuffer = ByteBuffer.allocate(outBuffer.remaining()).order(outBuffer.order());
						byteBuffer.put(outBuffer.array(), outBuffer.position(), outBuffer.remaining());
						byteBuffer.flip();
					}			
					return IoBuffer.wrap(byteBuffer);
				}
			} finally {
				Protocol.release(protocol);
				session.unlock();
			}
		}
		return encodeProtocol(message);
	}

	/**
	 *
	 * 
	 * @param session
	 * @param message
	 * @throws Exception
	 */
	private IoBuffer encodeProtocol(ClientSession session, Object message) throws Exception {
		if (message instanceof Protocol) {
			Protocol protocol = (Protocol) message;
			IoBuffer outBuffer = session.getOutBuffer();
			outBuffer.clear();
			if (!protocol.encode(outBuffer)) {
				throw new MyException("protocol encode failed");
			}
			outBuffer.flip();
			ByteBuffer byteBuffer = null;
			Encryption encryption = session.getEncryption();
			if (encryption != null) {
				byteBuffer = encryption.update(outBuffer.buf());
			} else {
				byteBuffer = ByteBuffer.allocate(outBuffer.remaining()).order(outBuffer.order());
				byteBuffer.put(outBuffer.array(), outBuffer.position(), outBuffer.remaining());
				byteBuffer.flip();
			}
			return IoBuffer.wrap(byteBuffer);
		}
		return encodeProtocol(message);
	}
	
	/**
	 *
	 * 
	 * @param message
	 * @return
	 * @throws MyException
	 */
	private IoBuffer encodeProtocol(Object message) throws MyException {
		if (message instanceof ByteBuffer){
			return IoBuffer.wrap((ByteBuffer) message);
		} else if (message instanceof String){
			return IoBuffer.wrap(((String) message).getBytes());
		} else if (message instanceof byte[]){
			return IoBuffer.wrap((byte[]) message);
		} else {
			throw new MyException("protocol message illegality");
		}
	}
}

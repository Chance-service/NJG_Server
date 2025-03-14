package com.test.robot;

import java.net.InetSocketAddress;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.guaji.app.App;
import org.guaji.cryption.Decryption;
import org.guaji.cryption.Encryption;
import org.guaji.log.Log;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.GuaJiSession;
import org.guaji.net.NetStatistics;
import org.guaji.net.client.ClientSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.google.protobuf.AbstractMessage.Builder;
import com.guaji.game.protocol.HP;
import com.test.ServerTest;
import com.test.handler.IRespProtocolHandler;
import com.test.handler.ProtocolRespManager;
import com.test.net.NetManager;
import com.test.proto.IProtoGeneror;
import com.test.proto.ProtoGenerorManager;

public class Robot extends GuaJiSession{
	
	private volatile boolean isContainue;
	/**
	 * ���ID
	 */
	private int playerId;
	/**
	 * ���puid
	 */
	private String puid;

	/**
	 * �Ƿ��ѵ�¼
	 */
	private boolean isLogined;
	/**�Ƿ񴴽�����ɫ*/
	private boolean isCreated;
	private long lastHeatbeatTime;
	
	/**
	 * ���id
	 * @return
	 */
	public int getPlayerId() {
		return playerId;
	}

	/**
	 * ���id
	 * @param playerId
	 */
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	/**
	 * puid
	 * @return
	 */
	public String getPuid() {
		return puid;
	}
	
	/**
	 * puid
	 * @param puid
	 */
	public void setPuid(String puid) {
		this.puid = puid;
	}

	/**
	 * �Ƿ��¼��
	 * @return
	 */
	public boolean isLogined() {
		return isLogined;
	}
	
	/**
	 * �Ƿ��¼��
	 * @param isLogined
	 */
	public void setLogined(boolean isLogined) {
		this.isLogined = isLogined;
	}
	
	
	/**
	 * @return the isCreated
	 */
	public boolean isCreated() {
		return isCreated;
	}

	/**
	 * @param isCreated the isCreated to set
	 */
	public void setCreated(boolean isCreated) {
		this.isCreated = isCreated;
	}

	/**
	 * �ϴη�������ʱ��
	 * @return
	 */
	public long getLastHeatbeatTime() {
		return lastHeatbeatTime;
	}
	/**
	 * �ϴη�������ʱ��
	 * @param lastHeatbeatTime
	 */
	public void setLastHeatbeatTime(long lastHeatbeatTime) {
		this.lastHeatbeatTime = lastHeatbeatTime;
	}

	/**
	 * ��ʼ���ͻ��˻Ự
	 * 
	 * @param ip
	 * @param port
	 * @param timeoutMs
	 * @return
	 */
	public boolean connect(String ip, int port, int timeoutMs) {
		try {
			IoConnector connector = NetManager.getConnector();
			
			connector.setConnectTimeoutMillis(timeoutMs);
			ConnectFuture future = connector.connect(new InetSocketAddress(ip, port));
			future.awaitUninterruptibly();
			session = future.getSession();
			if (session != null) {
				super.onOpenedClientSide(session);
//				// ���ö�ȡ���ݵĻ�������С
//				session.getConfig().setReadBufferSize(GuaJiNetManager.getInstance().getSessionBufSize());
//				// �ӽ������
//				if (GuaJiNetManager.getInstance().enableEncryption()) {
//					setEncryption(new Encryption());
//					setDecryption(new Decryption());
//				}
//				// �󶨱��ػỰ����
//				this.session.setAttribute(GuaJiSession.SESSION_ATTR, this);
//				// ����������
//				inBuffer = IoBuffer.allocate(GuaJiNetManager.getInstance().getSessionBufSize()).setAutoExpand(true);
//				outBuffer = IoBuffer.allocate(GuaJiNetManager.getInstance().getSessionBufSize()).setAutoExpand(true);
//				Log.logPrintln("coneect cross server success , ip: " + ip + " ,port: " + port);
				isContainue = true;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.errPrintln("connect server exception , ip: " + ip + " ,port: " + port);
			MyException.catchException(e);
		}
		Log.errPrintln("connect server failed , ip: " + ip + " ,port: " + port);
		return true;
	}
	
	@Override
	public boolean onReceived(Object message) {
		// TODO Auto-generated method stub
		Protocol protocol = (Protocol)message;
		doRespCommand(protocol);
		return true;
	}
	
	public void doRespCommand(Protocol protocol){
		int command = protocol.getType();
		IRespProtocolHandler handler = ProtocolRespManager.getHandler(command);
		if(handler!=null){
			handler.handler(protocol, this);
		}
		//�յ����ݰ����ż���ִ�к���ָ��
		isContainue=true;
	}
	
	public void doReqCommand(){
		
		long curTime = GuaJiTime.getMillisecond();
		int command = HP.sys.HEART_BEAT_VALUE;
		//�������ָ��������
		if(!isContainue){
			return;
		}
		if(!isLogined()){
			//���͵�¼ָ��
			command = HP.code.LOGIN_C_VALUE;
		}else if(isLogined()&&!isCreated()){
			command = HP.code.ROLE_CREATE_C_VALUE;
		}else{
			if(curTime-lastHeatbeatTime>10000){
				//����������
				setLastHeatbeatTime(curTime);
				command = HP.sys.HEART_BEAT_VALUE;
			}else{
				try {
					if(command!= HP.code.LOGIN_C_VALUE||command != HP.code.ROLE_CREATE_C_VALUE){
						int sleep = ServerTest.RANDOM.nextInt(3000);
						Thread.sleep(500+sleep);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			
				int index = ServerTest.RANDOM.nextInt(ProtoGenerorManager.commands.length);
				command = ProtoGenerorManager.commands[index];
				System.out.println(String.format("机器人%d 发送  命令%d", this.getPlayerId(),command));
			}
		}
		IProtoGeneror generor = ProtoGenerorManager.getBuilder(command);
		byte[] build = generor.genBuilder(this);
		if(build==null){
			sendProtocol(Protocol.valueOf(command, "".getBytes()));
		}else{
			sendProtocol(Protocol.valueOf(command, build));
		}
		isContainue = false;
		
	}
}

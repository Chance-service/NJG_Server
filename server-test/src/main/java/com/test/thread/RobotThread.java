package com.test.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.test.ServerTest;
import com.test.robot.Robot;

/**
 * 机器人线程，每个线程中有若干个机器人 随机让机器人发送消息
 */
public class RobotThread implements Runnable {

	private List<Robot> robots;

	private int nums;
	
	private Random random = new Random();

	/**
	 * 机器人线程构造函数
	 */
	public RobotThread(int numsArg) {
		this.nums = numsArg;
		robots = new ArrayList<Robot>();
		for (int i = 0; i < nums; i++) {
			if(i%10==0){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Robot robot = new Robot();
			robot.setPuid(UUID.randomUUID().toString().replace("-", ""));
			robot.connect(ServerTest.ip, ServerTest.PORT, 500);
			robots.add(robot);
		}
	}

	/**
	 * 增加机器人
	 * 
	 * @param robot
	 */
	public void addRobot(Robot robot) {
		robots.add(robot);
	}

	@Override
	public void run() {
		// 随机取玩家执行指令
		while (true) {
			try {
				for(int i = 0;i<nums;i++){
					Robot robot = robots.get(i);
					robot.doReqCommand();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

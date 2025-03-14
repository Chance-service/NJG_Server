package com.server.paynotice.mapper;

import java.sql.Timestamp;
import java.util.List;

import com.server.paynotice.pojo.Snapshot;

public interface SnapshotMapper {
	/**
	 * 插入数据
	 * 
	 * @param action
	 * @param userid
	 * @return
	 */
	public int insertSnapshot(int action, String userid);

	/***
	 * 批量插入数据
	 * 
	 * @param snapshots
	 * @return
	 */
	public int insertSnapshots(List<Snapshot> snapshots);

	/***
	 * 删除days天前的数据
	 * 
	 * @param days
	 * @return
	 */
	public int delSnapshotByDays(int days);

	/**
	 * 查询lastTime前的数据
	 * 
	 * @param lastTime
	 * @return
	 */
	public List<Snapshot> querySnapshots(Timestamp lastTime);

	/***
	 * 通过userid查询快照
	 * 
	 * @param lastTime
	 * @param userid
	 * @return
	 */
	public List<Snapshot> queryUserSnapshots(Timestamp lastTime, String userid);
}

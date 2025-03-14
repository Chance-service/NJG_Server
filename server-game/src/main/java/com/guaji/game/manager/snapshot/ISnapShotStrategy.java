package com.guaji.game.manager.snapshot;

import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;

/**
 * The snapshot strategy;
 * 
 * @author crazyjohn
 *
 */
public interface ISnapShotStrategy {

	/**
	 * Init;
	 * 
	 * @return
	 */
	public boolean init();

	/**
	 * Do tick things;
	 * 
	 * @return
	 */
	public boolean onTick();

	/**
	 * Get the snapshot by playerId;
	 * 
	 * @param playerId
	 * @return
	 */
	public PlayerSnapshotInfo.Builder getSnapShot(int playerId);

	/**
	 * Set the snapshot with palyerId;
	 * 
	 * @param playerId
	 * @param builder
	 * @return
	 */
	public boolean setSnapShot(int playerId, PlayerSnapshotInfo.Builder builder);

	/**
	 * Handle alliance data changed;
	 * 
	 * @param allianceEntity
	 * @return
	 */
	public boolean onAllianceDataChanged(PlayerAllianceEntity allianceEntity);

	/**
	 * Handle player login;
	 * 
	 * @param playerId
	 */
	public void onPlayerLogin(int playerId);
}

package com.server.paynotice.mapper;

import java.util.List;

import com.server.paynotice.pojo.CancelUser;

public interface CancelUserMapper {
	/**
	 * 根据userid查询t_cancel_user中的数据
	 * 
	 * @param userid
	 * @return
	 */
	public List<CancelUser> queryUsersByUserid(String userid);

	/**
	 * 根据days查询符t_cancel_user中的数据
	 * 
	 * @param day
	 * @return
	 */
	public List<CancelUser> queryUsersByDays(int day);

	/**
	 * 插入t_cancel_user的数据
	 * 
	 * @param userid
	 * @return
	 */
	public int insertUser(String userid);

	/**
	 * 删除t_cancel_user中的数据
	 * 
	 * @param userid
	 * @return
	 */
	public int delUser(String userid);

	/**
	 * 批量删除t_cancel_user中的数据
	 * 
	 * @param ids
	 * @return
	 */
	public int delUsers(List<Integer> ids);
}

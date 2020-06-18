package com.dili.account.service;

import com.dili.account.dto.CardRequestDto;
import com.dili.account.dto.PasswordManageResponseDto;

/**
 * @description： 重置密码修改密码等
 *
 * @author ：WangBo
 * @time ：2020年4月26日下午5:52:54
 */
public interface IPasswordService {

	/**
	 * @author: xiaosa
	 * @date: 2020/4/27
	 * @param: passwordDto
	 * @return: success/fail
	 * @description：修改登陆密码
	 */
	void modifyLoginPwd(CardRequestDto cardRequestDto) throws Exception;

	/**
	 * @author: xiaosa
	 * @date: 2020/4/27
	 * @param: passwordDto
	 * @return: success/fail
	 * @description：重置登陆密码
	 */
	void resetLoginPwd(CardRequestDto cardRequestDto) throws Exception;

	/**
	 * 验证密码
	 * @param encryptPwd 加密密码
	 * @param loginPwd 明文
	 */
	void checkLoginPwd(String encryptPwd, String loginPwd);
}

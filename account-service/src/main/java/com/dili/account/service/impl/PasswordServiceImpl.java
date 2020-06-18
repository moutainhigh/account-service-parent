package com.dili.account.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dili.account.dao.IUserAccountDao;
import com.dili.account.dto.CardRequestDto;
import com.dili.account.entity.UserAccountDo;
import com.dili.account.service.IPasswordService;
import com.dili.account.util.PasswordUtils;
import com.dili.ss.exception.BusinessException;

/**
 * @author: xiaosa
 * @date: 2020/4/27
 * @description: 密码相关的操作
 */
@Service
public class PasswordServiceImpl implements IPasswordService{

	private static final Logger log = LoggerFactory.getLogger(PasswordServiceImpl.class);

	@Resource
	private IUserAccountDao userAccountDao;

	@Override
	public void modifyLoginPwd(CardRequestDto cardRequestDto) throws Exception {
		
	}

	@Override
	public void resetLoginPwd(CardRequestDto cardRequestDto) throws Exception {
		
		
	}

	@Override
	public void checkPasswor(Long accountId, String password) {
		if (accountId == null || StringUtils.isBlank(password)) {
			throw new BusinessException("9999999999","参数错误");
		}
		UserAccountDo userAccountDo = userAccountDao.getByAccountId(accountId);
		if (userAccountDo == null) {
			throw new BusinessException("9999999999","卡信息不存在");
		}
		String encryptPwd = PasswordUtils.encrypt(password, userAccountDo.getSecretKey());
		if (!userAccountDo.getLoginPwd().equals(encryptPwd)) {
			throw new BusinessException("9999999999","密码错误");
		}
	}
}
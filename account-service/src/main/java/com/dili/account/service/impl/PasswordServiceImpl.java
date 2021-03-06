package com.dili.account.service.impl;

import javax.annotation.Resource;

import com.dili.account.entity.CardAggregationWrapper;
import com.dili.account.exception.AccountBizException;
import com.dili.account.exception.ErrorCode;
import com.dili.account.service.IAccountQueryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.account.dao.IUserAccountDao;
import com.dili.account.dto.CardRequestDto;
import com.dili.account.entity.UserAccountDo;
import com.dili.account.service.IPasswordService;
import com.dili.account.util.PasswordUtils;
import com.dili.ss.constant.ResultCode;

/**
 * @author: xiaosa
 * @date: 2020/4/27
 * @description: 密码相关的操作
 */
@Service
public class PasswordServiceImpl implements IPasswordService {

	public static final Logger log = LoggerFactory.getLogger(PasswordServiceImpl.class);

	@Resource
	private IUserAccountDao userAccountDao;
	@Autowired
	private IAccountQueryService accountQueryService;

	@Override
	public void modifyLoginPwd(CardRequestDto cardRequestDto) throws Exception {

	}

	@Override
	public void resetLoginPwd(CardRequestDto cardRequestDto) throws Exception {
		CardAggregationWrapper account = accountQueryService.getByAccountIdForGenericOp(cardRequestDto.getAccountId());
		if (!cardRequestDto.getLoginPwd().equals(cardRequestDto.getSecondLoginPwd())) {
			throw new AccountBizException(ResultCode.DATA_ERROR, "两次输入密码不匹配");
		}
		UserAccountDo userAccount = new UserAccountDo();
		userAccount.setId(account.getUserAccount().getId());
		userAccount.setAccountId(cardRequestDto.getAccountId());
		userAccount.setLoginPwd(
				PasswordUtils.encrypt(cardRequestDto.getLoginPwd(), account.getUserAccount().getSecretKey()));
		int update = userAccountDao.update(userAccount);
		if(update == 0) {
			throw new AccountBizException(ResultCode.DATA_ERROR, "密码重置失败");
		}
	}

	@Override
	public void checkPassword(Long accountId, String password) {
		if (accountId == null || StringUtils.isBlank(password)) {
			throw new AccountBizException(ResultCode.DATA_ERROR, "密码校验参数错误");
		}
		UserAccountDo userAccountDo = userAccountDao.getByAccountId(accountId);
		if (userAccountDo == null) {
			throw new AccountBizException(ResultCode.DATA_ERROR, "卡信息不存在");
		}
		String encryptPwd = PasswordUtils.encrypt(password, userAccountDo.getSecretKey());
		if (!userAccountDo.getLoginPwd().equals(encryptPwd)) {
			log.info("loginPwd[{}],SecretKey[{}],encryptPwd[{}]", userAccountDo.getLoginPwd(),
					userAccountDo.getSecretKey(), encryptPwd);
			throw new AccountBizException(ErrorCode.PASSWORD_ERROR, "密码错误");
		}
	}
}

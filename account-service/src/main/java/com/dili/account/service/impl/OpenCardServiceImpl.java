package com.dili.account.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.account.common.BizNoServiceType;
import com.dili.account.dto.FundAccountDto;
import com.dili.account.dto.OpenCardDto;
import com.dili.account.dto.OpenCardResponseDto;
import com.dili.account.dto.UserAccountCardQuery;
import com.dili.account.dto.UserAccountCardResponseDto;
import com.dili.account.entity.CardStorageDo;
import com.dili.account.entity.UserAccountDo;
import com.dili.account.entity.UserCardDo;
import com.dili.account.exception.AccountBizException;
import com.dili.account.rpc.UidRpc;
import com.dili.account.rpc.resolver.PayRpcResolver;
import com.dili.account.rpc.resolver.UidRpcResovler;
import com.dili.account.service.IAccountQueryService;
import com.dili.account.service.ICardStorageService;
import com.dili.account.service.IOpenCardService;
import com.dili.account.type.AccountType;
import com.dili.account.type.AccountUsageType;
import com.dili.account.type.CardType;
import com.dili.account.type.CustomerType;
import com.dili.account.type.UsePermissionType;
import com.dili.ss.domain.BaseOutput;
import com.google.common.collect.Lists;

/**
 * @description： 开卡service实现
 * 
 * @author ：WangBo
 * @time ：2020年6月19日下午5:54:23
 */
@Service("openCardService")
public class OpenCardServiceImpl implements IOpenCardService {

	@Resource
	private ICardStorageService ICardStorageService;
	@Resource
	private IAccountQueryService accountQueryService;
	@Resource
	private UidRpcResovler uidRpcResovler;
	@Resource
	private PayRpcResolver payRpcResolver;
//	@Resource
//	private IUserAccountDao userAccountDao;
//	@Resource
//	private IUserCardDao userCardDao;
//	@Resource
//	private IUserLegalDao userLegalDao;
//	@Resource
//	private IUserAccountCardDao userAccountCardDao;
//	@Autowired
//	private KeyGeneratorManager keyGeneratorManager;
//	@Resource
//	private CrmRpc crmRpc;
//	@Resource
//	private PayRpc payRpc;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public OpenCardResponseDto openMasterCard(OpenCardDto openCardInfo) {
		// 判断卡状态是否异常
		UserAccountCardResponseDto cardExists = accountQueryService.getByCardNoForRest(openCardInfo.getCardNo());
		if (cardExists != null) {
			throw new AccountBizException("该卡{}已被{}使用，不能开卡!", openCardInfo.getCardNo(), cardExists.getAccountId());
		}
		// 判断客户是否已办理过主卡，寿光每个人只能有一张交易主卡,其它市场允许办两张卡，则判断只能一张交易买家卡和一张交易卖家卡
		UserAccountCardQuery queryParam = new UserAccountCardQuery();
		queryParam.setCustomerIds(Lists.newArrayList(openCardInfo.getCustomerId()));
		List<UserAccountCardResponseDto> userAccountList = accountQueryService.getListByConditionForRest(queryParam);
		if (userAccountList.size() > 0) {
			// TODO 是否允许两张卡
			throw new AccountBizException("客户{}已办理过交易主卡{}", openCardInfo.getName(), userAccountList.get(0).getCardNo());
		}
		// 判断卡类型，并将卡片改为使用中
		CardStorageDo cardStorageDo = ICardStorageService.inUse(openCardInfo.getCardNo());
		if (cardStorageDo.getType() != CardType.MASTER.getCode()) {
			throw new AccountBizException("该卡{}不是主卡，操作失败!", openCardInfo.getCardNo());
		}

		// 创建资金账户
		FundAccountDto fundAccount = new FundAccountDto();
		String fundAccountIdStr = payRpcResolver.createFundAccount(fundAccount);
		Long fundAccountId = Long.parseLong(fundAccountIdStr);

		// 构建账户信息
//		String accountIdStr = uidRpcResovler.bizNumberRetry(BizNoServiceType.ACCOUNT_ID, 3);
//		Long accountId = Long.parseLong(accountIdStr);
//		UserAccountDo userAccount = buildUserAccount(openCardInfo, accountId, fundAccountId);
//
//		// 保存卡片信息
//		UserCardEntity userCard = buildUserCard(openCardInfo, userAccount.getId());
//		userCardDao.save(userCard);
//
//		// 保存账户信息
//		userAccount.setLatestCardId(userCard.getId());
//		userAccountDao.save(userAccount);
//
//		// 保存使用额度权限
//		UserAmountLimitEntity userLimit = buildUserLimit(openCardInfo, userAccount.getId());
//		amountLimitService.save(userLimit);

		// 返回数据
		OpenCardResponseDto response = new OpenCardResponseDto();
//		response.setAccountId(userAccount.getId());
		return null;
	}

	@Override
	public OpenCardResponseDto openSlaveCard(OpenCardDto openCardInfo) {
		// TODO Auto-generated method stub
		return null;
	}

//
//	@Override
//	public List<UserInfoEntity> listUser() {
//		return userInfoDao.selectList(new UserInfoEntity());
//	}
//
//	@Override
//	@Transactional(rollbackFor = Exception.class, readOnly = false)
//	public OpenCardResponseDto openMasterCard(OpenCardDto openCardInfo) {
//		// 判断卡状态是否异常
//		UserCardEntity userCardParam = new UserCardEntity();
//		userCardParam.setCardNo(openCardInfo.getCardNo());
//		UserCardEntity cardExists = userCardDao.getByIdCardNo(userCardParam);
//		if (cardExists != null) {
//			throw new AppException("该卡{}已被{}使用，不能开卡!", openCardInfo.getCardNo(), cardExists.getAccountId());
//		}
//		// 判断客户是否已办理过主卡，寿光每个人只能有一张交易主卡,其它市场允许办两张卡，则判断只能一张交易买家卡和一张交易卖家卡
//		UserAccountCardQuery param = new UserAccountCardQuery();
//		param.setCrmCustormerId(openCardInfo.getCrmCustormerId());
//		param.setUsePermission(UsePermissionType.TRANSACTION.getCode() + ",");
//		param.setCardCategorys(CardCategory.getMasterList());
//		List<UserAccountCardDto> userAccountList = userAccountCardDao.selectList(param);
//		if (userAccountList.size() > 0) {
//			// TODO 是否允许两张卡
//			throw new AppException("客户{}已办理过交易主卡{}", openCardInfo.getName(), userAccountList.get(0).getCardNo());
//		}
//		// 判断卡类型，并将卡片改为使用中
//		UserCardRepositoryEntity cardRep = cardRepositoryService.inUse(openCardInfo.getCardNo());
//		if (cardRep.getCategory() != CardCategory.MASTER.getCode()) {
//			throw new AppException("该卡{}不是主卡，操作失败!", openCardInfo.getCardNo());
//		}
//
//		// 对公户保存法人信息
//		UserLegalEntity userLegal = new UserLegalEntity();
//		if (AccountType.PUBLIC.getCode() == openCardInfo.getAccountType()) {
//			userLegal = buildUserLegal(openCardInfo);
//			userLegalDao.save(userLegal);
//		}
//		// 保存用户信息，如果已有用户信息则使用原信息
//		UserInfoEntity queryParam = new UserInfoEntity();
//		queryParam.setCrmCustormerId(openCardInfo.getCrmCustormerId());
//		UserInfoEntity userInfo = userInfoDao.getOnlyOne(queryParam);
//		if (userInfo == null) {
//			userInfo = buildUserInfo(openCardInfo);
//			userInfo.setLegalId(userLegal.getId());
//			userInfoDao.save(userInfo);
//		}
//		// 构建账户信息
//		UserAccountEntity userAccount = buildUserAccount(openCardInfo, userInfo.getId());
//
//		// 保存卡片信息
//		UserCardEntity userCard = buildUserCard(openCardInfo, userAccount.getId());
//		userCardDao.save(userCard);
//
//		// 保存账户信息
//		userAccount.setLatestCardId(userCard.getId());
//		userAccountDao.save(userAccount);
//
//		// 保存使用额度权限
//		UserAmountLimitEntity userLimit = buildUserLimit(openCardInfo, userAccount.getId());
//		amountLimitService.save(userLimit);
//
//		// 返回数据
//		OpenCardResponseDto response = new OpenCardResponseDto();
//		response.setAccountId(userAccount.getId());
//		return response;
//	}
//
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public OpenCardResponseDto openSlaveCard(OpenCardDto openCardInfo) {
//		// 判断卡状态是否异常
//		UserCardEntity userCardParam = new UserCardEntity();
//		userCardParam.setCardNo(openCardInfo.getCardNo());
//		UserCardEntity cardExists = userCardDao.getByIdCardNo(userCardParam);
//		if (cardExists != null) {
//			throw new AppException("该卡{}处于{}使用状态，请联系管理员!", openCardInfo.getCardNo(), cardExists.getAccountId());
//		}
//
//		// 卡片改为使用中
//		UserCardRepositoryEntity cardRep = cardRepositoryService.inUse(openCardInfo.getCardNo());
//		if (cardRep.getCategory() != CardCategory.SLAVE.getCode()) {
//			throw new AppException("该卡{}不是副卡，操作失败!", openCardInfo.getCardNo());
//		}
//
//		// 获取主卡用户信息
//		UserAccountEntity parentAccount = userAccountDao.getById(openCardInfo.getParentAccountId());
//		if (parentAccount == null) {
//			throw new AppException("{}主卡账户{}不存在!", openCardInfo.getCardNo(), openCardInfo.getParentAccountId());
//		}
//
//		// 构建账户信息，使用主卡的用户信息
//		UserAccountEntity userAccount = buildUserAccount(openCardInfo, parentAccount.getUserInfoId());
//
//		// 保存卡片信息
//		UserCardEntity userCard = buildUserCard(openCardInfo, userAccount.getId());
//		userCardDao.save(userCard);
//
//		// 保存账户信息
//		userAccount.setParentAccountId(parentAccount.getId());
//		userAccount.setLatestCardId(userCard.getId());
//		userAccountDao.save(userAccount);
//
//		// 保存使用额度权限
//		UserAmountLimitEntity userLimit = buildUserLimit(openCardInfo, userAccount.getId());
//		amountLimitService.save(userLimit);
//
//		// 返回数据
//		OpenCardResponseDto response = new OpenCardResponseDto();
//		response.setAccountId(userAccount.getId());
//		return response;
//	}
//
//	/**
//	 * 构建开主卡用户信息
//	 */
//	private UserInfoEntity buildUserInfo(OpenCardDto openCardInfo) {
//		UserInfoEntity userInfo = new UserInfoEntity();
//		IKeyGenerator userInfoKey = keyGeneratorManager.getKeyGenerator(SequenceKey.USER_INFO);
//		Long userInfoId = userInfoKey.nextId();
//		userInfo.setName(openCardInfo.getName());
//		userInfo.setMobile(openCardInfo.getMobile());
//		userInfo.setId(userInfoId);
//		userInfo.setCrmCustormerId(openCardInfo.getCrmCustormerId());
//		userInfo.setAddress(openCardInfo.getAddress());
//		userInfo.setAuthStatus(AuthStatus.AUTH.getCode());
//		userInfo.setAuthTime(LocalDateTime.now());
//		userInfo.setBusinessCategory(openCardInfo.getBusinessCategory());
//		userInfo.setCredentialNo(openCardInfo.getCredentialNo());
//		userInfo.setCredentialType(openCardInfo.getCredentialType());
//		userInfo.setCustormerType(openCardInfo.getCustormerType());
//		userInfo.setCustomerArea(openCardInfo.getCustomerArea());
//		userInfo.setGender(openCardInfo.getGender());
//		LocalDateTime now = LocalDateTime.now();
//		userInfo.setCreatedTime(now);
//		userInfo.setModifiedTime(now);
//		userInfo.setMarketId(openCardInfo.getMarketId());
//		return userInfo;
//	}
//
	/**
	 * 构建卡账户数据
	 *
	 * @throws Exception
	 */
	private UserAccountDo buildUserAccount(OpenCardDto openCardInfo, Long accountId, Long fundAccountId) {
		UserAccountDo userAccount = new UserAccountDo();
		userAccount.setAccountId(accountId);
		userAccount.setParentAccountId(openCardInfo.getParentAccountId());
		userAccount.setCustomerId(openCardInfo.getCustomerId());
		userAccount.setFundAccountId(fundAccountId);

		if (CustomerType.PURCHASE.getCode().equalsIgnoreCase(openCardInfo.getType())) {
			userAccount.setType(AccountType.PURCHASE.getCode());
			userAccount.setUsageType(AccountUsageType.TRADE.getCode()+",");
			Integer[] codes = { UsePermissionType.RECHARGE.getCode(), 
								UsePermissionType.WEALTH.getCode(),
								UsePermissionType.TRANSACTION.getCode(),
								UsePermissionType.PAY_FEES.getCode()};
			userAccount.setPermissions(UsePermissionType.getPermissions(codes));
		} else if (CustomerType.SALE.getCode().equalsIgnoreCase(openCardInfo.getType())) {
			userAccount.setType(AccountType.SALE.getCode());
			userAccount.setUsageType(AccountUsageType.TRADE.getCode()+",");
			Integer[] codes = { UsePermissionType.RECHARGE.getCode(), 
								UsePermissionType.WEALTH.getCode(),
								UsePermissionType.TRANSACTION.getCode(),
								UsePermissionType.PAY_FEES.getCode()};
			userAccount.setPermissions(UsePermissionType.getPermissions(codes));
		} else if (CustomerType.DRIVER.getCode().equalsIgnoreCase(openCardInfo.getType())) {
			
		}

//		userAccount.setAccountType(openCardInfo.getCustormerType());
//		userAccount.setBizUsageType(openCardInfo.getBizUsageType());
//		userAccount.setCreateSource(openCardInfo.getCreateSource());
//		userAccount.setFundAccountId(openCardInfo.getFundAccountId());
//		userAccount.setMarketId(openCardInfo.getMarketId());
//		userAccount.setLoginPwd(PasswordUtils.encrypt(openCardInfo.getLoginPwd(), Constant.LOGIN_PWD_SECRETKEY));
//		userAccount.setTradePwd(PasswordUtils.encrypt(openCardInfo.getTradePwd(), Constant.TRADE_PWD_SECRETKEY));
//		userAccount.setUsePermission(openCardInfo.getUsePermission() + ",");
//		LocalDateTime now = LocalDateTime.now();
//		userAccount.setModifiedTime(now);
//		userAccount.setCreatedTime(now);
		return userAccount;
	}

	/**
	 * 构建卡片数据
	 */
	private UserCardDo buildUserCard(OpenCardDto openCardInfo, Long userAccountId) {
		UserCardDo userCard = new UserCardDo();
//		IKeyGenerator userCardKey = keyGeneratorManager.getKeyGenerator(SequenceKey.USER_CARD);
//		userCard.setId(userCardKey.nextId());
//		userCard.setAccountId(userAccountId);
//		// 如果是电子卡则生成电子卡号,实体卡则使用卡片上的卡号
//		userCard.setCardNo(openCardInfo.getCardNo());
//		if (openCardInfo.getSeinsweise() == CardType.ELEC_CARD.getCode()) {
//			// TODO 生成电子卡号
//		}
//		userCard.setCategory(openCardInfo.getCategory());
//		userCard.setMarketId(openCardInfo.getMarketId());
//		userCard.setStatus(CardStatus.NORMAL.getCode());
//		userCard.setSeinsweise(openCardInfo.getSeinsweise());
//		LocalDateTime now = LocalDateTime.now();
//		userCard.setCreatedTime(now);
//		userCard.setModifiedTime(now);
//		userCard.setSystemDisableStatus(SystemDisableStatus.ENABLED.getCode());
		return userCard;
	}
}

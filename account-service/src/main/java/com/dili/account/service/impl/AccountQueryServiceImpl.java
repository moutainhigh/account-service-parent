package com.dili.account.service.impl;

import com.dili.account.common.ExceptionMsg;
import com.dili.account.dao.IUserAccountCardDao;
import com.dili.account.dao.IUserAccountDao;
import com.dili.account.dao.IUserCardDao;
import com.dili.account.dto.AccountSimpleResponseDto;
import com.dili.account.dto.AccountWithAssociationResponseDto;
import com.dili.account.dto.BalanceResponseDto;
import com.dili.account.dto.UserAccountCardQuery;
import com.dili.account.dto.UserAccountCardResponseDto;
import com.dili.account.entity.CardAggregationWrapper;
import com.dili.account.entity.UserAccountDo;
import com.dili.account.entity.UserCardDo;
import com.dili.account.exception.AccountBizException;
import com.dili.account.rpc.resolver.PayRpcResolver;
import com.dili.account.service.IAccountQueryService;
import com.dili.account.type.AccountUsageType;
import com.dili.account.type.CardType;
import com.dili.account.type.DisableState;
import com.dili.account.type.UsePermissionType;
import com.dili.account.util.PageUtils;
import com.dili.account.validator.AccountValidator;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dili.account.validator.AccountValidator.ALL;
import static com.dili.account.validator.AccountValidator.NONE;

/**
 * @description： 用户账户信息查询service实现
 *
 * @author ：WangBo
 * @time ：2020年4月22日下午5:53:40
 */
@Service("accountQueryService")
public class AccountQueryServiceImpl implements IAccountQueryService {
    @Autowired
    private IUserCardDao userCardDao;
    @Autowired
    private IUserAccountCardDao userAccountCardDao;
    @Autowired
    private PayRpcResolver payRpcResolver;


    @Override
    public Boolean cardExist(String cardNo) {
        return userCardDao.getByCardNo(cardNo, 0) != null;
    }

    @Override
    public AccountSimpleResponseDto getByCardNoWithBalance(String cardNo) {
        UserAccountCardQuery query = new UserAccountCardQuery();
        query.setCardNos(Lists.newArrayList(cardNo));
        UserAccountCardResponseDto userAccount = this.getSingleForRest(query);
        BalanceResponseDto fund = payRpcResolver.findBalanceByFundAccountId(userAccount.getFundAccountId());
        return new AccountSimpleResponseDto(fund, userAccount);
    }

    @Override
    public UserAccountCardResponseDto getSingleForRest(UserAccountCardQuery queryParam) {
        return this.getSingleForRest(queryParam, ALL);
    }

    @Override
    public UserAccountCardResponseDto getSingleForRest(UserAccountCardQuery queryParam, int validateFlag) {
        queryParam.setExcludeDisabled(0);
        queryParam.setExcludeReturn(0);
        PageHelper.startPage(1, 1, false);
        List<UserAccountCardResponseDto> list = this.getListByConditionForRest(queryParam);
        if (CollectionUtils.isEmpty(list)) {
            throw new AccountBizException(ResultCode.DATA_ERROR, ExceptionMsg.ACCOUNT_NOT_EXIST.getName());
        }
        UserAccountCardResponseDto responseDto = list.get(0);
        AccountValidator.validateAccount(responseDto, validateFlag);
        return responseDto;
    }

    @Override
    public AccountWithAssociationResponseDto getSingleWithAssociationForRest(UserAccountCardQuery queryParam) {
        UserAccountCardResponseDto primaryCard = this.getSingleForRest(queryParam, NONE);
        AccountWithAssociationResponseDto result = new AccountWithAssociationResponseDto();
        //查询关联卡，primaryCard为主卡就查副卡，副卡就查主卡
        UserAccountCardQuery param = new UserAccountCardQuery();
        if (CardType.isMaster(primaryCard.getCardType())) {
            param.setParentAccountId(primaryCard.getAccountId());
        } else if (CardType.isSlave(primaryCard.getCardType())) {
            param.setAccountIds(Lists.newArrayList(primaryCard.getParentAccountId()));
        }
        param.setExcludeReturn(0);
        param.setExcludeDisabled(0);
        List<UserAccountCardResponseDto> associationCards = this.getListByConditionForRest(param);
        result.setPrimary(primaryCard);
        result.setAssociation(associationCards);
        return result;
    }


    @Override
    public List<UserAccountCardResponseDto> getListByConditionForRest(UserAccountCardQuery queryParam) {
        //最多查询50条数据，防止爆库
        PageHelper.startPage(1, 50, false);
        List<CardAggregationWrapper> list = this.getWrapperList(queryParam);
        return list.stream().map(wrapper -> this.convertFromAccountUnionCard(
                wrapper.getUserCard(),
                wrapper.getUserAccount()))
                .collect(Collectors.toList());
    }


    @Override
    public PageOutput<List<UserAccountCardResponseDto>> getPageByConditionForRest(UserAccountCardQuery param) {
        Page<?> page = PageHelper.startPage(param.getPage(), param.getRows());
        List<CardAggregationWrapper> wrapperList = this.getWrapperList(param);
        List<UserAccountCardResponseDto> result = wrapperList.stream().map(wrapper -> this.convertFromAccountUnionCard(
                wrapper.getUserCard(),
                wrapper.getUserAccount()))
                .collect(Collectors.toList());
        return PageUtils.convert2PageOutput(page, result);
    }

    @Override
    public CardAggregationWrapper getSingle(UserAccountCardQuery queryParam) {
        queryParam.setExcludeDisabled(0);
        queryParam.setExcludeReturn(0);
        List<CardAggregationWrapper> list = this.getWrapperList(queryParam);
        if (CollectionUtils.isEmpty(list)) {
            throw new AccountBizException(ResultCode.DATA_ERROR, ExceptionMsg.ACCOUNT_NOT_EXIST.getName());
        }
        return list.get(0);
    }

    @Override
    public CardAggregationWrapper getByAccountIdForCardOp(Long accountId) {
        UserAccountCardQuery query = new UserAccountCardQuery();
        query.setAccountIds(Lists.newArrayList(accountId));
        CardAggregationWrapper wrapper = this.getSingle(query);
        UserAccountDo userAccount = wrapper.getUserAccount();
        UserCardDo userCard = wrapper.getUserCard();
        if (DisableState.DISABLED.getCode().equals(userAccount.getDisabledState())) {
            throw new AccountBizException(ResultCode.DATA_ERROR, ExceptionMsg.ACCOUNT_DISABLED.getName());
        }
        //如果是副卡，查询主卡状态
        if (CardType.isSlave(userCard.getType())) {
            query.setAccountIds(Lists.newArrayList(userAccount.getParentAccountId()));
            CardAggregationWrapper parentAccount = this.getSingle(query);
            if (DisableState.DISABLED.getCode().equals(parentAccount.getUserAccount().getDisabledState())) {
                throw new AccountBizException(ResultCode.DATA_ERROR, ExceptionMsg.ACCOUNT_DISABLED.getName());
            }
        }
        return wrapper;
    }

    @Override
    public Optional<CardAggregationWrapper> getByAccountId(Long accountId) {
        UserAccountCardQuery query = new UserAccountCardQuery();
        query.setAccountIds(Lists.newArrayList(accountId));
        query.setExcludeReturn(0);
        query.setExcludeDisabled(0);
        List<CardAggregationWrapper> wrapperList = this.getWrapperList(query);
        if (wrapperList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(wrapperList.get(0));
    }


    private List<CardAggregationWrapper> getWrapperList(UserAccountCardQuery queryParam) {
        //设置默认排序字段，避免xml写太多判断
        //默认排除退还状态和禁用状态
        queryParam.setDefSort("card_create_time")
                .setDefOrder("DESC");
        queryParam.setDefExcludeReturn(1)
                .setDefExcludeDisabled(1);
        return userAccountCardDao.getListByCondition(queryParam);
    }


    private UserAccountCardResponseDto convertFromAccountUnionCard(UserCardDo card, UserAccountDo account) {
        UserAccountCardResponseDto responseDto = new UserAccountCardResponseDto();
        responseDto.setCardId(card.getId());
        responseDto.setCardType(card.getType());
        responseDto.setCardNo(card.getCardNo());
        responseDto.setCardState(card.getState());
        responseDto.setUsageType(AccountUsageType.getUsageTypeList(account.getUsageType()));
        responseDto.setCardCreateTime(card.getCreateTime());
        responseDto.setCreator(card.getCreator());
        responseDto.setCreatorId(card.getCreatorId());
        responseDto.setFirmId(account.getFirmId());
        responseDto.setAccountId(account.getAccountId());
        responseDto.setFundAccountId(account.getFundAccountId());
        responseDto.setPermissionList(UsePermissionType.getPermissionList(account.getPermissions()));
        responseDto.setParentAccountId(account.getParentAccountId());
        responseDto.setAccountType(account.getType());
        responseDto.setAccountState(account.getState());
        responseDto.setDisabledState(account.getDisabledState());

        responseDto.setCustomerId(account.getCustomerId());
        responseDto.setCustomerName(account.getName());
        responseDto.setCustomerCode(account.getCode());
        responseDto.setCustomerCellphone(account.getCellphone());
        responseDto.setCustomerCertificateNumber(account.getCertificateNumber());
        responseDto.setCustomerCertificateType(account.getCertificateType());
        return responseDto;
    }


}

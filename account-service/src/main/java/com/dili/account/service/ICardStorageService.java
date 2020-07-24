package com.dili.account.service;

import com.dili.account.dto.CardAddStorageDto;
import com.dili.account.dto.CardRepoQueryParam;
import com.dili.account.entity.CardStorageDo;
import com.dili.ss.domain.PageOutput;

import java.util.List;

/**
 * @description：
 *          卡片入库管理
 * @author ：WangBo
 * @time ：2020年6月17日下午5:20:23
 */
public interface ICardStorageService {

    /**
     * 查询列表数据
     */
    PageOutput<List<CardStorageDo>> listPage(CardRepoQueryParam queryParam);

    /**
     * 卡片首次入库激活
     */
    void addCard(CardAddStorageDto cardAddInfo);
    
    /**
     * 批量，卡片首次入库激活
     */
    void batchAddCard(List<CardAddStorageDto> cardAddInfoList);

    /**
     * 使用过后更新为激活,表示可以被开卡
     */
    CardStorageDo activateCard(String cardNo);

    /**
     * 更新为使用中，开卡后更新为使用中
     */
    CardStorageDo inUse(String cardNo);

    /**
     * 更新为作废
     */
    void voidCard(String cardNo, String remark);

    /**
     * 根据卡号修改状态
     * <br><i>只支持修改状态，修改时间为当前时间
     * @param cardStorage
     * @return
     */
    int updateByCardNo(CardStorageDo cardStorage);

    /**
     * 根据卡号查询
     */
    CardStorageDo getByCardNo(String cardNo);

}

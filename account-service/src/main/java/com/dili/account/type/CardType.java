package com.dili.account.type;

/**
 * @description： 卡片类型
 *
 * @author ：WangBo
 * @time ：2020年4月24日上午9:58:20
 */
public enum CardType {
    MASTER("主卡", 10),

    SLAVE("副卡", 20),

    ANONYMOUS("匿名卡", 30),

    UNION("联营卡", 40),

    BANK("银行卡", 60);

    private String name;
    private int code;

    CardType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static CardType getCardCategory(int code) {
        for (CardType category : CardType.values()) {
            if (category.getCode() == code) {
                return category;
            }
        }
        return null;
    }

    /**
     * 判断是否为主卡
     * @author miaoguoxin
     * @date 2020/6/28
     */
    public static boolean isMaster(int code) {
        return code == CardType.MASTER.getCode()
                || code == CardType.UNION.getCode()
                || code == CardType.BANK.getCode();
    }

    /**
     * 判断是否为副卡
     * @author miaoguoxin
     * @date 2020/6/28
     */
    public static boolean isSlave(int code) {
        return code == CardType.SLAVE.getCode();
    }

    public static String getName(int code) {
        for (CardType category : CardType.values()) {
            if (category.getCode() == code) {
                return category.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

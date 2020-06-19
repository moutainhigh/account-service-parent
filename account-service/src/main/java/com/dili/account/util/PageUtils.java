package com.dili.account.util;

import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;

/**
 * @Auther: miaoguoxin
 * @Date: 2020/6/19 14:51
 * @Description:
 */
public class PageUtils {

    public static  <T> PageOutput<T> convert2PageOutput(Page page, T result) {
        PageOutput<T> pageOutput = new PageOutput<>();
        pageOutput.setPageNum(page.getPageNum());
        pageOutput.setPageSize(page.getPageSize());
        pageOutput.setTotal((int) page.getTotal());
        pageOutput.setData(result);
        return pageOutput;
    }
}

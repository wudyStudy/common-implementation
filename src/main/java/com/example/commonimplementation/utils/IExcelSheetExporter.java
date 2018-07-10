package com.example.commonimplementation.utils;

import java.util.List;

/**
 */
public interface IExcelSheetExporter<T> {

    /**
     * 获取Sheet的Header
     *
     * @return
     */
    String[] getHeader();

    /**
     * 获取Sheet的列对应的字段名称
     *
     * @return
     */
    String[] getFieldNames();

    /**
     * 获取sheet名称
     *
     * @return
     */
    String getSheetName();

    /**
     * 获取数据源
     *
     * @return
     */
    List<T> getDataSet();

    /**
     * 设置数据源
     */
    void setDataSet(List<T> data);

}

package com.meiqi.app.pojo;

import java.util.ArrayList;
import java.util.List;

import com.meilele.datalayer.common.data.builder.ColumnKey;

public class Commission {
    /*
     * 未结算 0 已结算1 结算状态 所有2
     */
    private long              status    = 0;

    private String            createTimeStart;

    private String            createTimeEnd;

    private String            totalPrice;

    private List<Commissions> months    = new ArrayList<Commissions>(); // 按月份分组存CommissionInfo

    /*
     * 以下字段用于设计师个人信息页面佣金信息 规则：COMM_HSV1_CommissionInfo C-user_id
     * I-NotCheckoutPrice J-HistoryIncome K-NewIncome M-newIncomeTime
     */
    @ColumnKey(value = "newIncomeTime")
    private String            newIncomeTime;

    @ColumnKey(value = "HistoryIncome")
    private String            historyIncome;

    @ColumnKey(value = "NotCheckoutPrice")
    private String            notCheckoutPrice;

    @ColumnKey(value = "NewIncome")
    private String            newIncome;

    private int               pageIndex = 0;
    private int               pageSize  = 0;



    public long getStatus() {
        return status;
    }



    public void setStatus(long status) {
        this.status = status;
    }



    public String getCreateTimeStart() {
        return createTimeStart;
    }



    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }



    public String getCreateTimeEnd() {
        return createTimeEnd;
    }



    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }



    public String getTotalPrice() {
        return totalPrice;
    }



    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }



    public List<Commissions> getMonths() {
        return months;
    }



    public void setMonths(List<Commissions> months) {
        this.months = months;
    }



    public void addCommissionInfo(CommissionInfo commissionInfo) {
        String month = commissionInfo.getMonth();
        List<CommissionInfo> commissionInfoList = this.getCommissionInfoListByMonth(month);
        if (null == commissionInfoList) { // 如果commissionInfo对应月无记录，则建立对应月的对象
            Commissions commissions = new Commissions();
            commissions.setMonth(month);
            months.add(commissions);

            commissionInfoList = commissions.getCommissionInfoList();
        }

        commissionInfoList.add(commissionInfo);
    }



    private List<CommissionInfo> getCommissionInfoListByMonth(String month) {
        for (Commissions commissions : months) {
            String tmpMonth = commissions.getMonth();
            if (tmpMonth != null && tmpMonth.equals(month)) {
                return commissions.getCommissionInfoList();
            }
        }

        return null;
    }



    public String getNewIncomeTime() {
        return newIncomeTime;
    }



    public void setNewIncomeTime(String newIncomeTime) {
        this.newIncomeTime = newIncomeTime;
    }



    public String getHistoryIncome() {
        return historyIncome;
    }



    public void setHistoryIncome(String historyIncome) {
        this.historyIncome = historyIncome;
    }



    public String getNotCheckoutPrice() {
        return notCheckoutPrice;
    }



    public void setNotCheckoutPrice(String notCheckoutPrice) {
        this.notCheckoutPrice = notCheckoutPrice;
    }



    public String getNewIncome() {
        return newIncome;
    }



    public void setNewIncome(String newIncome) {
        this.newIncome = newIncome;
    }



    public int getPageIndex() {
        return pageIndex;
    }



    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }



    public int getPageSize() {
        return pageSize;
    }



    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}

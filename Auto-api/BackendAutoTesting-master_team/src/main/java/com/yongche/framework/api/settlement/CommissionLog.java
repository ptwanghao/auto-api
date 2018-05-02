package com.yongche.framework.api.settlement;

import java.util.Map;

public class CommissionLog {

    private String distribute_commission_log_id;
    private String driver_id;
    private int driver_type;
    private String service_order_id;
    private int company_id;
    private int labour_company_id;
    private int product_type_id;
    private int car_type_id;
    private int yidao_ratio;
    private int company_commission_ratio;
    private int labour_company_commission_ratio;
    private int driver_commission_ratio;
    private int fund_commission_ratio;
    private int commission_ratio;
    private int distribute_amount;
    private int distribute_yidao_amount;
    private int distribute_company_amount;
    private int distribute_labour_company_amount;
    private int distribute_driver_amount;
    private int distribute_fund_amount;
    private int company_receipt_amount;
    private int labour_company_receipt_amount;
    private int driver_laowu_amount;
    private int type;
    private int status;

    public String getDistribute_commission_log_id() {
        return distribute_commission_log_id;
    }

    public void setDistribute_commission_log_id(String distribute_commission_log_id) {
        this.distribute_commission_log_id = distribute_commission_log_id;
    }

    public int getLabour_company_commission_ratio() {
        return labour_company_commission_ratio;
    }

    public void setLabour_company_commission_ratio(int labour_company_commission_ratio) {
        this.labour_company_commission_ratio = labour_company_commission_ratio;
    }

    public void setDistribute_amount(int distribute_amount) {
        this.distribute_amount = distribute_amount;
    }

    public int getDistribute_driver_amount() {
        return distribute_driver_amount;
    }

    public void setDistribute_driver_amount(int distribute_driver_amount) {
        this.distribute_driver_amount = distribute_driver_amount;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public int getDriver_type() {
        return driver_type;
    }

    public void setDriver_type(int driver_type) {
        this.driver_type = driver_type;
    }

    public String getService_order_id() {
        return service_order_id;
    }

    public void setService_order_id(String service_order_id) {
        this.service_order_id = service_order_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getLabour_company_id() {
        return labour_company_id;
    }

    public void setLabour_company_id(int labour_company_id) {
        this.labour_company_id = labour_company_id;
    }

    public int getProduct_type_id() {
        return product_type_id;
    }

    public void setProduct_type_id(int product_type_id) {
        this.product_type_id = product_type_id;
    }

    public int getCar_type_id() {
        return car_type_id;
    }

    public void setCar_type_id(int car_type_id) {
        this.car_type_id = car_type_id;
    }

    public int getYidao_ratio() {
        return yidao_ratio;
    }

    public void setYidao_ratio(int yidao_ratio) {
        this.yidao_ratio = yidao_ratio;
    }

    public int getCompany_commission_ratio() {
        return company_commission_ratio;
    }

    public void setCompany_commission_ratio(int company_commission_ratio) {
        this.company_commission_ratio = company_commission_ratio;
    }

    public int getLabour_commission_ratio() {
        return labour_company_commission_ratio;
    }

    public void setLabour_commission_ratio(int labour_company_commission_ratio) {
        this.labour_company_commission_ratio = labour_company_commission_ratio;
    }

    public int getDriver_commission_ratio() {
        return driver_commission_ratio;
    }

    public void setDriver_commission_ratio(int driver_commission_ratio) {
        this.driver_commission_ratio = driver_commission_ratio;
    }

    public int getFund_commission_ratio() {
        return fund_commission_ratio;
    }

    public void setFund_commission_ratio(int fund_commission_ratio) {
        this.fund_commission_ratio = fund_commission_ratio;
    }

    public int getCommission_ratio() {
        return commission_ratio;
    }

    public void setCommission_ratio(int commission_ratio) {
        this.commission_ratio = commission_ratio;
    }

    public int getDistribute_amount() {
        return distribute_amount;
    }

    public void setDistribute_amout(int distribute_amout) {
        this.distribute_amount = distribute_amout;
    }

    public int getDistribute_yidao_amount() {
        return distribute_yidao_amount;
    }

    public void setDistribute_yidao_amount(int distribute_yidao_amount) {
        this.distribute_yidao_amount = distribute_yidao_amount;
    }

    public int getDistribute_company_amount() {
        return distribute_company_amount;
    }

    public void setDistribute_company_amount(int distribute_company_amount) {
        this.distribute_company_amount = distribute_company_amount;
    }

    public int getDistribute_labour_company_amount() {
        return distribute_labour_company_amount;
    }

    public void setDistribute_labour_company_amount(int distribute_labour_company_amount) {
        this.distribute_labour_company_amount = distribute_labour_company_amount;
    }

    public int getDistriebute_driver_amount() {
        return distribute_driver_amount;
    }

    public void setDistriebute_driver_amount(int distriebute_driver_amount) {
        this.distribute_driver_amount = distriebute_driver_amount;
    }

    public int getDistribute_fund_amount() {
        return distribute_fund_amount;
    }

    public void setDistribute_fund_amount(int distribute_fund_amount) {
        this.distribute_fund_amount = distribute_fund_amount;
    }

    public int getCompany_receipt_amount() {
        return company_receipt_amount;
    }

    public void setCompany_receipt_amount(int company_receipt_amount) {
        this.company_receipt_amount = company_receipt_amount;
    }

    public int getLabour_company_receipt_amount() {
        return labour_company_receipt_amount;
    }

    public void setLabour_company_receipt_amount(int labour_company_receipt_amount) {
        this.labour_company_receipt_amount = labour_company_receipt_amount;
    }

    public int getDriver_laowu_amount() {
        return driver_laowu_amount;
    }

    public void setDriver_laowu_amount(int driver_laowu_amount) {
        this.driver_laowu_amount = driver_laowu_amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static CommissionLog getCommissionLog(Map<String,String> commssionMap){
        CommissionLog commissionLog = new CommissionLog();
        commissionLog.setDistribute_commission_log_id(commssionMap.get("distribute_commission_log_id"));
        commissionLog.setDriver_id(commssionMap.get("driver_id"));
        commissionLog.setDriver_type(Integer.parseInt(commssionMap.get("driver_type")));
        commissionLog.setService_order_id(commssionMap.get("service_order_id"));
        commissionLog.setCompany_id(Integer.parseInt(commssionMap.get("company_id")));
        commissionLog.setLabour_company_id(Integer.parseInt(commssionMap.get("labour_company_id")));
        commissionLog.setProduct_type_id(Integer.parseInt(commssionMap.get("product_type_id")));
        commissionLog.setCar_type_id(Integer.parseInt(commssionMap.get("car_type_id")));
        commissionLog.setYidao_ratio(Integer.parseInt(commssionMap.get("yidao_ratio")));
        commissionLog.setCompany_commission_ratio(Integer.parseInt(commssionMap.get("company_commission_ratio")));
        commissionLog.setLabour_commission_ratio(Integer.parseInt(commssionMap.get("labour_commission_ratio")));
        commissionLog.setDriver_commission_ratio(Integer.parseInt(commssionMap.get("driver_commission_ratio")));
        commissionLog.setFund_commission_ratio(Integer.parseInt(commssionMap.get("fund_commission_ratio")));
        commissionLog.setCommission_ratio(Integer.parseInt(commssionMap.get("commission_ratio")));
        commissionLog.setDistribute_amout(Integer.parseInt(commssionMap.get("distribute_amount")));
        commissionLog.setDistribute_yidao_amount(Integer.parseInt(commssionMap.get("distribute_yidao_amount")));
        commissionLog.setDistribute_company_amount(Integer.parseInt(commssionMap.get("distribute_company_amount")));
        commissionLog.setDistribute_labour_company_amount(Integer.parseInt(commssionMap.get("distribute_labour_company_amount")));
        commissionLog.setDistriebute_driver_amount(Integer.parseInt(commssionMap.get("distribute_driver_amount")));
        commissionLog.setDistribute_fund_amount(Integer.parseInt(commssionMap.get("distribute_fund_amount")));
        commissionLog.setCompany_receipt_amount(Integer.parseInt(commssionMap.get("company_receipt_amount")));
        commissionLog.setLabour_company_receipt_amount(Integer.parseInt(commssionMap.get("labour_company_receipt_amount")));
        commissionLog.setDriver_laowu_amount(Integer.parseInt(commssionMap.get("driver_laowu_amount")));
        commissionLog.setType(Integer.parseInt(commssionMap.get("type")));
        commissionLog.setStatus(Integer.parseInt(commssionMap.get("status")));
        return commissionLog;
    }
}

package com.yongche.framework.api.settlement;

import com.yongche.framework.api.order.Order;
import com.yongche.framework.core.DbTableColumn;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.db.SettlementDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.*;


public class Settlement {

    private String driver_settlement_id;
    private String distribute_commission_log_id;
    private String driver_id;
    private String service_order_id;
    private int amount;
    private int settle_action;
    private int rewards_type;
    private String extra;
    private int status;

    public static Logger log = LoggerFactory.getLogger(Settlement.class);

    public String getDriver_settlement_id() {
        return driver_settlement_id;
    }

    public void setDriver_settlement_id(String deriver_settlement_id) {
        this.driver_settlement_id = deriver_settlement_id;
    }

    public String getDistribute_commission_log_id() {
        return distribute_commission_log_id;
    }

    public void setDistribute_commission_log_id(String distribute_commission_log_id) {
        this.distribute_commission_log_id = distribute_commission_log_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getService_order_id() {
        return service_order_id;
    }

    public void setService_order_id(String service_order_id) {
        this.service_order_id = service_order_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getSettle_action() {
        return settle_action;
    }

    public void setSettle_action(int settle_action) {
        this.settle_action = settle_action;
    }

    public int getRewards_type() {
        return rewards_type;
    }

    public void setRewards_type(int rewards_type) {
        this.rewards_type = rewards_type;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static Settlement getSettlement(Map<String,String> settlementMap){
        Settlement settlement = new Settlement();
        settlement.setDriver_id(settlementMap.get("driver_id"));
        settlement.setService_order_id(settlementMap.get("service_order_id"));
        settlement.setStatus(Integer.parseInt(settlementMap.get("status")));
        settlement.setExtra(settlementMap.get("extra"));
        settlement.setDistribute_commission_log_id(settlementMap.get("distribute_commission_log_id"));
        return settlement;
    }

    /**
     *检查分佣是否正确
     * @param service_order_id 订单ID
     * @param driver_id  司机ID
     */
    public static void check_distribute(String service_order_id,String driver_id,boolean isAdjustAmount,boolean isAccounted){
        if(service_order_id!=null&driver_id!=null&!isAdjustAmount&&!isAccounted){
            check_distribute(service_order_id,driver_id);
        }else if (service_order_id!=null&driver_id!=null&isAdjustAmount&&!isAccounted){
            check_distribute_adjustAmount_unAccounted(service_order_id,driver_id);
        }else if(service_order_id==null&driver_id==null&isAdjustAmount&&isAccounted){
            Random random = new Random();
            int randomInt = random.nextInt(64);
            check_database_distribute_adjustAmount_accounted(randomInt);
        }

    }

    /**
     * 根据订单ID和司机ID获得分佣日志ID
     * @param service_order_id
     * @param driver_id
     * @return
     */
    public static String get_distribute_commission_log_id(String service_order_id,String driver_id){
        String str_sql = "select * from driver_settlement where service_order_id="+service_order_id;
        int dbIndex = Integer.parseInt(driver_id) % 64 ;
        String distribute_commission_log_id = SettlementDBUtil.getDataFromLastRow(dbIndex,str_sql, DbTableColumn.DISTRIBUTE_COMMISION_LOG_ID);
        log.info("distribute_commission_log_id:"+distribute_commission_log_id);
        return distribute_commission_log_id;
    }

    /**
     * 根据订单ID和司机ID检查分佣是否正确，正常五方分佣
     * @param service_order_id  订单ID
     * @param driver_id 司机ID
     */
    public static void check_distribute(String service_order_id, String driver_id){
        String distribute_commission_log_id = get_distribute_commission_log_id(service_order_id,driver_id);
        check_database_distribute(distribute_commission_log_id,driver_id);
    }

    /**
     * 检查五方分佣是否正确
     * @param distribute_commission_log_id
     * @param driver_id
     */
    public static void check_database_distribute(String distribute_commission_log_id,String driver_id){
        String str_sql = "select * from distribute_commission_log where distribute_commission_log_id="+distribute_commission_log_id;
        int dbIndex = Integer.parseInt(driver_id) % 64 ;
        List<Map<String,String>> resultList = SettlementDBUtil.getResultSet(dbIndex,str_sql);
        if(resultList.size()>0){
            Map<String,String> resultMap = resultList.get(0);
            String distribute_amount = resultMap.get("distribute_amount");
            int int_distribute_amount = Integer.parseInt(distribute_amount);

            String yidao_amount_actual = resultMap.get("distribute_yidao_amount");
            String company_amount_actual = resultMap.get("distribute_company_amount");
            String labour_company_amount_actual = resultMap.get("distribute_labour_company_amount");
            String fund_amount_actual = resultMap.get("distribute_fund_amount");
            String driver_amount_actual = resultMap.get("distribute_driver_amount");

            String yidao_ratio = resultMap.get("yidao_ratio");
            Double int_yidao_ratio = Double.parseDouble(yidao_ratio);
            String company_ratio = resultMap.get("company_commission_ratio");
            Double int_company_ratio = Double.parseDouble(company_ratio)*(1-int_yidao_ratio/10000);
            String labour_company_ratio = resultMap.get("labour_commission_ratio");
            Double int_labour_company_ratio =Double.parseDouble(labour_company_ratio)*(1-int_yidao_ratio/10000);
            String fund_commission_ratio = resultMap.get("fund_commission_ratio");
            Double int_fund_commission_ratio = Double.parseDouble(fund_commission_ratio);
            String driver_ratio = resultMap.get("driver_commission_ratio");
            Double int_driver_ratio = Double.parseDouble(driver_ratio);

            double yidao_amount_expect = int_distribute_amount*int_yidao_ratio/10000;
            double company_amount_expect = int_distribute_amount*int_company_ratio/10000;
            double labour_company_amount_expect = int_distribute_amount*int_labour_company_ratio/10000;
            double fund_amount_expect = int_fund_commission_ratio*int_distribute_amount/10000;
            double driver_amount_expect = int_distribute_amount*int_driver_ratio/10000;

            log.info("分佣总金额:"+int_distribute_amount);

            log.info("易到实际分佣:"+yidao_amount_actual);
            log.info("租赁公司实际分佣:"+company_amount_actual);
            log.info("劳务公司实际分佣:"+labour_company_amount_actual);
            log.info("成长基金实际分佣:"+fund_amount_actual);
            log.info("司机实际分佣:"+driver_amount_actual);

            log.info("易到分佣比例（万分比）:"+int_yidao_ratio);
            log.info("租赁公司分佣比例（万分比）:"+int_company_ratio);
            log.info("劳务公司分佣比例（万分比）:"+int_labour_company_ratio);
            log.info("成长基金分佣比例（万分比）:"+int_fund_commission_ratio);
            log.info("司机分佣比例（万分比）:"+int_driver_ratio);

            log.info("易到预期分佣:"+Math.round(yidao_amount_expect));
            log.info("租赁公司预期分佣:"+Math.round(company_amount_expect));
            log.info("劳务公司预期分佣:"+Math.round(labour_company_amount_expect));
            log.info("成长基金预期分佣:"+Math.round(fund_amount_expect));
            log.info("司机预期分佣:"+ Math.round(driver_amount_expect));

            Assert.assertEquals(Integer.parseInt(yidao_amount_actual),Math.round(yidao_amount_expect),"易到分佣有误");
            Assert.assertEquals(Integer.parseInt(company_amount_actual),Math.round(company_amount_expect),"租赁公司分佣有误");
            Assert.assertEquals(Integer.parseInt(labour_company_amount_actual),Math.round(labour_company_amount_expect),"劳务公司分佣有误");
            Assert.assertEquals(Integer.parseInt(fund_amount_actual),Math.round(fund_amount_expect),"成长基金分佣有误");
            //Assert.assertEquals(Integer.parseInt(driver_amount_actual), Math.round(driver_amount_expect),"司机分佣有误");
            //将Math.floor(driver_amount_expect) 返回的double类型转为int类型
            Assert.assertEquals(Integer.parseInt(driver_amount_actual), new Double(Math.floor(driver_amount_expect)).intValue(),"司机分佣有误");
        }else{
            log.info("The query result amounts to 0 ");
        }
    }

    /**
     * 未记账入账调整金额，检查分佣是否正确
     * @param service_order_id 订单ID
     * @param driver_id 司机ID
     */
    public static void check_distribute_adjustAmount_unAccounted(String service_order_id,String driver_id){
        log.info("-----------------测试未记账入账后调整金额，分佣是否正确--------------------------");
        ArrayList<Settlement> settlements = getSettlementList(service_order_id,driver_id);
        Assert.assertEquals(settlements.size(),2);
        for(int i=0;i<settlements.size();i++){
            Settlement settlement = settlements.get(i);
            int status = settlement.getStatus();
            String extra = settlement.getExtra();
            if(i==0){
                log.info("-----------------------未记账入账调整金额前结算状态-----------------------");
                Assert.assertEquals(status,-10,"settlement status should be -10");
                Assert.assertEquals(extra,"distribute order by platform confirm charge","settlement extra should be distribute order by platform confirm charge");
            }else{
                log.info("-----------------------未记账入账调整金额后结算状态-----------------------");
                Assert.assertEquals(status,10,"settlement status should be 10");
                Assert.assertEquals(extra,"distribute order by platform adjust amount","settlement extra should be distribute order by platform adjust amount");
            }
            log.info("status:"+status);
            log.info("extra:"+extra);
        }
        check_distribute_commission(service_order_id,driver_id);
    }

    public static void check_distribute_commission(String service_order_id,String driver_id){
        ArrayList<CommissionLog> commissionLogList = getCommissionLogList(service_order_id,driver_id);
        Assert.assertEquals(commissionLogList.size(),2);
        for(int i=0;i<commissionLogList.size();i++){
            if(i==0){
                log.info("-----------------------调整金额前分佣明细-----------------------");
            }else{
                log.info("-----------------------调整金额后分佣明细-----------------------");
            }
            check_database_distribute(commissionLogList.get(i).getDistribute_commission_log_id(),driver_id);
        }
    }

    /**
     * 已记账入账后调整金额，检查分佣是否正确
     * @param dbIndex 结算数据库索引号
     */
    public static void check_database_distribute_adjustAmount_accounted(int dbIndex) {
        log.info("-----------------测试已记账入账后调整金额，分佣是否正确--------------------------");
        Settlement settlements = getSettlementList(dbIndex).get(dbIndex);
        String order_id = settlements.getService_order_id();
        String driver_id = settlements.getDriver_id();
        log.info("order_id:"+order_id);
        log.info("driver_id:"+driver_id);
        Order.adjustAmount(order_id);
        CommonUtil.sleep(2000);
        ArrayList<Settlement> settlementList = getSettlementList(order_id, driver_id);
        Assert.assertEquals(settlementList.size(), 3);
        for (int i = 0; i < settlementList.size(); i++) {
            Settlement settlement = settlementList.get(i);
            int status = settlement.getStatus();
            String extra = settlement.getExtra();
            if (i == 0) {
                log.info("-----------------------已记账入账调整金额前分佣-----------------------");
                Assert.assertEquals(status, 30, "settlement status should be 30");
                Assert.assertEquals(extra, "distribute order by platform confirm charge", "settlement extra should be distribute order by platform confirm charge");
            } else if (i == 1) {
                log.info("-----------------------已记账入账调整金额后充正---------------------------");
                Assert.assertEquals(status, 20, "settlement status should be 20");
                Assert.assertEquals(extra, "reverse order by platform adjust amount", "settlement extra should be reverse order by platform adjust amount");
            } else {
                log.info("-----------------------已记账入账调整金额后重新分佣-----------------------");
                Assert.assertEquals(status, 10, "settlement status should be 20");
                Assert.assertEquals(extra, "distribute order by platform adjust amount", "settlement extra should be distribute order by platform adjust amount");
            }
            log.info("status:"+status);
            log.info("extra:"+extra);
        }
        check_distribute_commission(order_id,driver_id);
    }

    /**
     * 根据订单ID和司机ID获得分佣日志记录列表
     * @param service_order_id
     * @param driver_id
     * @return
     */
    public static ArrayList<CommissionLog> getCommissionLogList(String service_order_id,String driver_id){
        String str_sql = "select * from distribute_commission_log where service_order_id="+service_order_id;
        log.info("query sql:"+str_sql);
        int dbIndex = Integer.parseInt(driver_id) % 64 ;
        ArrayList<CommissionLog> commissionLogList = new ArrayList<>();
        List<Map<String,String>> resultList = SettlementDBUtil.getResultSet(dbIndex,str_sql);
        log.info("commission_log count:"+resultList.size());
        CommissionLog commissionLog = null;
        for(Map<String,String> map:resultList){
            commissionLog = CommissionLog.getCommissionLog(map);
            commissionLogList.add(commissionLog);
        }
        return commissionLogList;
    }

    /**
     * 根据订单ID和司机ID获得结算记录列表
     * @param service_order_id
     * @param driver_id
     * @return
     */
    public static ArrayList<Settlement> getSettlementList(String service_order_id,String driver_id){
        String str_sql = "select * from driver_settlement where service_order_id="+service_order_id + " order by create_time";
        int dbIndex = Integer.parseInt(driver_id) % 64 ;
        ArrayList<Settlement> settlementList = new ArrayList<>();
        List<Map<String,String>> resultList = SettlementDBUtil.getResultSet(dbIndex,str_sql);
        Settlement settlement = null;
        for(Map<String,String> map:resultList){
            settlement = Settlement.getSettlement(map);
            settlementList.add(settlement);
        }
        return settlementList;
    }

    /**
     * 根据司机ID获得未调整过金额且分佣已记账入账的记录列表
     * @param dbIndex(0-63)
     * @return
     */
    public static ArrayList<Settlement> getSettlementList(int dbIndex){
        String str_sql = "select * from driver_settlement where status=30 and extra='distribute order by platform confirm charge' and service_order_id in (select service_order_id from driver_settlement GROUP BY service_order_id HAVING count(*)=1 ) ORDER BY record_time DESC";
        ArrayList<Settlement> settlementList = new ArrayList<>();
        if(dbIndex<=63&&dbIndex>0){
            List<Map<String,String>> resultList = SettlementDBUtil.getResultSet(dbIndex,str_sql);
            Settlement settlement = null;
            for(Map<String,String> map:resultList){
                settlement = Settlement.getSettlement(map);
                settlementList.add(settlement);
            }
        }else {
            log.info("dbIndex is not valid,dbIndex should between 0 to 63 ");
        }
        return settlementList;
    }

}

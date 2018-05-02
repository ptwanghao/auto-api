package com.yongche.framework.api.charge;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.JsonUtil;
import com.yongche.framework.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 */
public class Estimate {

    public static Logger log = LoggerFactory.getLogger(Estimate.class);

    public static Map<String, TestParameter> getIntervalEstimateCost(Map<String,TestParameter> parameterMap){
        return CaseExecutor.runTestCase("charge/charge_getIntervalEstimateCost.xml",parameterMap);
    }

    public static Map<String, TestParameter> getDriverEstimateCostFromExt(Map<String,TestParameter> parameterMap){
        return CaseExecutor.runTestCase("charge/charge_getDriverEstimateCostFromExt.xml",parameterMap);
    }

    /**
     * 计算乘客的预估费用<br>
     * @param basePriceMap  基础价格数据
     * @param paramMap      getIntervalEstimateCost() 返回的价格数据
     * @param freeLongDistance  免费空驶公里数(用于计算长途服务费)
     * @return 乘客的预估价格
     */
    public static double getPassengerEstimateCost(Map<String,TestParameter> basePriceMap,
                                                  Map<String,TestParameter> paramMap,
                                                  int freeLongDistance){
        double cost=-1;

        String car_type_id = MapUtil.getString(basePriceMap,"car_type_id");
        log.info("car_type_id="+car_type_id);
        log.info("长途服务费 (API): " + JsonUtil.getValueFromResult(paramMap,car_type_id,"长途服务费") );
        log.info("夜间服务费 (API): " + JsonUtil.getValueFromResult(paramMap,car_type_id,"夜间服务费") );
        log.info("超时费 (API): " + JsonUtil.getValueFromResult(paramMap,car_type_id,"超时费") );
        log.info("超公里费 (API): " + JsonUtil.getValueFromResult(paramMap,car_type_id,"超公里费") );
        log.info("起步价 (API): " + JsonUtil.getValueFromResult(paramMap,car_type_id,"起步价") );
        log.info("系统提价倍率 (API): " + JsonUtil.getValueFromResult(paramMap,car_type_id,"系统提价倍率") );
        log.info("***************计算所得值***************");

        //长途服务费=round(max((实际计费公里数-免费空驶公里数)*长途服务公里单价(元),0),2)
        int distance = MapUtil.getInt(paramMap,"distance"); //实际计费公里数  米
        //double distance = MapUtil.getDouble(paramMap,"distance"); //实际计费公里数  米
        //distance = CommonUtil.round(distance);

        int fee_per_deadhead_kilometer = MapUtil.getInt(basePriceMap,"fee_per_deadhead_kilometer"); //长途服务费单价 : 分
        double longDistanceFee =(double) ( (distance - freeLongDistance) * fee_per_deadhead_kilometer) / 1000 / 100;
        longDistanceFee = Math.max(longDistanceFee,0);
        longDistanceFee = CommonUtil.round(longDistanceFee);
        log.info("长途服务费 : " + longDistanceFee);

        //夜间服务费=round((夜间服务费单价(元)*夜间服务时长(秒),2)
        int night_time_length = MapUtil.getInt(paramMap,"night_time_length");//夜间计费时长 分钟
        int night_unit_price = MapUtil.getInt(basePriceMap,"night_service_fee");//夜间服务费单价 分
        double nightServiceFee = (double)(night_unit_price * night_time_length) / 100;// / 60;
        log.info("夜间服务费 : " + nightServiceFee);


        //超时费=round(max(超出分钟*分钟单价,0),2)
        int free_time_length = MapUtil.getInt(basePriceMap,"free_time_length"); //免费时长 秒
        int actualTime = MapUtil.getInt(paramMap,"time_length");//实际计费时间 秒
        int fee_per_minute = MapUtil.getInt(basePriceMap,"fee_per_minute"); //每分钟费用 分

        //超出分钟=(ceil(max((实际计费分钟-免费分钟),0)/时长粒度分钟))*时长粒度分钟
        int granularity = MapUtil.getInt(basePriceMap,"granularity") / 60;
        log.info("时长粒度 (分钟) : " + granularity);
        double minutes = (actualTime - free_time_length) / 60;
        minutes = Math.max(minutes,0);
        minutes = Math.ceil(minutes / granularity);
        minutes = minutes * granularity;
        log.info("超出分钟 : "+ minutes);
        double overTimeFee =   minutes * fee_per_minute / 100;
        overTimeFee = Math.round(overTimeFee);
        overTimeFee = Math.max(overTimeFee,0);
        log.info("超时费 : " + overTimeFee);

        //超公里费=round(max((实际计费公里数-免费公里数)*公里单价,0),2)
        int free_distance = MapUtil.getInt(basePriceMap,"free_distance"); //免费公里数 米
        int fee_per_kilometer = MapUtil.getInt(basePriceMap,"fee_per_kilometer"); //每公里费用 分
        double overMileFee = (double)(distance - free_distance ) * fee_per_kilometer / 1000 / 100;
        //overMileFee = overMileFee/ 1000 / 100;
        overMileFee = CommonUtil.round(overMileFee);
        overMileFee = Math.max(overMileFee,0);
        log.info("超公里费 : " + overMileFee);

        //原始订单基本费用=round(max((起步价+超时费+超公里费+长途服务费+夜间服务费),0),2)
        //原始订单基本费用=round(max(原始订单基本费用,最小消费金额),2)
        int starting_fare = MapUtil.getInt(basePriceMap,"starting_fare"); //起步价 分
        double min_fee = MapUtil.getDouble(basePriceMap,"min_fee"); //最低消费 分
        double baseFare = starting_fare/100 + overTimeFee + overMileFee + longDistanceFee + nightServiceFee;

        baseFare = Math.max(baseFare,min_fee/100);

        //起步价
        log.info("起步价 : " + (double)starting_fare/100);

        //TODO:找出能直接取 系统提价倍率 的API
        String multiple = JsonUtil.getValueFromResult(paramMap,car_type_id,"系统提价倍率");
        log.info("系统提价倍率 : " + multiple);

        cost = (1 + Double.parseDouble(multiple) ) * baseFare;
        cost = CommonUtil.round(cost);

        log.info("原始订单基本费用 : " + cost);
        log.info("原始订单基本费用 (API): " + JsonUtil.getValueFromResult(paramMap,car_type_id,"原始订单基本费用") );
        return cost;
    }

    public static double getDriverEstimateCost(Map<String,TestParameter> basePriceMap){

        //订单基本费用=round(max((起步价+超时费+超公里费+长途服务费+夜间服务费),0),2)
        //===起步价===
        int starting_fare = MapUtil.getInt(basePriceMap,"starting_fare")/100; //起步价 分
        log.info("起步价 : " + (double)starting_fare);


        //===超时费===
        int free_time_length = MapUtil.getInt(basePriceMap,"free_time_length"); //免费时长 秒
        int actualTime = MapUtil.getInt(basePriceMap,"time_length");//实际计费时间 秒
        int fee_per_minute = MapUtil.getInt(basePriceMap,"fee_per_minute"); //每分钟费用 分

        //超出分钟=(ceil(max((实际计费分钟-免费分钟),0)/时长粒度分钟))*时长粒度分钟
        int granularity = MapUtil.getInt(basePriceMap,"granularity") / 60;
        log.info("时长粒度 (分钟) : " + granularity);

        double minutes = (actualTime - free_time_length) / 60;
        minutes = Math.max(minutes,0);
        minutes = Math.ceil(minutes / granularity);
        minutes = minutes * granularity;
        log.info("超出分钟 : "+ minutes);

        double overTimeFee =   minutes * fee_per_minute / 100;
        overTimeFee = Math.round(overTimeFee);
        overTimeFee = Math.max(overTimeFee,0);
        log.info("超时费 : " + overTimeFee);

        //===超公里费===
        //超公里费=round(max((实际计费公里数-免费公里数)*公里单价,0),2)
        int distance = MapUtil.getInt(basePriceMap,"distance"); //实际计费公里数  米
        int free_distance = MapUtil.getInt(basePriceMap,"free_distance"); //免费公里数 米
        int fee_per_kilometer = MapUtil.getInt(basePriceMap,"fee_per_kilometer"); //每公里费用 分
        double overMileFee = (double)(distance - free_distance ) * fee_per_kilometer / 1000 / 100;
        //overMileFee = overMileFee/ 1000 / 100;
        overMileFee = CommonUtil.round(overMileFee);
        overMileFee = Math.max(overMileFee,0);
        log.info("超公里费 : " + overMileFee);

        //===长途服务费===
        //长途服务费=round(max((实际计费公里数-免费空驶公里数)*长途服务公里单价(元),0),2)
        int freeLongDistance = MapUtil.getInt(basePriceMap,"free_long_distance");
        int fee_per_deadhead_kilometer = MapUtil.getInt(basePriceMap,"fee_per_deadhead_kilometer"); //长途服务费单价 : 分
        double longDistanceFee =(double) ( (distance - freeLongDistance) * fee_per_deadhead_kilometer) / 1000 / 100;
        longDistanceFee = Math.max(longDistanceFee,0);
        longDistanceFee = CommonUtil.round(longDistanceFee);
        log.info("长途服务费 : " + longDistanceFee);

        //===夜间服务费===
        //夜间服务费=round((夜间服务费单价(元)*夜间服务时长(秒),2)
        int night_time_length = MapUtil.getInt(basePriceMap,"night_time_length");//夜间计费时长 分钟
        int night_unit_price = MapUtil.getInt(basePriceMap,"night_service_fee");//夜间服务费单价 分
        double nightServiceFee = (double)(night_unit_price * night_time_length) / 100;// / 60;
        log.info("夜间服务费 : " + nightServiceFee);

        double subsidy= MapUtil.getDouble(basePriceMap,"subsidy");
        log.info("易到补贴 " + subsidy);

        double expressWayFee = MapUtil.getDouble(basePriceMap,"expressWayFee");
        log.info("高速费 " + expressWayFee);

        double parkFee = MapUtil.getDouble(basePriceMap,"parkFee");
        log.info("停车费 " + parkFee);

        double otherFee = MapUtil.getDouble(basePriceMap,"otherFee");
        log.info("其它费用" + otherFee);

        //订单基本费用=round(max((起步价+超时费+超公里费+长途服务费+夜间服务费),0),2)
        //订单原始金额=round(max((订单基本费用+高速费+停车费+其他费用+易到补贴+订单加价金额),0),2)

        double min_fee = MapUtil.getDouble(basePriceMap,"min_fee"); //最低消费 分
        double baseFare = starting_fare +overTimeFee + overMileFee + longDistanceFee + nightServiceFee;
        baseFare = Math.max(baseFare,min_fee/100);

        log.info("订单基本费用 " + baseFare);
        log.info("订单基本费用 (API): " + MapUtil.getDouble(basePriceMap,"apiPrice"));

        return  baseFare;
    }
}

package com.yongche.framework.api.charge;

import com.yongche.framework.core.Snapshot;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The class is to calculate 长途服务费，超公里费，超时费，夜间服务费
 */
public class FaresCalculator {

    public static Logger log = LoggerFactory.getLogger(FaresCalculator.class);

    /**
     * 計算長途服務費 长途服务费=round(max((实际计费公里数-免费空驶公里数)*长途服务公里单价,0),2)
     * @param distance  總里程，單位：公里
     * @param freeLongDistance 免費長途里程數， 單位：公里
     * @param fee_per_deadhead_kilometer 長途服務費單價，單位：分
     * @return
     */
    public static double getLongDistanceFee(double distance, double freeLongDistance, int fee_per_deadhead_kilometer){
        double longDistanceFee =   (distance - freeLongDistance) * fee_per_deadhead_kilometer / 100;
        longDistanceFee = Math.max(longDistanceFee, 0);
        return CommonUtil.round(longDistanceFee);
    }

    /**
     * 計算超公里費 超公里费=round(max((实际计费公里数-免费公里数)*公里单价,0),2)
     * @param distance 總里程，單位：公里
     * @param freeDistance 免費里程數， 單位：公里
     * @param fee_per_kilometer 每公里單價 ，單位：分
     * @return 里程费，单位：元
     */
    public static double getDistanceFee(double distance, double freeDistance, int fee_per_kilometer){
        double distanceFee = ( distance - freeDistance ) * fee_per_kilometer / 100;
        distanceFee = Math.max(distanceFee, 0);
        return CommonUtil.round(distanceFee);
    }

    /**
     * 计算夜间服务费 夜间服务费=round((夜间服务费单价*夜间服务时长),2)
     * @param nightTimeLength 夜間服務時長 ，單位：分鐘
     * @param night_unit_price 夜間服務費單價，單位：分
     * @return 夜间服务费，单位：元
     */
    public static double getNightServiceFee(int nightTimeLength, int night_unit_price){
        double fee = nightTimeLength * night_unit_price / 100;
        fee = Math.max(fee, 0);
        return CommonUtil.round(fee);
    }

    /**
     * 计算超时费 超时费=round(max(超出分钟*分钟单价,0),2)
     * @param actualMinutes 总时长 单位：分钟
     * @param free_time_length 免费分钟数 单位：分钟
     * @param fee_per_minute 时长单价，单位：分
     * @param granularity 时长粒度分钟
     * @return 超时费，单位：元
     */
    public static double getOverTimeFee(int actualMinutes, int free_time_length, int fee_per_minute,int granularity) {
        /**
         *  超出分钟=(ceil(max((实际计费分钟-免费分钟),0)/时长粒度分钟))*时长粒度分钟
         */
        double overTimeLength = (actualMinutes - free_time_length);
        overTimeLength = Math.max(overTimeLength, 0);
        overTimeLength = Math.ceil(overTimeLength / granularity) * granularity;

        double overTimeFee = overTimeLength * fee_per_minute / 100;
        overTimeFee = Math.max(overTimeFee, 0);
        return CommonUtil.round(overTimeFee);
    }

    /**
     * 计算订单基本费用
     * @param minFee  最低消费
     * @param startingFare 起步价
     * @param distanceFee 超公里费
     * @param longDistanceFee 长途服务费
     * @param overTimeFee 超时费
     * @param nightServiceFee 夜间服务费
     * @param bidding 系统提价倍率
     * @return 订单原始金额
     */
    public static double getBasicFare(double minFee,          // 最低消费
                                      double startingFare,    // 起步价
                                      double distanceFee,     // 超公里费
                                      double longDistanceFee, // 长途服务费
                                      double overTimeFee,     // 超时费
                                      double nightServiceFee, // 夜间服务费
                                      double bidding){        // 加价倍率

        /**
         * 原始订单基本费用=round(max((起步价+超时费+超公里费+长途服务费+夜间服务费),0),2)
         */
        double basicFare = CommonUtil.round(Math.max(startingFare + overTimeFee + distanceFee + longDistanceFee + nightServiceFee,0));

        /**
         * 原始订单基本费用=round(max(原始订单基本费用,最小消费金额),2)
         */
        basicFare = CommonUtil.round(Math.max(basicFare,minFee));

        /**
         * 订单加价金额=round(原始订单基本费用*(系统提价倍率),2)
         * 订单基本费用=round((原始订单基本费用+订单加价金额),2)
         */
        log.info("系统提价倍率 : " + bidding);
        basicFare += basicFare * bidding;
        basicFare = CommonUtil.round(basicFare);

        log.info("订单基本费用(Expected) : " + basicFare);
        return basicFare;
    }

    /**
     * Calculate the basic order fare according to the order id and results returned by state.endTiming
     * @param orderId  id of the order which has ended
     * @param orderDataMap parameter map returned by state.endTiming which contains order running data including distance, time length,etc
     * @return
     */
    public static double getBasicFare(String orderId , Map<String, TestParameter> orderDataMap, boolean isDriver){
        // 获取订单结算依据 -（订单快照）
        ChargeSnap.SnapType snapType = (isDriver)? ChargeSnap.SnapType.Driver: ChargeSnap.SnapType.Passenger;
        ChargeSnap chargeSnap = ChargeSnap.construct(snapType);
        Snapshot snap = chargeSnap.getLastSnapFromMongo(orderId);

        return getBasicFare(snap,orderDataMap);
    }

    /**
     * 计算订单基本费用
     * @param snap  订单快照
     * @param orderDataMap 订单运行数据
     * @return 订单基本费用
     */
    public static double getBasicFare(Snapshot snap, Map<String, TestParameter> orderDataMap) {

        double basicFare = 0.0;

        double minFare = snap.getOutput().getDouble("最小消费金额");
        log.info("最小消费金额 : " + minFare);
        double startFee = snap.getOutput().getDouble("起步价");
        log.info("起步价 : " + startFee);
        double distance = MapUtil.getDouble(orderDataMap,"实际计费公里数");
        log.info("实际计费公里数 : " + distance);
        int totalMinutes = MapUtil.getInt(orderDataMap,"实际计费分钟");
        log.info("实际计费分钟 : " + totalMinutes);
        double freeDistance = snap.getPrice().getDouble("免费公里数");
        log.info("免费公里数 : " + freeDistance);
        int free_time_length = snap.getPrice().getInt("免费分钟");
        log.info("免费分钟 : " + free_time_length);
        double freeLongDistance = snap.getPrice().getDouble("免费空驶公里数");
        log.info("免费空驶公里数 : " + freeLongDistance);
        int nightTimeLength = snap.getData().getInt("夜间服务时长");
        log.info("夜间服务时长 : " + nightTimeLength);
        int fee_per_kilometer = (int)(snap.getPrice().getDouble("公里单价") * 100);
        log.info("公里单价 : " + fee_per_kilometer);
        int fee_per_minute = (int)(snap.getPrice().getDouble("分钟单价") * 100);
        log.info("分钟单价 : " + fee_per_minute);
        int fee_per_deadhead_kilometer = (int)(snap.getPrice().getDouble("长途服务公里单价") * 100);
        log.info("长途服务公里单价 : " + fee_per_deadhead_kilometer);
        int night_unit_price = (int)(snap.getPrice().getDouble("夜间服务费单价") * 100);
        log.info("夜间服务费单价 : " + night_unit_price);
        int granularity_minute = snap.getPrice().getInt("时长粒度分钟");
        log.info("时长粒度分钟 : " + granularity_minute);


        //超公里费
        double distanceFee = getDistanceFee(distance,freeDistance,fee_per_kilometer);
        log.info("超公里费(Expected) : " + distanceFee);
        //超时费
        double overTimeFee = getOverTimeFee(totalMinutes,free_time_length,fee_per_minute,granularity_minute);
        log.info("超时费(Expected) : " + overTimeFee);
        //长途服务费
        double longDistanceFee = getLongDistanceFee(distance,freeLongDistance,fee_per_deadhead_kilometer);
        log.info("长途服务费(Expected) : " + longDistanceFee);
        //夜间服务费
        double nightServiceFee = getNightServiceFee(nightTimeLength,night_unit_price);
        log.info("夜间服务费(Expected) : " + nightServiceFee);

        double systemBidding = snap.getPrice().getDouble("系统提价倍率");
        log.info("系统提价倍率(Expected) : " + systemBidding);

        basicFare = getBasicFare(minFare,startFee,distanceFee,longDistanceFee,overTimeFee,nightServiceFee,systemBidding);

        return basicFare;
    }


    /**
     * 计算订单原始金额 订单原始金额=round(max((订单基本费用+高速费+停车费+其他费用),0),2)
     * @param snap 订单快照
     * @param orderDataMap 订单运行数据
     * @return
     */
    public static double getOriginaOrderAmount(Snapshot snap, Map<String,TestParameter> orderDataMap){
        double basicFare = getBasicFare(snap,orderDataMap);

        double expressWayFee = MapUtil.getDouble(orderDataMap,"高速费");
        double parkFee = MapUtil.getDouble(orderDataMap,"停车费");
        double otherFee = MapUtil.getDouble(orderDataMap,"其他费用");

        /**
         * 订单原始金额=round(max((订单基本费用+高速费+停车费+其他费用),0),2)
         */
        double originalOrderAmount = CommonUtil.round(Math.max(basicFare + expressWayFee + parkFee + otherFee,0));

        return originalOrderAmount;
    }

    /**
     * 计算限扣券金额 限扣券金额=round(((限扣券基本费用)*(1-(优惠券折扣))),2)
     * @param basicFare 订单基本费用
     * @param couponDiscountTopThresholdFare 优惠券折扣上限金额
     * @param discountRateOfCoupon 优惠券折扣
     * @return
     */
    public static double getLimitCouponFare(double basicFare, double couponDiscountTopThresholdFare, int discountRateOfCoupon){

        //限扣券基本费用=min(订单基本费用,优惠券折扣上限金额)
        double limitCouponBasicFare = Math.min(basicFare, couponDiscountTopThresholdFare);

        //优惠券折扣=((100-优惠券折扣)/100)
        discountRateOfCoupon = (100 - discountRateOfCoupon ) / 100;

        //限扣券金额=round(((限扣券基本费用)*(1-(优惠券折扣))),2)
        return CommonUtil.round(limitCouponBasicFare * (1-discountRateOfCoupon));
    }

    /**
     * 计算满减券金额 满减券金额=max(订单基本费用-满减券上限金额+1,0)?满减券优惠金额:0 <br>
     *    满减券如何翻译？XX off for a purchase or reduction for a purchase ？ <br>
     * @param basicFare 订单基本费用
     * @param topLimitFeeOfReduction 满减券上限金额
     * @param discountFeeOfReduction 满减券优惠金额
     * @return
     */
    public static double getFeeOfReductionForPurchase( double basicFare, double topLimitFeeOfReduction, double discountFeeOfReduction ){
        return Math.max(basicFare - topLimitFeeOfReduction + 1,0) > 0 ? discountFeeOfReduction : 0 ;
    }

    /**
     * 计算优惠后订单基本费用 优惠后订单基本费用=round(max(订单基本费用-套包优惠金额,0),2)
     * @param basicFare 订单基本费用
     * @param comboDiscountFare 套包优惠金额
     * @return
     */
    public static double getOrderBasicFareAfterDiscount(double basicFare, double comboDiscountFare) {
        return CommonUtil.round(Math.max(basicFare - comboDiscountFare, 0) );
    }

    /**
     * 计算订单优惠后金额 订单优惠后金额=round((max((优惠后订单基本费用)*会员折扣-订单优惠券金额-限扣券金额-满减券金额-减免金额,0)+高速费+停车费+其他费用),2)
     * @param basicFare 订单基本费用
     * @param snap 乘客端 订单最后一张快照
     * @return
     */
    public static double getOrderFareAfterDiscount(double basicFare, Snapshot snap) {

        //套包优惠金额
        double comboDiscountFare = snap.getOutput().getDouble("套包优惠金额");
        log.info("套包优惠金额 : " + comboDiscountFare);
        //计算 优惠后订单基本费用
        double orderBasicFareAfterDiscount = getOrderBasicFareAfterDiscount(basicFare,comboDiscountFare);
        log.info("优惠后订单基本费用(Expected) : " + orderBasicFareAfterDiscount);

        //订单优惠券金额
        double orderCouponAmount = snap.getData().getDouble("订单优惠券金额");

        double couponDiscountTopThresholdFare = snap.getData().getDouble("优惠券折扣上限金额");
        int discountRateOfCoupon = snap.getData().getInt("优惠券折扣");
        //计算限扣券金额
        double limitCouponFare = getLimitCouponFare(basicFare,couponDiscountTopThresholdFare, discountRateOfCoupon);

        double topLimitFeeOfReduction = snap.getData().getDouble("满减券上限金额");
        double discountFeeOfReduction = snap.getData().getDouble("满减券优惠金额");
        //计算满减券金额
        double reductionFeeForPurchase = getFeeOfReductionForPurchase(basicFare,topLimitFeeOfReduction,discountFeeOfReduction);

        double abatementFare = snap.getPrice().getDouble("减免金额");
        double expressWayFee = snap.getData().getDouble("高速费");
        double parkingFee = snap.getData().getDouble("停车费");
        double otherFee = snap.getData().getDouble("其他费用");


        //会员折扣=((100-会员折扣)/100)
        double discountRateOfMembership = (100 - snap.getData().getDouble("会员折扣") ) / 100;
        log.info("会员折扣 : " + discountRateOfMembership);

        //订单优惠后金额=round((max((优惠后订单基本费用)*会员折扣-订单优惠券金额-限扣券金额-满减券金额-减免金额,0)+高速费+停车费+其他费用),2)

        double orderFareAfterDiscount = orderBasicFareAfterDiscount * discountRateOfMembership -
                orderCouponAmount -  limitCouponFare - reductionFeeForPurchase - abatementFare;

        orderFareAfterDiscount = Math.max(orderFareAfterDiscount, 0);

        orderFareAfterDiscount = CommonUtil.round(orderFareAfterDiscount + expressWayFee + parkingFee + otherFee);

        log.info("订单优惠后金额(Expected) : " + orderFareAfterDiscount);
        return orderFareAfterDiscount;
    }

    /**
     * 计算用户应付金额
     * @param orderId 订单ID
     * @param orderDataMap 订单运行数据，包含里程，时长等等信息
     * @return
     */
    public static double getPassengerFare(String orderId, Map<String, TestParameter> orderDataMap){

        ChargeSnap chargeSnap = ChargeSnap.construct(ChargeSnap.SnapType.Passenger);
        Snapshot snap = chargeSnap.getLastSnapFromMongo(orderId);

        //订单基本金额
        double basicFare = getBasicFare(snap,orderDataMap);

        //订单优惠后金额
        double amountToBeCharged = getOrderFareAfterDiscount(basicFare,snap);

        log.info("用户应付金额(Expected) : " + amountToBeCharged);

        return amountToBeCharged;
    }

    /**
     * 计算司机端订单原始金额（结算之前）
     * @param snap  司机端 订单最后一张快照
     * @param orderDataMap 订单运行数据，包含里程，时长等等信息
     * @return 司机端佣金总金额
     */
    public static double getDriverFare(Snapshot snap, Map<String, TestParameter> orderDataMap){

        double originalOrderFare = 0; // 订单原始金额
        boolean isFixedFee = false; // 是否是一口价订单

        // 是否一口价订单
        isFixedFee = snap.getPrice().containsKey("预估一口价费用");
        if (isFixedFee) {
            log.info("这是一口价订单");
        } else {
            log.info("这不是一口价订单");
        }

        // 获取司机端订单基本费用
        double basicFare = getBasicFare(snap,orderDataMap);

        // 易到补贴
        double subsidy = snap.getOutput().getDouble("易到补贴");

        // 下浮系数 用于预估，不用于最后付费计费计算
        //double downCoefficient =(isFixedFee)? snap.getPrice().getDouble("下浮系数") : 1 ;

        // 计算佣金总金额
        // double totalCommission = CommonUtil.round(basicFare * downCoefficient) + subsidy ;
        double totalCommission = basicFare + subsidy ;

        if (isFixedFee) {
            double estimatedFixedFee = snap.getPrice().getDouble("预估一口价费用");
            double thresholdTop = snap.getPrice().getDouble("预估一口价阈值上限");
            double thresholdBottom = snap.getPrice().getDouble("预估一口价阈值下限");
            boolean isBeyondThreshold = isBeyondThreshold(estimatedFixedFee,totalCommission,thresholdBottom,thresholdTop);
            /**
             * 佣金总金额=预估一口价超阈?佣金总金额:预估一口价费用
             */
            totalCommission = (isBeyondThreshold) ? totalCommission : estimatedFixedFee;
            log.info("预估一口价费用 : " + estimatedFixedFee);
        }

        double expressWayFee = snap.getData().getDouble("高速费");
        log.info("高速费 : " + expressWayFee);
        double parkFee = snap.getData().getDouble("停车费");
        log.info("停车费 ： " + parkFee);
        double otherFee = snap.getData().getDouble("其他费用");
        log.info("其他费用 ：" + otherFee);
        /**
         * 订单原始金额=round(max((佣金总金额+高速费+停车费+其他费用),0),2)
         */
        originalOrderFare = CommonUtil.round(Math.max(totalCommission + expressWayFee + parkFee + otherFee,0));

        //TODO:取消订单金额

        return originalOrderFare;
    }

    /**
     * 计算司机端订单原始金额（结算之前）
     * @param orderId  订单ID
     * @param orderDataMap 订单运行数据，包含里程，时长等等信息
     * @return 司机端佣金总金额
     */
    public static double getDriverFare(String orderId,Map<String, TestParameter> orderDataMap){
        // 获取司机端快照
        ChargeSnap driverSnap = ChargeSnap.construct(ChargeSnap.SnapType.Driver);
        Snapshot snap =  driverSnap.getLastSnapFromMongo(orderId);

        return getDriverFare(snap,orderDataMap);
    }

    /**
     * 实际佣金总金额是否超出超出预估一口价阈值上下限
     * @param estimatedFixedFee 预估一口价
     * @param totalCommission 佣金总金额
     * @param thresholdBottom 预估一口价阈值下限
     * @param thresholdTop 预估一口价阈值上限
     * @return true 超出预估一口价阈值上下限, false otherwise
     */
    private static boolean isBeyondThreshold(double estimatedFixedFee, double totalCommission, double thresholdBottom, double thresholdTop){

        boolean beyond = false;

        /**
         * 预估一口价超阈=intval(预估一口价费用?(((预估一口价费用-佣金总金额)/预估一口价费用)>预估一口价阈值下限):1)
         * 预估一口价超阈=预估一口价超阈?预估一口价超阈:intval(预估一口价费用?(((佣金总金额-预估一口价费用)/预估一口价费用)>预估一口价阈值上限):1)
         */
        // Wrap code according to PHP formula.
        if ( estimatedFixedFee > 0 ) {
            double percentage = (totalCommission - estimatedFixedFee) / estimatedFixedFee;

            if (percentage < 0 && (-percentage) > thresholdBottom) {
                log.info("低于一口价阈值下限 : " + thresholdBottom);
                beyond = true;
            }

            if (!beyond && percentage > 0 && percentage > thresholdTop) {
                log.info("高于一口价阈值上限 : " + thresholdTop);
                beyond = true;
            }
        } else {
            beyond =  true; // 预估一口价为0，就应当取实际佣金总金额
        }

        return beyond;
    }

}

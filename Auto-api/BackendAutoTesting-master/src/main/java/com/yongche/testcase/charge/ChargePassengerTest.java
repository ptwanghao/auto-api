package com.yongche.testcase.charge;


import com.yongche.framework.CaseExecutor;
import com.yongche.framework.api.APIUtil;
import com.yongche.framework.api.order.Order;
import com.yongche.framework.core.EstimatePriceType;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import com.yongche.framework.api.charge.*;

/**
 * 测试预估价格API<BR>
 * http://zhengyun.dev-charge.yongche.org/test-platform/test.php <BR>
 * 计费公式: https://phab.yongche.org/F4656 <BR>
 * 补充说明:<BR>
 *     乘客端  预约和马上用车(随叫随到)属于非定价产品 不需要fixed_product_id<BR>
 *     乘客端  其它类型均为定价产品 需要fixed_product_id<BR>
 */
public class ChargePassengerTest {
    public static Logger log = LoggerFactory.getLogger(ChargePassengerTest.class);

    public static void getPrice(String car_type_id,
                                String product_type_id,
                                String city,
                                String fixed_product_id){

        //下面的三个值必须用代码设置 才能保证不同API是基于同样的车型取数据
        Map<String,TestParameter> baseParamMap = new HashMap<>();
        MapUtil mapUtil = MapUtil.getInstance(baseParamMap);

        mapUtil.put("car_type_id",car_type_id);
        mapUtil.put("product_type_id",product_type_id);
        mapUtil.put("city",city);
        //mapUtil.put("start_time","1549479240");//用于调试夜间服务费

        //获取免费长途公里数
        int freeLongDistance = FreeDistance.getFreeDistance(baseParamMap);
        log.info("免费长途公里数 (米) :" + freeLongDistance);

        if(null != fixed_product_id){
            MapUtil.put(baseParamMap,"fixed_product_id",fixed_product_id);
            MapUtil.put(baseParamMap,"fix_pro_id",fixed_product_id);//在API getIntervalEstimateCost()中 fix_pro_id 代表 fixed_product_id
        }

        String product_type_class = Price.getProductTypeClass(product_type_id);
        MapUtil.put(baseParamMap,"product_type_class",product_type_class);

        //根据价格类型 获取基础价格信息
        EstimatePriceType priceType = EstimatePriceType.PASSENGER;
        Map<String,TestParameter> basePriceMap = Price.getPrice(priceType,baseParamMap);

        //获取系统的预估总价
        Map<String,TestParameter> estimateMap = Estimate.getIntervalEstimateCost(baseParamMap);

        //根据基础价格表和乘客价格预估公式 计算预估总价
        double calPrice = Estimate.getPassengerEstimateCost(basePriceMap,estimateMap,freeLongDistance);

        //校验系统预估价格与计算所得的价格是否在允许的差值范围内
        String baseFareStr = JsonUtil.getValueFromResult(estimateMap,car_type_id,"原始订单基本费用");
        double sysEstimatedPrice = Double.parseDouble(baseFareStr);

        AssertUtil.assertDiff(sysEstimatedPrice,calPrice,2);
    }

    /**
     * 测试目的：<br>
     * 乘客能获取正确获取 非定价产品-预约用车-Young车型 的预估价格<br>
     *
     * 测试步骤：<br>
     * 1.获取免费长途公里数<br>
     * 2.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 3.获取系统的预估总价<br>
     * 4.根据基础价格表和乘客价格预估公式 计算预估总价<br>
     *   检查点：预约用车-Young车型 系统预估总价和计算所有预估总价差值在2以内<br>
     *
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public static void charge_getPrice_hourRent_shouldSucceed_youngCar(){
        getPrice(ParamValue.CAR_TYPE_ID_YOUNG,
                ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR,
                ParamValue.CITY_BEIJING,null);
    }

    /**
     * 测试目的：<br>
     * 乘客能获取正确获取 定价产品-送机-Young车型 的预估价格<br>
     *
     * 测试步骤：<br>
     * 1.获取免费长途公里数<br>
     * 2.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 3.获取系统的预估总价<br>
     * 4.根据基础价格表和乘客价格预估公式 计算预估总价<br>
     *   检查点：送机-Young车型 系统预估总价和计算所有预估总价差值在2以内<br>
     *
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public static void charge_getPrice_airportDropOff_shouldSucceed_youngCar(){
        getPrice(ParamValue.CAR_TYPE_ID_YOUNG,
                ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF,
                ParamValue.CITY_BEIJING,
                ParamValue.FIXED_PRODUCT_ID_AIRPORT_DROP_OFF_BJ);
    }

    //@Test
    public static void charge_getPrice_asap_shouldSucceed_luxuryCar(){
        getPrice(ParamValue.CAR_TYPE_ID_LUXURY,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                ParamValue.CITY_BEIJING,null);
    }

    //@Test
    public static void charge_getPrice_oneDayRent_shouldSucceed_comfortableCar(){
        getPrice(ParamValue.CAR_TYPE_ID_COMFORTABLE,
                ParamValue.PRODUCT_TYPE_ID_RENTAL_ONE_DAY,
                ParamValue.CITY_BEIJING,
                ParamValue.FIXED_PRODUCT_ID_RENTAL_ONE_DAY);
    }

}

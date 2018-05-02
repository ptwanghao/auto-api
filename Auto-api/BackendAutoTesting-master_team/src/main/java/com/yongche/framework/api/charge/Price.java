package com.yongche.framework.api.charge;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.EstimatePriceType;
import com.yongche.framework.core.ParamValue;
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
public class Price {
    public static Logger log = LoggerFactory.getLogger(Price.class);

    /**
     * 调用getPrice API<br>
     */
    public static Map<String,TestParameter> getPrice(EstimatePriceType priceType, Map<String, TestParameter> inputParamMap){
        String caseFile = null;

        switch(priceType){
            case PASSENGER:
                caseFile = "charge/charge_getPrice_passenger.xml";
                break;
            case ORDER:
                caseFile = "charge/charge_getPrice_order_deluxe.xml";
            default:
                break;
        }

        return CaseExecutor.runTestCase(caseFile,inputParamMap);
    }


    /**
     * 根据product_type_id获取product_type_class<br>
     * @param product_type_id  产品类型id
     * @return product_type_class
     */
    public static String getProductTypeClass(String product_type_id){
        String product_type_class = "2"; //产品类型（1为非定价，其它值为定价）

        //根据product_type_id判断是时租(定价)还是非定价
        switch(product_type_id){
            case ParamValue.PRODUCT_TYPE_ID_IMMEDIATE:
            case ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR:
                product_type_class = "1";//非定价
                break;
        }

        return  product_type_class;
    }

}

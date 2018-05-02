package com.yongche.framework.api.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.ConfigUtil;
import com.yongche.framework.utils.MapUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 *
 */
public class Order {
    public static Logger log = LoggerFactory.getLogger(Order.class);

    /**
     * 获取订单ID<br>
     * @return 由订单号和"order_id"创建的{@link TestParameter}对象
     */
    public static TestParameter getOrderId(String xmlFileName, Map<String, TestParameter> inputParameterMap, Map<String, TestParameter> expectedResultMap) {
        Map<String, TestParameter> expectedParams = CaseExecutor.runTestCase(xmlFileName, inputParameterMap,expectedResultMap);
        return expectedParams.get("order_id");
    }

    /**
     * 根据不同的订单类型创建订单<br>
     * @param inputParameterMap  必须包含product_type_id的TestParameter对象
     * @return 由订单号和"order_id"创建的{@link TestParameter}对象
     */
    //TODO: Add database check for order ID
    public static TestParameter createOrderByProductTypeId(Map<String,TestParameter> inputParameterMap){
        String fixedProductIdVal = "1";//0代表非固定价格 1 代表固定价格
        String typeId = inputParameterMap.get("product_type_id").getValue();

        //预约用车 和 随叫随到 要求fixed_product_id = 0,接机类型为38,送机35,其它为1
        switch(typeId){
            case  ParamValue.PRODUCT_TYPE_ID_AIRPORT_PICKUP:
                fixedProductIdVal = "38";
                break;
            case  ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF:
                fixedProductIdVal = "35";
                break;
            case  ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR:
            case  ParamValue.PRODUCT_TYPE_ID_IMMEDIATE:
                fixedProductIdVal = "0";
                break;
        }

        //TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", typeId);
        //TestParameter fixedProductId = TestParameter.getTestParameter("fixed_product_id",fixedProductIdVal);
        //Map<String,TestParameter> inputParameterMap = new HashMap<>();
        //inputParameterMap.put("product_type_id",product_type_id);
        //inputParameterMap.put("fixed_product_id",fixedProductId);
        MapUtil.put(inputParameterMap,"fixed_product_id",fixedProductIdVal);
        String caseFile = "state_createOrder_immediate.xml";

        //日租和半日租 用state_createOrder_rental_one_day.xml 且返回后需要调用confirm()接口
        //其它类型的order 用state_createOrder_immediate.xml
        switch (typeId){
            case  ParamValue.PRODUCT_TYPE_ID_AIRPORT_PICKUP:
                caseFile = "state_createOrder_airport_pickup.xml";
                break;
            case  ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF:
                caseFile = "state_createOrder_airport_dropOff.xml";
                break;
            case  ParamValue.PRODUCT_TYPE_ID_RENTAL_HALF_DAY:
            case  ParamValue.PRODUCT_TYPE_ID_RENTAL_ONE_DAY:
                caseFile = "state_createOrder_rental_one_day.xml";
                break;
        }

        return getOrderId(caseFile,inputParameterMap,null);
    }

    /**
     * 根据不同的订单类型创建订单<br>
     * @param typeId  订单类型 {@link TestParameter}
     * @return 由订单号和"order_id"创建的{@link TestParameter}对象
     */
    public static TestParameter createOrderByProductTypeId(String typeId){
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("product_type_id",typeId);
        return createOrderByProductTypeId(inputParameterMap);
    }

    public static TestParameter createOrderByProductTypeId(String typeId, Map<String,TestParameter> inputParameterMap ){

        if(null ==inputParameterMap){
            inputParameterMap = MapUtil.getParameterMap("product_type_id",typeId);
        }else{
            MapUtil.put(inputParameterMap,"product_type_id",typeId);
        }

        return createOrderByProductTypeId(inputParameterMap);
    }

    public static void cancelOrder(Map<String, TestParameter> inputParameterMap) {
        CaseExecutor.runTestCase("state_cancelOrder_success.xml", inputParameterMap);
    }

    public static void cancelOrderById(TestParameter order_id) {
        Map<String, TestParameter> inputParamMap = MapUtil.getParameterMap("order_id", order_id.getValue());
        cancelOrder(inputParamMap);
    }

    public static void setDriverDepart(String orderId){
        String where = "array('order_id'=> '" + orderId +"')";
        Map<String,TestParameter> departInputMap =MapUtil.getParameterMap("where",where);
        CaseExecutor.runTestCase("order_setDriverDepart.xml",departInputMap);
    }

    public static boolean hasDriverResponse(String orderId, int timeout){
        boolean ret = false;

        //每10秒循环一次
        int interval = 10*1000;
        int loopCount = timeout / interval;

        int status = -1;

        while (status < 4 && loopCount >0){
            loopCount --;
            String value = getStatus(orderId);
            status = Integer.parseInt(value);
            CommonUtil.sleep(interval);
        }

        if(status == 4){
            ret = true;
        }
        //Assert.assertEquals(status , 4,"status is " + status);

        return  ret;
    }

    public static boolean hasDriverResponse(String orderId){
        int timeout = ParamValue.DRIVER_RESPONSE_TIMEOUT;
        return hasDriverResponse(orderId,timeout);
    }

    public static String getStatus(String orderId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);
        Map<String, TestParameter> outputParamMap = CaseExecutor.runTestCase("order_getStatus.xml",paraMap);
        String status = outputParamMap.get("status").getValue();

        log.info("order status: " + status);
        return status;
    }

    public static String getDriverIdByOrderId(String orderId){
        Map<String, TestParameter> outputParamMap =  getFieldsByOrderId(orderId);
        return outputParamMap.get("driver_id").getValue();
    }

    public static String getCarIdByOrderId(String orderId){
        Map<String, TestParameter> outputParamMap =  getFieldsByOrderId(orderId);
        return outputParamMap.get("car_id").getValue();
    }

    public static Map<String, TestParameter>  getFieldsByOrderId(String orderId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("service_order_id",orderId);
        Map<String, TestParameter> outputParamMap = CaseExecutor.runTestCase("order_getFieldsByOrderId.xml",paraMap);
        return outputParamMap;
    }

    /**
     * 用于处理几个查询接口的返回结果，将f返回内容格式化为json格式
     */
    public static JSONObject getJsonResult(Map<String, TestParameter> result){
        //取出 response中RESPONSE_RESULT字段的value
        String res = result.get("RESPONSE_RESULT").getValue();
        /*
        //要解析出json需要在返回值的success字段两边添加双引号
        int start = res.indexOf("success");
        int len = "success".length();
        int end = start + len + 1;
        StringBuilder res_sb = new StringBuilder(res);//构造一个StringBuilder对象
        res_sb.insert(start, "\"");
        res_sb.insert(end, "\"");
        res = res_sb.toString();
        //解析json 获取当前订单列表
        */
        JSONObject  jsonObject = JSONObject.fromObject(res);
        return  jsonObject;
    }
    /**
     * 用于处理getJsonResult方法返回的数据，返回其中某个节点
     */
    public static JSONArray getOrderList(Map<String,TestParameter> result,String list_name){
        JSONObject json_result = (JSONObject)getJsonResult(result).get("result");
        JSONArray order_list = (JSONArray) json_result.get(list_name);
        return order_list;
    }
    public static JSONArray getOrderList(Map<String,TestParameter> result){
        return (JSONArray)getJsonResult(result).get("result");
    }

    public static boolean checkOrderInList(String order_id,JSONArray order_list){
        boolean flag = false;
        for(int i=0; i<order_list.size();i++){
            JSONObject json_order = (JSONObject)order_list.get(i);
            String service_order_id = json_order.get("service_order_id").toString();
            if(order_id.equals(service_order_id)){
                flag = true;
                break;
            }
        }
        return flag;
    }


}

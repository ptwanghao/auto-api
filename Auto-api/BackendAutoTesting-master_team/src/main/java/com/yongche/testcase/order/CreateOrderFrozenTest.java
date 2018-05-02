package com.yongche.testcase.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Map;

public class CreateOrderFrozenTest {

    public static Logger log = LoggerFactory.getLogger(CreateOrderFrozenTest.class);

    /**
     * 测试目的：<br>
     * 个人账户（ERP手动修改）风控冻结，禁止下单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建订单参数,其中用户状态为被冻结状态
     * 2.调用createOrder()创建一个订单<br>
     * 3.检查接口返回结果<br>
     *   检查点："hint": "您的账户有恶意下单或作弊行为，已冻结"<br>
     * <br>
     * 测试用例开发人员：罗文平<br>
     */
    @Test
    public void order_createOrder_Frozen_should_failed(){
        //构造请求参数
        RiderInfo riderInfo = CommonUtil.getRider(RiderCategoryConst.FROZEN_USER); //该账号状态为冻结状态
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id", riderInfo.getUserId());
        MapUtil.put(inputParameterMap,"passenger_phone", riderInfo.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", riderInfo.getPassengerName());
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParameterMap,"product_type_id",ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParameterMap,"car_type","Young车型");

        //创建订单
        String caseFile = "state_createOrder_frozen.xml";
        CaseExecutor.runTestCase(caseFile,inputParameterMap);
    }

    /**
     * 测试目的：<br>
     * 企业账户（ERP手动修改）风控冻结，禁止下单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建企业用户订单参数,其中用户状态为被冻结状态
     * 2.调用createOrder()创建一个订单<br>
     * 3.检查接口返回结果<br>
     *   检查点："hint": "您所选的集团账户已失效，请联系管理员咨询解决"<br>
     * <br>
     * 测试用例开发人员：罗文平<br>
     */
    @Test
    public void order_createOrder_FrozenCorporation_should_failed(){
        //构造请求参数
        RiderInfo riderInfo = CommonUtil.getRider(RiderCategoryConst.FROZEN_USER); //该账号状态为冻结状态
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id", riderInfo.getUserId());
        MapUtil.put(inputParameterMap,"passenger_phone", riderInfo.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", riderInfo.getPassengerName());
        String corporate_id = CorporateInfo.getCorporateInfo(CorporateInfoConst.CORP_WITHOUT_DEPT).getId();
        MapUtil.put(inputParameterMap,"corporate_id", corporate_id);
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParameterMap,"product_type_id",ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParameterMap,"car_type","Young车型");


        //创建订单
        String caseFile = "state_createOrder_frozen_corporate.xml";
        CaseExecutor.runTestCase(caseFile,inputParameterMap);
    }
}

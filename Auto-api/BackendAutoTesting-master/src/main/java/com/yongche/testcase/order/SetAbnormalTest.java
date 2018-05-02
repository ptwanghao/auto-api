package com.yongche.testcase.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.core.DbTableColumn;
import com.yongche.framework.utils.XMLUtil;
import com.yongche.framework.utils.db.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.AfterTest;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试接口: state/setAbnormal
 * 接口说明: 该接口用于用户对订单有疑问时提交异议信息，请求接口后会在yc_order_global数据库的abnormal_record表中新插入一条记录
  */
public class SetAbnormalTest {
    public static Logger log = LoggerFactory.getLogger(SetAbnormalTest.class);

    /**
     * 测试目的：<br>
     * 确保setAbnormal在数据库中插入新的异常订单记录，且记录内容正确<br>
     * <br>
     * 测试步骤：<br>
     * 1.查询指定订单号的所有异议记录个数<br>
     * 2.调用setAbnormal() 插入一条异常记录<br>
     * 3.检验插入后订单异议记录个数 = 原异议记录个数 + 1 <br>
     *   检查点：数据库表abnormal_record有新增记录<br>
     * 4.校验新插入的记录内容与提交内容一致 <br>
     *   检查点;新增记录的source、message字段与传入参数一致，create_time在当前时间120秒前（非必须）<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    //@Test
    public void order_setAbnormal_shouldSucceed_withValidParameters() throws Exception{
        //获取set_abnormal.xml中的input参数
        Map<String, TestParameter> parameters = XMLUtil.getParameterFromXML("state_setAbnormal.xml","SetAbnormalSuccess");

        //数据库查询setAbnormal接口被调用之前的订单异常信息记录个数abnormal_count

        String str_sql = "SELECT count(*) FROM abnormal_record where service_order_id=" + parameters.get("order_id").getValue();
        String abnormal_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER_GLOBAL,str_sql,DbTableColumn.COUNT);
        log.info("订单初始异常记录数为:" + abnormal_count);

        //调用setAbnormal
        CaseExecutor.runTestCase("state_setAbnormal.xml");

        //数据库查询setAbnormal接口被调用之后的订单异常信息记录个数abnormal_count_after
        String abnormal_count_after = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER_GLOBAL,str_sql,DbTableColumn.COUNT);
        log.info("case执行后订单异常记录数为:" + abnormal_count_after);

        //检验 插入后订单异议记录个数 = 原异议记录个数 + 1
        Assert.assertEquals(Integer.parseInt(abnormal_count)+1,Integer.parseInt(abnormal_count_after));

        //比对异常记录的内容是否正确 source message字段完全相同，createtime 在最近120s内
        String str_sql_all = "SELECT * FROM abnormal_record where service_order_id = " + parameters.get("order_id").getValue() + " order by abnormal_record_id desc limit 1";
        String source = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER_GLOBAL,str_sql_all,DbTableColumn.ABNORMAL_RECORD_SOURCE);
        Assert.assertEquals(parameters.get("source").getValue(),source);
        String message = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER_GLOBAL,str_sql_all,DbTableColumn.ABNORMAL_RECORD_MESSAGE);
        Assert.assertEquals(parameters.get("message").getValue(),message);
        String create_time = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER_GLOBAL,str_sql_all,DbTableColumn.ABNORMAL_RECORD_CREATE_TIME);
        Assert.assertTrue( (Long.parseLong(create_time)+120L) >= System.currentTimeMillis()/1000);//非必须条件，考虑数据库延迟等因素期望记录插入时间在2分钟以内

    }

    /**
     * 测试目的： <br>
     * 当setAbnormal接口传入一个不存在的订单时，返回订单不存在错误 <br>
     * <br>
     * 测试步骤： <br>
     * 1.设置接口参数order_id为一个不存在的订单 1000100010001000100  <br>
     * 2.设置期望的返回值  <br>
     * 3.调用setAbnormal 校验返回结果 <br>
     *   检查点：返回结果ret_code和ret_msg校验与设置的期望值一致<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    //@Test
    public void order_setAbnormal_shouldFailed_whenOrderIdIsNotExist() throws Exception{
        //构造要传入的参数,一个不存在的订单id
        Map<String, TestParameter> inputParams = new HashMap<>();
        TestParameter order_id = new TestParameter("order_id","1000100010001000100");
        inputParams.put("order_id",order_id);

        //期望返回的结果,期望返回404 订单不存在
        Map<String, TestParameter> expectedResult = new HashMap<>();
        TestParameter ret_code = new TestParameter("ret_code","404");
        TestParameter ret_msg = new TestParameter("ret_msg","select for update data is empty, service_order_id: 1000100010001000100");
        expectedResult.put("ret_code",ret_code);
        expectedResult.put("ret_msg",ret_msg);

        //调用setAbnormal
        CaseExecutor.runTestCase("state_setAbnormal.xml",inputParams,expectedResult);
    }

    /**
     * 测试目的：<br>
     * 测试用例执行完毕后销毁测试数据<br>
     * <br>
     * 测试步骤：<br>
     * 1.获取xml文件中测试数据<br>
     * 2.执行sql语句，删除SetAbnormal()方法插入的记录<br>
     * 3.如果执行sql返回影响行数小于1，则输出warning日志<br>
     */
    //@AfterTest
    public void teardown(){
        //获取set_abnormal.xml中传入的参数
        Map<String, TestParameter> parameters = XMLUtil.getParameterFromXML("state_setAbnormal.xml","SetAbnormalSuccess");

        //删除setAbnormal()方法新增的异议信息记录
        log.info("=======================销毁SetAbnormal测试数据==========================");
        String str_sql = "delete from abnormal_record where service_order_id=" + parameters.get("order_id").getValue();
        int res = DBUtil.updateDB(ConfigConst.YC_ORDER_GLOBAL,str_sql);
        if (res < 1){
            log.warn("销毁SetAbnormal测试数据失败！");
        }
    }

}

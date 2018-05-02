package com.yongche.framework.core;

import com.yongche.framework.api.order.Order;
import com.yongche.framework.utils.db.AccountDBUtil;
import com.yongche.framework.utils.db.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 清除测试数据
 */
public class TearDown {
    public static Logger log = LoggerFactory.getLogger(TearDown.class);

    public static void cancelAndDeleteOrder(String new_order_id){
        log.info("Order id is —— " + new_order_id);
        //获取订单当前状态
        TestParameter param_order_id = TestParameter.getTestParameter("order_id",new_order_id);
        int current_status = Integer.parseInt(Order.getStatus(new_order_id));
        //如果状态不为结束或取消 取消订单释放司机
        if(OrderStatus.CANCELED != current_status && OrderStatus.FINISHED != current_status){
            Order.cancelOrderById(param_order_id);
        }
        //已经为结束或取消状态的订单则直接删除
        //2017.2.20 功能测试组要求订单保留，所以暂注释掉删除数据库操作
        //teardownForCreateOrder(new_order_id);
    }

    public static void deleteDbByOrderId( String order_id,String table_name){
        Logger log = LoggerFactory.getLogger(TearDown.class);
        //delete record created by createOrder from db
        log.info("=====================delete the test data created by createOrder======================");
        String str_sql = "DELETE FROM " + table_name + " where service_order_id=" + order_id;
        int res = DBUtil.updateDB(ConfigConst.YC_ORDER,str_sql);
        if (res < 1){
            log.warn("teardown the test data created by createOrder failed！table name is :" + table_name);
        }else{
            log.info("teardown record from table " + table_name + " count :" + res);
        }
    }

    //销毁创建订单测试用例的测试数据
    public static void teardownForCreateOrder(String order_id){
        deleteDbByOrderId(order_id,"service_order");
        deleteDbByOrderId(order_id,"service_order_ext");
        deleteDbByOrderId(order_id,"order_key_value");
    }

    //删除优惠券数据
    public static void deleteCouponByCouponId(String coupon_id,String table_name){
        Logger log = LoggerFactory.getLogger(TearDown.class);
        //delete coupon test data from db
        log.info("=====================delete the coupon test data======================");
        String str_sql = "DELETE FROM " + table_name + " where user_coupon_id=" + coupon_id;
        int res = DBUtil.updateDB(ConfigConst.YC_COUPON,str_sql);
        if (res < 1){
            log.warn("teardown the coupon test data failed！table name is :" + table_name);
        }else{
            log.info("teardown record from table " + table_name + " count :" + res);
        }
    }

    //销毁优惠券测试数据
    public static void teardownForCoupon(String user_id,String user_coupon_id){
        //根据user_id计算分表编号
        int db_num = ( Integer.parseInt(user_id) % 1000 ) % 256 + 1 ;
        String table_name = "user_coupon_";
        if(db_num<10){
            table_name = table_name + "00" + db_num;
        }else if(db_num<100){
            table_name = table_name + "0" + db_num;
        }else {
            table_name = table_name + db_num;
        }
        deleteCouponByCouponId(user_coupon_id,table_name);
    }

    /**
     * 根据sql语句销毁测试数据
     * @param str_sql 需要执行的sql语句
     * @param table_name 操作的数据库表名称
     */
    public static void destoryTestData(String dbName,String str_sql,String table_name){
        Logger log = LoggerFactory.getLogger(TearDown.class);
        int res = DBUtil.updateDB(dbName,str_sql);
        if (res < 1){
            log.warn("teardown test data failed！table name is :" + table_name);
        }else{
            log.info("teardown test data from table " + table_name + " count :" + res);
        }
    }

    /**
     * 根据sql语句销毁测试数据
     * @param str_sql 需要执行的sql语句
     * @param table_name 操作的数据库表名称
     */
    public static void destoryAccountTestData(int dbIndex,String str_sql,String table_name){
        Logger log = LoggerFactory.getLogger(TearDown.class);
        int res = AccountDBUtil.updateAccountDB(dbIndex,str_sql);
        if (res < 1){
            log.warn("teardown test data failed！table name is :" + table_name);
        }else{
            log.info("teardown test data from table " + table_name + " count :" + res);
        }
    }

    /**
     * 调整订单金额以完成付费，确保订单服务完成
     * @param orderId 订单ID
     */
    public static void adjustAmountToCompleteOrder(String orderId){
        Order.adjustAmount(orderId);
    }
}

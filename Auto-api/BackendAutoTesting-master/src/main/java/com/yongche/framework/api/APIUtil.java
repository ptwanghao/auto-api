package com.yongche.framework.api;


import com.yongche.framework.api.order.Order;
import com.yongche.framework.api.order.State;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.utils.ConfigUtil;
import org.slf4j.*;



/**
 * 对API调用的一些封装 方便编写测试用例
 */
public class APIUtil {
    public static Logger log = LoggerFactory.getLogger(APIUtil.class);

    public static boolean acceptOrder(String orderId){
        String value = ConfigUtil.getValue(ConfigConst.DRIVER_INFO,"driver_response_timeout");
        int timeout = Integer.parseInt(value)*1000;

        boolean ret = Order.hasDriverResponse(orderId,timeout);

        if(ret) {
            afterAcceptOrder(orderId);
        }

        return ret;
    }

    public static void afterAcceptOrder(String orderId){
        Order.setDriverDepart(orderId);

        State.driverArrived(orderId);

        State.startTiming(orderId);

        State.endTiming(orderId);

        State.confirmCharge(orderId);

        Order.getStatus(orderId);
    }
}


/*
        long time = (new Date()).getTime() + 3600*1000;

        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(time);

        System.out.println("Format To String(Date):"+d);
        //Date date= format.parse(d);
        //System.out.println("Format To Date:"+date);
*/

package com.yongche.framework.api.order;

import com.yongche.framework.utils.psf.PSFUtil;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import java.util.*;

public class DriverAgent {

    public static Logger log = LoggerFactory.getLogger(DriverAgent.class);

    public static boolean driverRedispatch(String service_order_id,String driver_id,String bidding_id,String deduct_fine,String deduct_marks,String driver_base_rights_id){
        String service_type="dispatch";
        String service_url="Dispatch/driverRedispatch";
        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("service_order_id",service_order_id));
        formParameters.add(new BasicNameValuePair("driver_id",driver_id));
        formParameters.add(new BasicNameValuePair("bidding_id",bidding_id));
        formParameters.add(new BasicNameValuePair("deduct_fine",deduct_fine));
        formParameters.add(new BasicNameValuePair("deduct_marks",deduct_marks));
        formParameters.add(new BasicNameValuePair("driver_base_rights_id",driver_base_rights_id));
        String response = PSFUtil.sendRequest(service_type,service_url,formParameters);
        JSONObject obj = JSONObject.fromObject(response);
        String ret_code = obj.getString("ret_code");
        String ret_msg = obj.getString("ret_msg");
        Assert.assertEquals(ret_code,"200",ret_msg);
        if( (200 == Integer.parseInt(ret_code)) && "success".equals(ret_msg)){
            return true;
        }
        return false;
    }

    public static int getChangeDriverStatus(String service_order_id,String driver_id,String bidding_id){
        String service_type="dispatch";
        String service_url="Dispatch/getChangeDriverStatus";
        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("service_order_id",service_order_id));
        formParameters.add(new BasicNameValuePair("driver_id",driver_id));
        formParameters.add(new BasicNameValuePair("bidding_id",bidding_id));
        String response = PSFUtil.sendRequest(service_type,service_url,formParameters);
        JSONObject obj = JSONObject.fromObject(response);
        int ret_code = obj.getInt("ret_code");
        return ret_code;
    }

}

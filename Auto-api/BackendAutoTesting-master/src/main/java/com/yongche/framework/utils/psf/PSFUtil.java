package com.yongche.framework.utils.psf;

import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.JsonUtil;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class PSFUtil {
    public static Logger log = LoggerFactory.getLogger(PSFUtil.class);
    private static final int PSF_TIMEOUT = 30*1000;

    public static String sendRequest(String service_type,
                String service_uri,
                List<NameValuePair> paramList){
        String response = null;
        PSFClient psfClient = null;

        try {
            String [] serviceCenter = CommonUtil.getServiceCenter();
            psfClient = new PSFClient(serviceCenter);

            //psfClient = new PSFClient(serviceCenter, 30000, 30000, 4096, 10,0,15000);

            log.info("PSF Service type : " + service_type);
            log.info("PSF Service uri : " + service_uri);
            log.info("PSF Service center : " + Arrays.asList(serviceCenter));

            //Add psf request data
            JSONObject json; //= JSONObject.fromObject();
            PSFClient.PSFRPCRequestData request = new PSFClient.PSFRPCRequestData();

            request.service_uri = service_uri + "?"+ URLEncodedUtils.format(paramList, "UTF-8");
            request.data = "";//URLEncodedUtils.format(paramList, "UTF-8");
            log.info("PSF Request data : " + request.data);
            log.info("PSF Request uri : " + request.service_uri);

            //Send PSF Request to service center
            response = psfClient.call(service_type, request, PSF_TIMEOUT);
            log.info("PSF response : " + response);
        }catch (Exception e){
            log.error("Exception :" + e.getMessage());
        }finally {
            try{
                if(psfClient != null){
                    psfClient.close();
                }
            }catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        //return JsonUtil.JsonToHashMap(response);
        return response;
    }
}

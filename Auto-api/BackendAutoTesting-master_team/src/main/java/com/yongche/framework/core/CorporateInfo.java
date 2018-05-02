package com.yongche.framework.core;


import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.config.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CorporateInfo {
    public static Logger log = LoggerFactory.getLogger(CommonUtil.class);
    private static final List<CorporateInfo> s_corporateList;

    String id;
    String departmentId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    static {
        s_corporateList = new ArrayList<>();// 企业信息列表
        String corporateDataFile = ConfigUtil.getValue(ConfigConst.CORPORATE_INFO, "corporate_info_data");

        try {
            File file = new File(corporateDataFile);
            FileReader reader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(reader);
            String line;

            while ((line = buffer.readLine()) != null) {
                if (line.charAt(0) == '#')
                    continue;
                String[] values = line.split(",");
                CorporateInfo corp = new CorporateInfo();
                corp.setId(values[0]);
                corp.setDepartmentId(values[1]);

                s_corporateList.add(corp);
            }
        }catch (Exception e){
            log.info("Failed to read corporate data info. " + e.getMessage());
        }
    }

    public static CorporateInfo getCorporateInfo(int index){
        return ( s_corporateList.size() > index && index >=0 ) ? s_corporateList.get(index) : null;
    }


}

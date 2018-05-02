package com.yongche.testcase.payment;

import com.yongche.framework.api.payment.Account;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.MapUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class AccountTest {


    public static void reduceAccountAmount(String balanceType){
        String accountId ="75000000778";

        //获取用户的帐户余额
        int amount = Account.getAccountAmount(accountId,balanceType);

        //添加ERP余额
        int reduceAmount = 100;//1元
        Map<String,TestParameter> inputParaMap = new HashMap<>();
        MapUtil.put(inputParaMap,"account_id",accountId);
        MapUtil.put(inputParaMap,"amount",String.valueOf(reduceAmount));
        MapUtil.put(inputParaMap,"balance_type",balanceType);
        Account.reduceAmount(inputParaMap);

        //再次获取用户的帐户余额
        int finalAmount = Account.getAccountAmount(accountId,balanceType);

        //判断ERP是否增加了指定金额
        Assert.assertEquals(reduceAmount,(amount - finalAmount));
    }

    public static void addAccountAmount(String balanceType){
        String accountId ="75000000778";

        //获取用户的帐户余额
        int amount = Account.getAccountAmount(accountId,balanceType);

        //添加ERP余额
        int addAmount = 100;//1元
        Map<String,TestParameter> inputParaMap = new HashMap<>();
        MapUtil.put(inputParaMap,"account_id",accountId);
        MapUtil.put(inputParaMap,"amount",String.valueOf(addAmount));
        MapUtil.put(inputParaMap,"balance_type",balanceType);
        Account.addAmount(inputParaMap);

        //再次获取用户的帐户余额
        int finalAmount = Account.getAccountAmount(accountId,balanceType);

        //判断ERP是否增加了指定金额
        Assert.assertEquals(addAmount,(finalAmount - amount));
    }

    /**
     * 测试目的：<br>
     * 用户ERP主帐户添加余额功能正常.<br>
     * <br>
     * 测试步骤：<br>
     * 1.获取指定用户(account_id 对应的 user_id)的余额
     * 2.给用户添加余额
     * 3.检查用户的余额
     *   检查点：用户的新余额=原余额+添加的余额
     * <br>
     * 测试用例开发人员：马毅<br>
     */
    @Test
    public void addMainAccountBalance(){
        addAccountAmount(ParamValue.BALANCE_TYPE_MAIN);
    }

    /**
     * 测试目的：<br>
     * 用户ERP副帐户添加余额功能正常.<br>
     * <br>
     * 测试步骤：<br>
     * 1.获取指定用户(account_id 对应的 user_id)的余额
     * 2.给用户添加余额
     * 3.检查用户的余额
     *   检查点：用户的新余额=原余额+添加的余额
     * <br>
     * 测试用例开发人员：马毅<br>
     */
    @Test
    public void addMainAccountSubBalance(){
        addAccountAmount(ParamValue.BALANCE_TYPE_SUB);
    }

    /**
     * 测试目的：<br>
     * 用户ERP主帐户减少余额功能正常.<br>
     * <br>
     * 测试步骤：<br>
     * 1.获取指定用户(account_id 对应的 user_id)的余额
     * 2.给减少用户余额
     * 3.检查用户的余额
     *   检查点：用户的新余额=原余额-减去的余额
     * <br>
     * 测试用例开发人员：马毅<br>
     */
    @Test
    public void reduceMainAccountBalance(){
        reduceAccountAmount(ParamValue.BALANCE_TYPE_MAIN);
    }

    /**
     * 测试目的：<br>
     * 用户ERP副帐户减少余额功能正常.<br>
     * <br>
     * 测试步骤：<br>
     * 1.获取指定用户(account_id 对应的 user_id)的余额
     * 2.给减少用户余额
     * 3.检查用户的余额
     *   检查点：用户的新余额=原余额-减去的余额
     * <br>
     * 测试用例开发人员：马毅<br>
     */
    @Test
    public void reduceMainAccountSubBalance(){
        reduceAccountAmount(ParamValue.BALANCE_TYPE_SUB);
    }
}

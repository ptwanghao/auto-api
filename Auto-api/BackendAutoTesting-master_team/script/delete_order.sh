#!/bin/bash

mysqlOrder="mysql -h10.0.11.158 -P3306 -uorder -porder -Dyc_order "

#待查询的手机号码
cellphone="16801355462,16888800000,16811070001,16800000076"


function execSql()
{
	sqlstr="$1"
	sqlstr="$mysqlOrder  -e \"$sqlstr\""
	echo -e "$sqlstr\n"
	eval $sqlstr
}


#查找乘客测试帐户创建的订单 service_order表 
sqlstr="select service_order_id from service_order where passenger_phone in ($cellphone)"
execSql "$sqlstr"


#查找乘客测试帐户创建的订单 service_order_ext表 
sqlstr="select ext.service_order_id  as service_order_id_from_ext from service_order_ext as ext, service_order as orders where orders.passenger_phone in ($cellphone) and ext.service_order_id = orders.service_order_id"
execSql "$sqlstr"


#先删除service_order_ext表 中测试帐户创建的订单
echo  -e "\n=====Deleting data from service_order_ext====="
sqlstr="delete ext.* from service_order_ext as ext, service_order as orders where orders.passenger_phone in ($cellphone) and ext.service_order_id = orders.service_order_id"
execSql "$sqlstr"


#再删除service_order表中测试帐户创建的订单
echo -e "\n=====Deleting data from serice_order====="
sqlstr="delete from service_order where passenger_phone in ($cellphone)"
execSql "$sqlstr"

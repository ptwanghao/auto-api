#!/bin/bash
##################使用方法##################
#1、配置数据库mysql和mysql2
#2、指定要新建的车型car_type_id
#3、指定要新建司机个数create_car_num
#4、确保script_dir路径下有绑定设备脚本add_device.php 并把app_version改成300
#5、在10.0.11.235机器上执行该脚本,登录名testing密码yongche
#6、脚本执行结果保存在driverInfo.txt

mysql="/usr/bin/mysql -h10.0.11.229 -P3306 -uyongche"
mysql2="/usr/bin/mysql -h10.0.11.229 -P3307 -uyongche"
car_type_id=2  #2舒适  3豪华  4奢侈 37Young
create_car_num=2
script_dir="/home/testing/git/driver-api/utils/run_once"

if [ -n "$1" ];then
    car_type_id="$1"
fi

#获取最后一个车牌号 再此基础上自增
max_vehicle_number=`${mysql} -Dyc_crm_common -e"select vehicle_number from car where vehicle_number like '自D%'  order by vehicle_number desc limit 1;" | awk -F 'D' '{print $2}' | sed s'/^0*//'`
printf "\n------------当前最新的车牌号尾数--------------\n"
echo $max_vehicle_number

#构造要创建的车牌号
printf  "\n----------新建车辆的车牌号列表----------\n"
for((i=0;i<$create_car_num;i++)){
    #自D开头 + 5位数字 ，不足5位用0补全
    new_vehicle_number[$i]=$[$max_vehicle_number+1+$i]
    len=`expr length ${new_vehicle_number[$i]}`
    if(( len<1 || len>5 ));then
        echo "新增车牌号不合法"
        exit 1
    fi
    for((j=0;j<$[5-$len];j++)){
        new_vehicle_number[$i]='0'${new_vehicle_number[$i]}
    }
    new_vehicle_number[$i]="自D"${new_vehicle_number[$i]}
    echo ${new_vehicle_number[$i]}
}

#构建要创建的车辆信息
printf "\n------------要插入数据库的车辆信息--------------\n"
for((i=0;i<$create_car_num;i++)){
    insert_car_info[$i]="(${car_type_id},${car_type_id},'${new_vehicle_number[$i]}',3,'奥迪A3',1,1,4),"
    echo ${insert_car_info[$i]}
}

#根据车辆个数构造插入sql语句
insert_car_sql="INSERT INTO car (car_type_id,base_car_type_id,vehicle_number,color,brand,car_brand_id,car_model_id,seat_num) VALUES"
for((i=0;i<$create_car_num;i++)){
    insert_car_sql+=${insert_car_info[$i]}
}
insert_car_sql=`echo $insert_car_sql|sed -e 's/,$/;/'`
printf "\n--------------插入car表的sql语句------------\n"
echo $insert_car_sql

printf "\n--------------执行sql,insert into table car----------------\n"
${mysql} -Dyc_crm_common -e"${insert_car_sql}"
if [ $? -eq 0 ];then
    echo "执行成功"
else
    echo "执行失败"
    exit 2
fi

#根据插入的车牌号查询新建的car_id
 printf "\n--------------新建car_id------------\n"
for((i=0;i<$create_car_num;i++)){
    car_id[$i]=`${mysql} -Dyc_crm_common -e"select car_id from car where vehicle_number='${new_vehicle_number[$i]}' limit 1;"|sed s'/car_id//'|sed -e s'/^[ ]*//g' `
    echo ${car_id[$i]}
}

#查询司机表，获得当前最新的司机手机号
printf "\n-------------当前最后的司机手机号------------\n"
last_driver_phone=`${mysql} -Dyc_crm_common -e"select cellphone from driver where name like '自D司机%' order by cellphone desc limit 1;"|sed s'/cellphone//'`
echo $last_driver_phone

#与car_id对应，构造司机信息
printf "\n-------------待创建的司机信息------------\n"
for((i=0;i<$create_car_num;i++)){
    driver_info[$i]="(${car_id[$i]},'自D司机${car_id[$i]}',$[$last_driver_phone+$i+1],'cn',0,20,0,1,1000,1,10000,1,61440,100,1),"
    driver_info[$i]=`echo ${driver_info[$i]}|sed -e 's/[ ]*//g'`
    echo ${driver_info[$i]}
}

#插入司机表的sql
insert_driver_sql="INSERT INTO driver (car_id,name,cellphone,country,silence_end_at,audit_status,work_status,surpport_face_pay,evaluation,driver_level,contribution,is_self_employed,flag,good_comment_rate,driver_type) VALUES"
for((i=0;i<$create_car_num;i++)){
    insert_driver_sql+=${driver_info[$i]}
}
insert_driver_sql=`echo $insert_driver_sql|sed -e 's/,$/;/'`
printf "\n-------------插入driver表sql------------\n"
echo $insert_driver_sql

#插入操作
printf "\n-------------执行sql,insert into table driver------------\n"
${mysql} -Dyc_crm_common -e"${insert_driver_sql}"
if [ $? -eq 0 ];then
    echo "执行成功"
else
    echo "执行失败"
    exit 3
fi

#查询一下新增的司机和车辆信息
printf "\n-------------新插入的司机和车辆信息------------\n"
for((i=0;i<$create_car_num;i++)){
    driver_car_info[$i]=`${mysql} -Dyc_crm_common -e"SELECT d.driver_id, device_id, 'DR','TOKEN',c.car_id, c.vehicle_number, d.name FROM driver AS d JOIN car AS c ON d.car_id=c.car_id and c.car_id=${car_id[$i]}" `
    echo ${driver_car_info[$i]}
    driver_id[$i]=`echo ${driver_car_info[$i]}|awk -F ' ' '{print $8}'`
    echo ${driver_id[$i]}
}

#add_device脚本
printf "\n--------------------绑定设备号--------------------\n"
len=${#driver_id[*]}
echo "待绑定的司机数："$len
for((j=0;j<"len";j++))
do
    php $script_dir"/"qa_add_device.php ${driver_id[$j]} ${car_id[$j]}
    #php $script_dir"/"add_device.php 50060113 50070297
    echo driver is ${driver_id[$j]} , car is ${car_id[$j]}
    printf "\n"
done

#在3307数据库 拿到工程需要的司机数据返回
printf "\n-------------------工程需要的最终数据--------------------\n"
echo `date "+%Y-%m-%d %H:%M:%S"`>>driverInfo.txt
for((i=0;i<$len;i++))
do
    data[$i]=`${mysql2} -Dyc_device_center_statistic -e"select user_id as 'driver_id',device_id,'DR',token from device where user_id=${driver_id[$i]}"`
    device_id[$i]=`echo ${data[$i]}|awk -F ' ' '{print $6}'`
    token[$i]=`echo ${data[$i]}|awk -F ' ' '{print $8}'`
    data[$i]=${driver_id[$i]}","${device_id[$i]}",DR,"${token[$i]}","${car_id[$i]}
    data[$i]=`echo ${data[$i]}|sed 's/ //'`
    echo ${data[$i]}
    echo ${data[$i]}>>driverInfo.txt
done

printf "\n------------------结束-------------------\n"

#!/ubin/bash

mysql="/usr/bin/mysql -h10.0.11.229 -P3306 -uyongche"
car_type_array=(2 3 4 37)
car_type_id=0 #车型为0时默认更新所有车型
param1=$1

function check_car_type(){
    flag=0
    for i in "${car_type_array[@]}";
    do
        if [ "$i" = "$param1" ];then
            flag=1
            car_type_id="$param1"
            echo "update driver's car_type_id is $param1"
            break
        fi
    done
    return $flag;
}


#参数1是指定更新某个车型的司机信息
if [ -n "$param1" ];then
    check_car_type;
    if [[ $? -eq 0 ]];then
        echo "param 1 should be in (2,3,4,37) "
        exit 1;
    fi
fi

car_type_append="car.car_type_id=${car_type_id}"
if [ ${car_type_id} -eq 0 ];then
    car_type_append=""
fi

driver_array=`${mysql} -Dyc_crm_common -e'select driver.driver_id  from  driver join car on driver.car_id=car.car_id and driver.name like "自D司机%" and '${car_type_append}' limit 2;'`

echo $driver_array

update_url=" http://newtest.merchant.yongche.org/V1/Driver/updateDriverById?work_status=0&evaluation=1000&driver_level=5&contribution=30001&has_logistics=1&has_qualified=1&good_comment_rate=100&good_comment_count=38&driver_id="

for d in ${driver_array[@]:9};
do
    echo `curl $update_url"${d}"`
done



#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import print_function
import os
import sys
import logging

if __name__ == '__main__':
    os.chdir(os.path.dirname(sys.argv[0]))  # 将当前文件所在文件夹设为 wkdir
    project_dir = os.path.split(os.getcwd())
    project_dir = os.path.split(project_dir[0])
    sys.path.append(project_dir[0])
    os.chdir(project_dir[0])    # 将项目所在文件夹设置为 wkdir (目录下必须有 __init__.py 文件)
    # logging.info(os.getcwd() + '       home 1')



from api_automation.config.config import Config

logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s %(levelname)s[%(filename)s|ln:%(lineno)d]%(message)s',
                    datefmt='%y-%m-%d %H:%M:%S')

class Data:

    if not Config.debug:
        result_table_name = 'Result'
    else:
        result_table_name = Config.result_table_name
    sql_create_result_table = '''CREATE TABLE %s (
                                case_id char(50),
                                case_name varchar(100),
                                case_result char(50),
                                taken_time char(50),
                                response_time_result char(10),
                                url varchar(1000),
                                body text,
                                actual_result longtext,
                                response_json longtext,
                                expected_result longtext,
                                check_result text,
                                log longtext,
                                total_time char(50),
                                tests_counts char(50),
                                failures char(10),
                                execute_time char(100),
                                level char(100)
                                );''' % result_table_name

    host = 'sandbox.carmaster.yongche.org'
    cookie_iphone = " "
    cookie_android = "B=nve1sl82jcfjxnz&1&jd.13d06b7c9"
    useragent_iphone = " "
    useragent_android = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:48.0) Gecko/20100101 Firefox/48.0"
    # Authorization_iphone = "OAuth oauth_consumer_key=2afdd89f5c6dbdc34542ab04933a091004eba18e2,oauth_signature_method=PLAINTEXT,oauth_timestamp=1473823256,oauth_nonce=1799692674,x_auth_mode=client_auth,oauth_version=1.0,oauth_token=679384c628cf01db44920f82df7c3c84057d64160,oauth_signature=5sARLGoVkNAPhh5wq1Hl95crWIk"
    # Authorization_android = "OAuth oauth_consumer_key=2afdd89f5c6dbdc34542ab04933a091004eba18e2,oauth_signature_method=PLAINTEXT,oauth_timestamp=1473823256,oauth_nonce=1799692674,x_auth_mode=client_auth,oauth_version=1.0,oauth_token=679384c628cf01db44920f82df7c3c84057d64160,oauth_signature=5sARLGoVkNAPhh5wq1Hl95crWIk"
    headers_iphone = {
                        "Host": host,
                        "Content-type": "application/x-www-form-urlencoded",
                        "Accept": "gzip",
                        "User-Agent": useragent_iphone,
                        "cookie": cookie_iphone,
                        # "Authorization": "Authorization_iphone"
                       }
    headers_android = {
                        "Host": host,
                        "Content-type": "application/x-www-form-urlencoded",
                        "Accept": "gzip",
                        "User-Agent": useragent_android,
                        "cookie": cookie_android,
                        # "Authorization": Authorization_android
                       }

    platform_get_iphone = ''
    platform_get_android = ''

    platform_post_iphone = {
                           " "
                            }
    platform_post_android = {
                           " "
                            }

    platform_iphone = {
                'platform_get': platform_get_iphone,
                'platform_post': platform_post_iphone,
                'headers': headers_iphone
                }
    platform_android = {
                'platform_get': platform_get_android,
                'platform_post': platform_post_android,
                'headers': headers_android
                }
    # print (platform_android)
    # sql_case = "select table_id, case_id, case_module, case_name, proxy, api_v, channel, path3, parameters, body,\
    #             check_result, expected_response_time, combination_case from api_test_case order by case_id and table_id asc"
    sql_case_threads_iphone = "select table_id, case_id, case_module, case_name, proxy, path1, path2, path3, parameters, body,\
                check_result, expected_response_time, combination_case from api_test_case where (combination_case is null or combination_case = '') and platform like '%iphone%' order by case_id and table_id asc"
    sql_case_threads_android = "select table_id, case_id, case_module, case_name, proxy, path1, path2, path3, parameters, body,\
                check_result, expected_response_time, combination_case from api_test_case where (combination_case is null or combination_case = '') and platform like '%android%' order by case_id and table_id asc"
    sql_case_combination_iphone = "select table_id, case_id, case_module, case_name, proxy, path1, path2, path3, parameters, body,\
                check_result, expected_response_time, combination_case from api_test_case where combination_case = 'yes' and platform like '%iphone%' order by case_id and table_id asc"
    sql_case_combination_android = "select table_id, case_id, case_module, case_name, proxy, path1, path2, path3, parameters, body,\
                check_result, expected_response_time, combination_case from api_test_case where combination_case = 'yes' and platform like '%android%' order by case_id and table_id asc"

if __name__ == '__main__':
    logging.info(os.getcwd() + '       home 1')
    logging.info(dir(Data))
    Data.setup('android')
    logging.info(Data.cookie)
    pass


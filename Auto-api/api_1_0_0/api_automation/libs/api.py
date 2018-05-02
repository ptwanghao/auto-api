#! /usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import time
import json
import urllib
import MySQLdb
import logging
import traceback
import httplib

if __name__ == '__main__':
    os.chdir(os.path.dirname(sys.argv[0]))  # 将当前文件所在文件夹设为 wkdir
    project_dir = os.path.split(os.getcwd())
    project_dir = os.path.split(project_dir[0])
    sys.path.append(project_dir[0])
    os.chdir(project_dir[0])  # 将项目所在文件夹设置为 wkdir (目录下必须有 __init__.py 文件)

from api_automation.libs import sql
from api_automation.config.config import DataBase
from api_automation.data.data import Data

reload(sys)
sys.setdefaultencoding('utf-8')


class API:
    thread_counts = []
    test_case_counts = []
    str_time = time.strftime("%Y-%m-%d %H:%M:%S")

    def __init__(self, row=None, ip=None, platform=None):
        if row is not None:
            # row: 从mysql获取的一条用例的数据
            self.table_id = row[0]  # 模块 id
            self.case_id = row[1]
            self.case_module = row[2] + '-' # 功能模块名字
            self.case_name = row[3] # 用例名字
            self.proxy = row[4] + '://' # 协议 http
            self.path1 = '/' + row[5] + '/' # 接口版本号
            self.path2 = row[6] + '/' # 接口模块名字
            self.address = row[7] + '?' # 接口地址
            self.parameters = row[8]    # get 请求参数
            self.body = row[9]  # post 请求参数
            self.check_result = row[10] # 断言检查点
            self.expected_response_time = row[11]   # 期望响应时间
            self.ip = ip

            self.host = Data.host   # 接口地址的域名
            self.headers = platform['headers']
            self.platform = platform['platform_get']   # get 请求使用的平台参数
            self.platform_dict = platform['platform_post'] # post 请求使用的平台参数
            self.body_dict = None
            self.body_format = None
            self.url = None

        self.response_content = None
        self.json_dict = None
        self.json_format = None
        self.log = ''
        self.status_code = ''
        self.response_time_result = None
        self.taken_time = None
        self.response_json = None
        self.actual_result = ''
        self.results = []
        self.final_result = False
        self.case_result = 'fail'
        self.result_table_name = 'Result'
        self.test_counts = None
        self.thread_counts = {}

    def prepare_case_data(self, body):
        """return url, body, headers"""
        # logging.info(self.table_id + ' ' + self.case_id + ' ' + self.case_module + self.case_name)
        if (body is None) | (body == "") | (body == " ") | (body == "  "):
            pass
        else:
            self.parameters = ''
            self.platform = ''
            self.body_dict = json.loads(body)

            self.body_dict.update(self.platform_dict)
            self.body_format = json.dumps(self.body_dict, ensure_ascii=False, indent=2)
        self.url = self.proxy + self.host + self.path1 + \
            self.path2 + self.address + self.parameters + self.platform
        # logging.info(self.url)

    def execute(self, row, db):
        try:
            self.prepare_case_data(self.body)

            start_one = time.time()  # 记录当前时间A
            start_request_time = time.strftime("%Y-%m-%d %H:%M:%S")
            response_content, self.json_dict, self.json_format, self.log, self.status_code = get_api_response(
                                                                                                        self.url,
                                                                                                        self.body_dict,
                                                                                                        self.headers,
                                                                                                        self.ip)
            end_one = time.time()  # 记录当前时间B
            self.taken_time = str(round((end_one - start_one), 3))  # B减去A 就是请求接口所消耗的时间

            if response_content is None:
                self.response_json = '接口请求失败: %s\r%s' % (start_request_time, self.log)
                logging.info(self.table_id + ' ' + self.case_id + ' ' + self.case_module + self.case_name)
            elif self.json_dict is None:
                self.response_json = "接口返回值错误: %s \r '%s'" % (start_request_time, response_content)
                logging.info(self.table_id + ' ' + self.case_id + ' ' + self.case_module + self.case_name)
            else:
                # self.response_json = MySQLdb.escape_string(self.json_format)
                self.response_json = self.json_format
                self.response_time_result = round((end_one - start_one), 3) < float(
                    self.expected_response_time)
                check_result_list = self.check_result.split('\n')
                self.get_result(check_result_list, self.json_dict)
                if False in self.results:
                    logging.info(self.table_id + ' ' + self.case_id + ' ' + self.case_module + self.case_name)
                    logging.info(self.url)
                    logging.info(self.body_format)
                    logging.info(self.actual_result)
                    logging.info(self.json_format)
                    logging.info(self.results)
                    pass  # self.final_result 初始值为 False
                else:
                    self.final_result = True
                    # logging.info(self.table_id + ' ' + self.case_id + ' ' + self.case_module + self.case_name)
            if self.final_result is True:
                self.case_result = 'pass'
                self.response_json = ''
        except Exception:
            traceback.print_exc()
        finally:
            self.save_one_result(db)

    def save_one_result(self, db):
        try:
            self.check_result = MySQLdb.escape_string(self.check_result)  #escape_string转义函数
            self.actual_result = MySQLdb.escape_string(self.actual_result)
            self.log = MySQLdb.escape_string('status code: ' + self.status_code + '\r' + str(self.log))
            self.response_json = MySQLdb.escape_string(self.response_json)
            values = "( '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')" \
                     % (self.table_id + self.case_id, self.case_module + self.case_name, self.case_result,
                        self.taken_time, self.response_time_result, self.url, self.body_format, self.log,
                        self.response_json, self.check_result, self.actual_result, self.str_time,
                         self.status_code)
            sql_insert = "insert into %s (case_id, case_name, case_result, taken_time, response_time_result,\
                        url, body, log, response_json, check_result, actual_result, execute_time, status_code) values %s"\
                        % (self.result_table_name, values)
            sql.execute_one_sql(db, sql_insert)
        except Exception:
            traceback.print_exc()

    def save_summary_result(self, db, total_time):
        sql1 = "select count(*) from %s where execute_time = '%s' and case_id != '000000001'" % (
            self.result_table_name, self.str_time)  # 获取 case 数量
        cursor = sql.execute_one_sql(db, sql1)
        rows = cursor.fetchall()
        for row in rows:
            tests_counts = str(row[0])
            logging.info("执行用例数: %s" % tests_counts)

        sql2 = "select count(*) from %s where case_result = 'fail' and execute_time = '%s'" % (
            self.result_table_name, self.str_time)
        cursor = sql.execute_one_sql(db, sql2)
        rows = cursor.fetchall()
        for row in rows:
            failures = str(row[0])
            logging.info("失败用例数: %s" % failures)
        # if failures != '0':
        #     os.system('say 失败')
        case_name = '测试概要总结: '
        values = "('%s', '%s', '%s', '%s', '%s', '%s')" \
                 % ('000000001', case_name, total_time, tests_counts, failures, self.str_time)
        sql3 = "insert into %s (case_id, case_name, taken_time, tests_counts, failures, execute_time)\
                 values %s" % (self.result_table_name, values)
        sql.execute_one_sql(db, sql3)
        logging.info('total_time: %s' % total_time)

    def get_result(self, check_result_list, json_dict):
        for check_item in check_result_list:
            try:
                value = ""
                check_key = check_item.split(" == ")
                exec ("value = json_dict%s" % check_key[0])

                # 如果判断了多个字段，将结果进行连接
                if self.actual_result == '':
                    self.actual_result = "%s == '%s'" % (check_key[0], value)
                else:
                    self.actual_result = "%s\n%s == '%s'" % (self.actual_result, check_key[0], value)

                check_command = "self.results.append(json_dict%s)" % check_item
                exec (check_command)
            except Exception:
                traceback.print_exc()
                self.results.append(False)


def get_api_response(_url=None, _data=None, _headers=None, _ip=None):
    response_dict = None
    response_format = None
    response_content = None
    status_code = ''
    error = ''
    try:
        if _data is not None:
            method = 'POST'
            data_encode = urllib.urlencode(_data)
        else:
            data_encode = None
            method = 'GET'
        conn = httplib.HTTPConnection(_ip, timeout=10)
        conn.request(method, url=_url, body=data_encode, headers=_headers)
        response = conn.getresponse()
        response_content = response.read()
        status_code = str(response.status)
        try:
            response_dict = json.loads(response_content)  # 如果接口返回的 response_content 数据含有报错信息，此方法会失败
            response_format = json.dumps(response_dict, ensure_ascii=False, indent=2)  # unicode 类型
        except Exception, error:
            logging.info(response_content)
            logging.info(error)
            traceback.print_exc()  # 接口返回信息报错
    except Exception, error:
        traceback.print_exc()
    finally:
        conn.close()
        return response_content, response_dict, response_format, error, status_code

# def get_db_connection():
#     database = DataBase()
#     # database.connect_db_test_data_local() # connect to local test_data db
#     # database.connect_db_test_result_local() # connect to local test_result db
#     database.connect_db_test_data() # connect to server test_data db
#     database.connect_db_test_result() # connect to server test_result db

#     return database

# class ApiThread(threading.Thread):
#     def __init__(self, row):
#         threading.Thread.__init__(self)
#         self.row = row

#     def run(self):
#         API.thread_counts.append(True)
#         execute_one(self.row)

# def execute_one(row, ):
#     try:
#         api = API(row)
#         db = get_db_connection()
#         api.execute(row, db.db_test_result)
#     except Exception:
#         traceback.print_exc()
#     finally:
#         db.db_test_result.close()
#         API.thread_counts.remove(True)
#         API.test_case_counts.append(True)

if __name__ == '__main__':
    # main()
    for i in xrange(1):
        logging.info('------------------------------------')
        logging.info('第 %s 次' % i)
        API.str_time = time.strftime("%Y-%m-%d %H:%M:%S")

        main()

        logging.info('------------------------------------')
        API.test_case_counts = []

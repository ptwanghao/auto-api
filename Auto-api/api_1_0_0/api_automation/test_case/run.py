#! /usr/bin/env python
# -*- coding: utf-8 -*-
import os
import sys
import time
import logging
import threading
import traceback
if __name__ == '__main__':
    os.chdir(os.path.dirname(sys.argv[0]))  # 将当前文件所在文件夹设为 wkdir
    project_dir = os.path.split(os.getcwd())
    project_dir = os.path.split(project_dir[0])
    sys.path.append(project_dir[0])
    os.chdir(project_dir[0])  # 将项目所在文件夹设置为 wkdir (目录下必须有 __init__.py 文件)
from api_automation.config.config import Config, DataBase
from api_automation.libs.report import Report
from api_automation.libs import sql
from api_automation.data.data import Data
from api_automation.libs.api import API

reload(sys)
sys.setdefaultencoding('utf-8')


def run():
    pass
    args = sys.argv  #获取命令行参数
    logging.info(args)
    to_mail_list = None
    platform = 'android'
    # platform = 'android'
    if len(args) == 1:
        execute_all(platform, to_mail_list)
    elif len(args) == 2:
        if (args[1] == 'iphone') | (args[1] == 'android'):
            platform = args[1]
            execute_all(platform, to_mail_list)
        elif 'yongche.com' in args[1]:
            to_mail_list = args[1]
            execute_all(platform, to_mail_list)
        else:
            logging.info('Please input correct parameters')
    elif len(args) == 3:
        if (args[1] == 'iphone') | (args[1] == 'android'):
            platform = args[1]
            to_mail_list = args[2]
            execute_all(platform, to_mail_list)
        else:
            logging.info('Please input correct parameters')

def get_db_connection():
    database = DataBase()
    database.connect_db_test_data_local() # connect to local test_data db
    database.connect_db_test_result_local() # connect to local test_result db
    # database.connect_db_test_data() # connect to server test_data db
    # database.connect_db_test_result() # connect to server test_result db

    return database

class ApiThread(threading.Thread):
    def __init__(self, row, ip, platform):
        threading.Thread.__init__(self)
        self.row = row
        self.ip = ip
        self.platform = platform

    def run(self):
        API.thread_counts.append(True)
        execute_one(self.row, self.ip, self.platform)

def execute_one(row, ip, platform):
    try:
        db = None
        api = API(row, ip, platform)
        db = get_db_connection()
        api.execute(row, db.db_test_result)
    except Exception:
        traceback.print_exc()
    finally:
        if db is not None:
            db.db_test_result.close()
        API.thread_counts.remove(True)
        API.test_case_counts.append(True)

def execute_all(system, to_mail_list=None):
    database = get_db_connection()

    if system == 'iphone':
        platform = Data.platform_iphone
        sql_case_threads = Data.sql_case_threads_iphone
        sql_case_combination = Data.sql_case_combination_iphone
    elif system == 'android':
        platform = Data.platform_android
        sql_case_threads = Data.sql_case_threads_android
        sql_case_combination = Data.sql_case_combination_android
    # sql_case = "select table_id, case_id, case_module, case_name, proxy, path1, path2, address,\
    #                 parameters, body, check_result, expected_response_time, combination_case \
    #                 from api_test_case where case_id = '000100' and table_id = '00'"
    cursor = sql.execute_one_sql(database.db_test_data, sql_case_threads)
    cursor_combination = sql.execute_one_sql(database.db_test_data, sql_case_combination)
    database.db_test_data.close()
    rows = cursor.fetchall()
    rows_combination = cursor_combination.fetchall()
    test_case_counts = len(rows)
    time1 = time.time()
    api_threads = []
    for row in rows:
        t = ApiThread(row, Config.ip, platform)
        api_threads.append(t)

    for api_thread in api_threads:
        while len(API.thread_counts) > 20:
            logging.info("thread_counts: %s" % len(API.thread_counts))
            time.sleep(1)
        api_thread.start()
        time.sleep(0.01)

    logging.info("start execute combination case")
    for row in rows_combination:
        api_combination = API(row, Config.ip, platform)
        api_combination.execute(row, database.db_test_result)

    while len(API.test_case_counts) < test_case_counts:
        time2 = time.time()
        taken_time = time2 - time1
        if taken_time > 300:
            break
        time.sleep(1)
        logging.info('thread counts: %s' % len(API.thread_counts))

    logging.info('test_case_counts: %s' % len(API.test_case_counts))
    time2 = time.time()
    taken_time = time2 - time1
    taken_time = str(round(taken_time, 1))

    api = API()
    api.save_summary_result(database.db_test_result, taken_time)
    to_list = []
    if to_mail_list is None:
        to_list = Config.to_list
    else:
        to_list.append(to_mail_list)
    Report.anlyze_result(database.db_test_result, to_list, API.str_time)
    database.db_test_result.close()


if __name__ == '__main__':
    # run()
    for i in xrange(1):
        logging.info('------------------------------------')
        logging.info('第 %s 次' % (i+1))
        API.str_time = time.strftime("%Y-%m-%d %H:%M:%S")

        run()

        logging.info('------------------------------------')
        API.test_case_counts = []

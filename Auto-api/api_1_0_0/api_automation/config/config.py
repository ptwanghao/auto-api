# /usr/bin/env python
# -*- encoding: utf-8 -*-
from __future__ import print_function
import os, sys
import time
import logging
import MySQLdb

if __name__ == '__main__':
    os.chdir(os.path.dirname(sys.argv[0]))      # 添加此文件所在路径为 working directory
    project_dir = os.path.split(os.getcwd())
    project_dir = os.path.split(project_dir[0])
    project_name = project_dir[1]
    wkdir = project_dir[0]
    sys.path.append(wkdir)
    os.chdir(wkdir)    # 将项目所在文件夹设置为 wkdir (目录下必须有 __init__.py 文件)
else:
    project_name = __name__.split('.')[0]   # 获取项目名称
    wkdir = os.getcwd()

'''
配置 logging 输出日志格式
logging.basicConfig函数各参数:

filename: 指定日志文件名
filemode: 和file函数意义相同，指定日志文件的打开模式，'w'或'a'
format: 指定输出的格式和内容，format可以输出很多有用信息，如上例所示:
 %(levelno)s: 打印日志级别的数值
 %(levelname)s: 打印日志级别名称
 %(pathname)s: 打印当前执行程序的路径，其实就是sys.argv[0]
 %(filename)s: 打印当前执行程序名
 %(funcName)s: 打印日志的当前函数
 %(lineno)d: 打印日志的当前行号
 %(asctime)s: 打印日志的时间
 %(thread)d: 打印线程ID
 %(threadName)s: 打印线程名称
 %(process)d: 打印进程ID
 %(message)s: 打印日志信息
datefmt: 指定时间格式，同time.strftime()
level: 设置日志级别，默认为logging.WARNING
stream: 指定将日志的输出流，可以指定输出到sys.stderr,sys.stdout或者文件，默认输出到sys.stderr，当stream和filename同时指定时，stream被忽略
'''

debug = True
debug = False
if debug is True:

    logging.basicConfig(level=logging.DEBUG,     #logging.basicConfig函数对日志的输出格式及方式做相关配置

                        format='%(asctime)s %(levelname)s[%(filename)s|ln:%(lineno)d]%(message)s',
                        datefmt='%y-%m-%d %H:%M:%S')
else:

    logging.basicConfig(level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s[%(filename)s|ln:%(lineno)d]%(message)s',
                        datefmt='%y-%m-%d %H:%M:%S',
                        filename='%s%s%s%slog%s%s.log' % (wkdir, os.sep, project_name, os.sep, os.sep, time.strftime("%Y%m%d %H-%M-%S")),
                        filemode='w')
    console = logging.StreamHandler()
    console.setLevel(logging.INFO)
    formatter = logging.Formatter('%(asctime)s %(levelname)s[%(filename)-5s] [L:%(lineno)d]%(message)s')
    console.setFormatter(formatter)
    logging.getLogger('').addHandler(console)

class Config:
    debug = True
    table_list = []
    taken_time = "1.5"
    mail_pre_title = "Automation-API-"
    result_table_name = "Result"
    str_time = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time())) #time.strftime 获取当前时间


    # ip = '106.75.11.106'        # 测试 IP
    # ip = '111.202.98.65'        # 测试 IP：120.132.64.170
    # ip = '10.0.11.65'            # 添加车辆
    ip = '10.0.11.117'             # getHomePage

    to_list = ['wanghao@yongche.com']  # 指定测试报告邮件接收列表



class DataBase:

    def __init__(self):
        self.db_test_data = None
        self.db_test_result = None

    def connect_db_test_data(self):
        self.db_test_data = MySQLdb.Connect(
                                               host="xxxx1",
                                               port=3306,
                                               user="xxx",
                                               passwd="xxxxxxx*7o-Ra",
                                               db="xxxxxxx",
                                               charset="utf8"
                                               )

    def connect_db_test_result(self):
        self.db_test_result = MySQLdb.connect(
                                               host="XXX",
                                               port=3306,
                                               user="XXX",
                                               passwd="XXX",
                                               db="test_result",
                                               charset="utf8"
                                               )
        # logging.info("服务器数据库连接成功")

    def connect_db_test_data_local(self):  #格式MySQLdb.Connect()
        self.db_test_data = MySQLdb.Connect(
                                               host="localhost",   # 可以修改成我的机器的IP地址10.1.1.195
                                               port=3306,
                                               user="root",
                                               passwd="123123",
                                               db="test_data",
                                               charset="utf8"
                                               )

    def connect_db_test_result_local(self):
        self.db_test_result = MySQLdb.connect(
                                               host="localhost",
                                               port=3306,
                                               user="root",
                                               passwd="123123",
                                               db="test_result",
                                               charset="utf8"
                                               )
        # logging.info("本地数据库连接成功")



if __name__ == '__main__':
    # logging.info("本地数据库连接成功")
    # print("本地数据库连接成功")
    # logging.info(wkdir)
    pass

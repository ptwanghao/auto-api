# #! /usr/bin/env python
# # -*- coding: utf-8 -*-
# import os
# import sys
# import time
# import logging
# import threading
# import traceback
# if __name__ == '__main__':
#     os.chdir(os.path.dirname(sys.argv[0]))  # 将当前文件所在文件夹设为 wkdir
#     project_dir = os.path.split(os.getcwd())
#     project_dir = os.path.split(project_dir[0])
#     sys.path.append(project_dir[0])
#     os.chdir(project_dir[0])  # 将项目所在文件夹设置为 wkdir (目录下必须有 __init__.py 文件)
# from api_automation.config.config import Config, DataBase
# from api_automation.libs.report import Report
# from api_automation.libs import sql
# from api_automation.data.data import Data
# from api_automation.libs.api import API, get_api_response
#
# reload(sys)66
# sys.setdefaultencoding('utf-8')
#
#
# def test():
#     url = ' '
#     data = {
#         "f": "iphone",
#         "s": "Rm5mh0Uf8Ud/JaIyFIXqKp4xUPqgXMOl6J3zd/4Ho0clEV6hAnRNyuvPP2VDauonkYKkHsyUV0=",
#         "d": "Vgzq9z1YhmbQYl6Tn7SpaXJbhkhebMH8G1IdfSP2Ciro9M0L09BmrdZTwZPNWCPGEPLTbt10EIf/CjwUNhHRQg==",
#         "t": "e69bf9aeba79b45932c120cae241e175"
#         }
#     headers = {
#         "Host": "h5.smzdm.com",
#         "Content-type": "application/x-www-form-urlencoded",
#         "Accept": "text/plain",
#         "User-Agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 8_4_1 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Mobile/12H321#SMZDM#ä»ä¹å¼å¾ä¹° 6.3 rv:25.3 (iPhone; iPhone OS 8.4.1; zh_CN)/iphone_smzdmapp/6.3",
#         "cookie": ""
#     }
#     ip = "111.202.98.63"
#     a = get_api_response(url, data, headers, ip)
#     logging.info(a[2])
#
# # test()
#
#
#
# if __name__ == '__main__':
#     # run()
#     for i in xrange(1):
#         logging.info('------------------------------------')
#         logging.info('第 %s 次' % (i+1))
#         API.str_time = time.strftime("%Y-%m-%d %H:%M:%S")
#
#         test()
#
#         logging.info('------------------------------------')
#         API.test_case_counts = []

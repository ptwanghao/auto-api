#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import print_function
import os
import sys
import json
import logging

if __name__ == '__main__':
    # 将项目所在文件夹设置为 wkdir (目录下必须有 __init__.py 文件)
    os.chdir(os.path.dirname(sys.argv[0]))      # 添加此文件所在路径为 working directory
    project_dir = os.path.split(os.getcwd())
    project_dir = os.path.split(project_dir[0])
    wkdir = project_dir[0]
    sys.path.append(wkdir)
    os.chdir(wkdir)    # 将项目所在文件夹设置为 wkdir (目录下必须有 __init__.py 文件)

from api_automation.libs.mail import SendMail
# from api_automation.config.config import *
from api_automation.data import data

# config = Config()

class Report():

    mail_content = ""
    failures_counts = None
    failures_rate = None

    @classmethod
    def anlyze_result(self, connection_test_result, to_list, execute_time):
        cursor_test_result = connection_test_result.cursor()
        # 总结
        reload(data)
        logging.info(data.Data.result_table_name)
        sql_summary = "select tests_counts, failures from test_result.%s where case_id='000000001' and execute_time = '%s'" % (data.Data.result_table_name, execute_time)
        cursor_test_result.execute(sql_summary)
        # logging.info(cursor_test_result)

        for i in cursor_test_result:
            # logging.info(i)
            tests_counts = i[0]
            self.failures = i[1]
            # logging.info("tests_counts: %s" %tests_counts)
            # logging.info("failures:" + self.failures)
            failures_rate = str(round(float(self.failures)/float(tests_counts), 3)*100) + "%"
            # logging.info("failures_rate: %s" %failures_rate)

        sql_exceed_time_counts = "select case_id from test_result.%s where response_time_result = 'False' and case_id != '000000001' and execute_time = '%s'" % (data.Data.result_table_name, execute_time)
        cursor_test_result.execute(sql_exceed_time_counts)
        exceed_time_counts = cursor_test_result.rowcount    # long 类型
        # logging.info("exceed_time_counts: %s" %exceed_time_counts)

        # Summary
        mail_content = '''
            <!DOCTYPE html>
            <html>
            <meta charset="utf-8"/>
            <body>

            <table width=100%% border="0" cellspacing="2" cellpadding="3" >
            <h1>API_Automation</h1>
            <tr>
            <td style="background-color:#B3EE3A; color:#080808; text-align:center;"><b>接口执行总数</b><br></td>
            <td style="background-color:#B3EE3A; color:#080808; text-align:center;"><b>失败总数</b><br></td>
            <td style="background-color:#B3EE3A; color:#080808; text-align:center;"><b>失败率</b><br></td>
            <td style="background-color:#B3EE3A; color:#080808; text-align:center;"><b>执行超时的接口统计</b><br></td>
            </tr>
            <tr>
            <td style="background-color:#F5F5DC;text-align:center">%s</td>
            <td style="background-color:#F5F5DC;text-align:center">%s</td>
            <td style="background-color:#F5F5DC;text-align:center">%s</td>
            <td style="background-color:#F5F5DC;text-align:center">%s</td>
            </tr>
            </table>
            ''' % (tests_counts, self.failures, failures_rate, exceed_time_counts)
        # logging.info(mail_content)

        # 失败的用例
        # logging.info("failures: " + self.failures)
        if self.failures != '0':
            mail_content = mail_content + "<br><h3>设置请求接口超时时间为10s):</h3>"
            sql_failure = "select url, body, log, expected_result, response_json, case_id, case_name, check_result, actual_result from test_result.%s where case_result='fail' and execute_time = '%s'" % (data.Data.result_table_name, execute_time)
            cursor_test_result.execute(sql_failure)

            failure_number = 1
            for i in cursor_test_result:
                url = i[0]
                body = str(i[1])
                log = i[2]
                response_json = i[4]
                case_id = i[5]
                case_name = i[6]
                check_result = i[7]
                actual_result = i[8]

                if body != 'None':
                    # logging.info(body)
                    body = json.loads(body)
                    body = json.dumps(body, ensure_ascii=None, indent=2)
                # expected_result = json.loads(expected_result)
                # expected_result = json.dumps(expected_result, ensure_ascii=None, indent=2)

                mail_content = mail_content + '''
                    <table width=100%%  border="0" >
                        <td style="background-color:#F5F5DC; color:red">
                            %s、<ins>用例ID/名称</ins>: %s: %s<br>
                            <div style="width:100%%; word-wrap:break-word; ">接口地址：<a href="%s" target="_blank">%s</a></div>
                            <pre rows="10" style="width:100%%; "><ins>请求参数data</ins>：<br>%s<br></pre>
                            <pre rows="2" style="width:100%%; "><ins>预期结果</ins>：<br>%s</pre>
                            <pre rows="2" style="width:100%%; "><ins>实际结果</ins>：<br>%s</pre>
                            <ins>接口返回值</ins>：<br>
                            <textarea rows="20" style="width:98.5%%; overflow-y:auto; overflow-x:hidden">%s</textarea><br>
                            Log: <textarea rows="2" style="width:98.5%%; overflow-y:auto; overflow-x:hidden">%s</textarea><br>
                        </td>
                    </table>
                    ''' % (failure_number, case_id, case_name, url, url, body, check_result, actual_result, response_json, log)

                failure_number += 1

        # 响应时间不达标的接口
        sql_exceed_time_counts = "select url, body, case_id, case_name, taken_time from test_result.%s where response_time_result = 'False' and case_id != '000000001' and execute_time = '%s'" % (data.Data.result_table_name, execute_time)
        cursor_test_result.execute(sql_exceed_time_counts)
        # exceed_time_counts = cursor_test_result.rowcount    # long 类型
        if exceed_time_counts != 0:
            mail_content = mail_content + "<h3>响应时间不达标的接口共有：%s个:</h3>" % exceed_time_counts
            # logging.info(cursor_test_result.rowcount)

            failure_number = 1
            for i in cursor_test_result:
                # logging.info(i)
                url = i[0]
                body = i[1]
                case_id = i[2]
                case_name = i[3]
                taken_time = i[4]

                if body != 'None':
                    # logging.info(body)
                    body = json.loads(body)
                    body = json.dumps(body, ensure_ascii=None, indent=2)

                mail_content = mail_content + '''
                    <table width=100%%  border="0" >
                    <td style="background-color:#F5F5DC;"><br>
                    %s、<ins>用例ID/名称</ins>: %s: %s<br><br>
                    <div style="width:100%%; word-wrap:break-word;">接口地址：<a href="%s" target="_blank">%s</a></div>消耗时间：%s
                    <pre rows="15" style="width:100%%; "><ins>请求参数data</ins>：<br>%s<br></pre>
                    </td>
                    </table>
                ''' % (failure_number, case_id, case_name, url, url, taken_time, body)

                failure_number += 1
        mail_content = mail_content + '''
            </body>
            </html>
        '''

        if self.failures != "0":
            mail = SendMail()
            mail.send_mail(to_list, mail_content)
        elif exceed_time_counts != 0:
            # mail = SendMail()
            # mail.send_mail(to_list, mail_content)
            # mail_content = mail_content + "<br>测试通过"
            # mail.send_mail(["wanghao@yongche.com"], mail_content)
            pass
        # return mail_content

if __name__ == '__main__':
    pass
    # DataBase.connect_database()
    # # logging.info("start")
    # # nose.run()
    # Report.anlyze_result(DataBase.connection_test_result, to_list, '2016-02-02 15:33:01')
    # DataBase.close_database()
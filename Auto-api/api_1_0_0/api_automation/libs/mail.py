#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import print_function
from email.mime.text import MIMEText
import time
import smtplib

from api_automation.config.config import Config

config = Config()


class SendMail():
    '''
    send email
    '''

    def __init__(self):
        self.mail_host = "smtp.yongche.com"  # 设置服务器
        self.mail_user = "wanghao@yongche.com"  # 用户名
        self.mail_pass = "w894080."  # 口令
        self.mail_postfix = "mail.yongche.com"  # 发件箱的后缀
        self.str_date = time.strftime('%Y-%m-%d  %H:%M', time.localtime(time.time()))

    def send_mail(self, to_list, content):
        me = " Automation-API" + "<" + self.mail_user + ">"
        # msg = MIMEText(content,_subtype='plain',_charset='gb2312')
        # msg = MIMEText(content,_subtype='plain',_charset='utf-8')
        msg = MIMEText(content, _subtype='html', _charset='utf-8')
        msg['Subject'] = config.mail_pre_title + self.str_date + ' @' + config.ip
        msg['From'] = me
        msg['To'] = ";".join(to_list)
        try:
            server = smtplib.SMTP()
            server.connect(self.mail_host)
            server.login(self.mail_user, self.mail_pass)
            server.sendmail(me, to_list, msg.as_string())
            server.close()
            print("发送成功")
            return True
        except Exception, e:
            print(str(e))
            print("发送失败")
            return False

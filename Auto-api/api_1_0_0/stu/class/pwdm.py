#! /usr/bin/env python
# -*- coding: utf-8 -*-


'''
写一个登录的程序，要输入账号和密码，进行判断，如果帐号密码正确，输出欢迎信息，欢迎***登录
如果帐号密码错误，提示错误信息，让用户重新输入
如果输入错误次数超过3次，程序自动退出
用户名输入空的话，提示用户名不能为空，用户名输入空也算失败一次
'''
name = 'wang'
pwd = '123'


count = 1
for count in (1,2,3):
    uname = raw_input('输入用户名:')
    upwd = raw_input('输入密码:')

    if uname == name and upwd ==pwd:
        print "欢迎您"+name
        break
    if uname != name:
        print "用户名错误"
        count+=1
        break
    if upwd != pwd:
        print "密码错误"
        count+=1
        break
else:
    print '锁定'

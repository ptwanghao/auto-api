#! /usr/bin/env python
# -*- coding: utf-8 -*-
import urllib
import sys
import re
import os
__mateclass = type
class Person:

    def setName(self,name):
        self.name =name

    def getName(self):
        return self.name

    def great(self):
        print "Hello! I'm %s." % self.name

foo = Person()
foo.setName('oks')
foo.great()


class Filter:
    def init(self):
        self.block = []
    def filter(self,sequence):
        return [x for x in sequence if x not in self.block]
class SPAM(Filter):
    def init(self):
        self.block = ['SPA']

f = Filter()
f.init()
f.filter([1,2,3])

print issubclass(SPAM,Filter)  # 內建函数 查看参数1是否为参数2的子类

class Brid:
    def __init__(self):
        self.hun = True
    def eat(self):
        i = 0
        for i in(0,1,2,3,4):
            i = i+1
            if self.hun:
                print "chi"
                self.hun = False
            else:
                print  "no"

n = Brid()

n.eat()

# # 生成器
# def  flatten(nested):
#     # type: (object) -> object
#     for sublist in flatten(nested):
#         for el in sublist:
#             yield el
# nested = [[1,2],[3,4,5],[6]]
#
# list(flatten(nested))
a = list(sys.path)



#
# try:
#     dx= open('/Users/yongche_wh/Desktop/test.txt','r')
#
#     print dx.readline()
#
#     text = sys.stdin.read(dx)
#
#     # words = text.split()
#     # wordcount = len(words)
#     # print  'Wordcount:' , wordcount
# finally:
#     dx.close()
#read方法
# filet1= open('/Users/yongche_wh/Desktop/test.txt','rw')
#
# print filet1.read()

#####readline() 方法

f2 = open('/Users/yongche_wh/Desktop/test.txt')

for i in range(3):
    print str(i)+ ':'+f2.readline()

print os.chdir(os.path.dirname(sys.argv[0]))
project_dir = os.path.split(os.getcwd())
print project_dir
project_dir = os.path.split(project_dir[0])
print project_dir
print project_dir[1]

#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import print_function
import json
import os, sys, time
import urllib, httplib
import logging, traceback


if __name__ == '__main__':
    os.chdir(os.path.dirname(sys.argv[0]))  # 将当前文件所在文件夹设为 wkdir
    project_dir = os.path.split(os.getcwd())
    project_dir = os.path.split(project_dir[0])
    sys.path.append(project_dir[0])
    os.chdir(project_dir[0])    # 将项目所在文件夹设置为 wkdir (目录下必须有 __init__.py 文件)
    # logging.info(os.getcwd() + '       home 1')

reload(sys)
sys.setdefaultencoding('utf-8')

def execute_multi_sql(connection, sql_list):
    cursor = connection.cursor()
    for sql in sql_list:
        cursor.execute(sql)
        connection.commit()

def execute_one_sql(connection, sql):
    cursor = connection.cursor()
    # logging.info(sql)
    cursor.execute(sql)
    connection.commit()
    return cursor

def get_columns_name(connection, schema_name, table_name):
    '''return list of columns name'''
    columns_name_list = []
    sql = "select column_name from information_schema.columns where table_schema = '%s' and table_name = '%s'" %(schema_name, table_name)
    cursor = execute_one_sql(connection, sql)
    columns_name_tuple = cursor.fetchall()

    for column_name_tupe in columns_name_tuple:
        columns_name_list.append(str(column_name_tupe[0]))
    return columns_name_list

def get_tables_name(connection, schema_name):
    '''return list of tables name'''
    tables_name_list = []
    sql = "select table_name from information_schema.tables where table_schema = '%s' and table_type = 'base table'" %(schema_name)
    cursor = execute_one_sql(connection, sql)
    tables_name_tuple = cursor.fetchall()

    for table_name_tupe in tables_name_tuple:
        tables_name_list.append(str(table_name_tupe[0]))
    return tables_name_list


if __name__ == '__main__':
    # 连接数据库
    Config.debug = False
    # Config.debug = True
    # MysqlServer.connect_database('youhui_db_mysql_m01')
    # MysqlServer.connect_database('db-server_write01_eth01')

    # Config.debug = True
    # MysqlServer.connect_database('youhui_db_mysql_m01_bak')
    # MysqlServer.connect_database('db-server_write01_eth01_bak')

    # MysqlServer.close_database()
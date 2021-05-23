
https://www.cnblogs.com/tmdsleep/p/10967432.html  // MySQL 8.*版本 修改root密码

use mysql; 
update user set authentication_string='' where user='root'
mysql> select host,user,authentication_string from mysql.user;
+-----------+------------------+------------------------------------------------------------------------+
| host      | user             | authentication_string                                                  |
+-----------+------------------+------------------------------------------------------------------------+
| %         | root             |                                                                |
| localhost | mysql.infoschema | $A$005$THISISACOMBINATIONOFINVALIDSALTANDPASSWORDTHATMUSTNEVERBRBEUSED |
| localhost | mysql.session    | $A$005$THISISACOMBINATIONOFINVALIDSALTANDPASSWORDTHATMUSTNEVERBRBEUSED |
| localhost | mysql.sys        | $A$005$THISISACOMBINATIONOFINVALIDSALTANDPASSWORDTHATMUSTNEVERBRBEUSED |
+-----------+------------------+------------------------------------------------------------------------+
4 rows in set (0.00 sec)

mysql> ALTER user 'root' IDENTIFIED BY 'pwd_root';
Query OK, 0 rows affected (0.01 sec)

mysql> select host,user,authentication_string from mysql.user;
+-----------+------------------+------------------------------------------------------------------------+
| host      | user             | authentication_string                                                  |
+-----------+------------------+------------------------------------------------------------------------+
| %         | root             | $A$005$7a@3R@Q}Y"gNlftKOFwT4Hqrng6JuvGskCXqa48Yy5qFw18hKA3HN4Vk1 |
| localhost | mysql.infoschema | $A$005$THISISACOMBINATIONOFINVALIDSALTANDPASSWORDTHATMUSTNEVERBRBEUSED |
| localhost | mysql.session    | $A$005$THISISACOMBINATIONOFINVALIDSALTANDPASSWORDTHATMUSTNEVERBRBEUSED |
| localhost | mysql.sys        | $A$005$THISISACOMBINATIONOFINVALIDSALTANDPASSWORDTHATMUSTNEVERBRBEUSED |
+-----------+------------------+------------------------------------------------------------------------+
4 rows in set (0.00 sec)



链接Navicat  // https://blog.csdn.net/seventopalsy/article/details/80195246
ALTER USER 'root' IDENTIFIED BY 'password' PASSWORD EXPIRE NEVER;
ALTER USER 'root' IDENTIFIED WITH mysql_native_password BY 'pwd_root';





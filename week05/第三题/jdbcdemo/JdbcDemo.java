package io.hust.pony.demoweb.jdbcdemo;

import io.hust.pony.demoweb.meta.UserMetaSpec;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class JdbcDemo {
    private Connection con = null;
    private Statement stmt = null;

    @PostConstruct
    public void initJdbc() {
        loadDriver();
        connectJDBC();
    }
    private void loadDriver() {
        try{//加载MySql的驱动类
            Class.forName("com.mysql.jdbc.Driver") ;
        }catch(ClassNotFoundException e){
            System.out.println("找不到驱动程序类 ，加载驱动失败！");
            e.printStackTrace() ;
        }
    }
    // List<UserMetaSpec>
    private void connectJDBC() {
        System.out.println("开启数据库连接诶！");
        String url = "jdbc:mysql://localhost:3306/macaw_openness?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC" ;
        String username = "root" ;
        String password = "" ;
        try {
            con = DriverManager.getConnection(url , username , password );
            stmt = con.createStatement() ;
        } catch(SQLException se) {
            System.out.println("数据库连接失败！");
            se.printStackTrace() ;
        }
    }

    public List<UserMetaSpec> getUserList() {
        List<UserMetaSpec> result = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT * FROM user");
            while (rs.next()) {
                UserMetaSpec user = new UserMetaSpec();
                user.setAge(rs.getInt("age"));
                user.setName(rs.getString("name"));
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name") + "from JDBC");
                result.add(user);
            }
        } catch (SQLException e) {
            System.out.println("数据库查询数据失败！");
            e.printStackTrace();
        } finally {
            if(rs !=null){   // 关闭记录集
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @PreDestroy
    public void destoryJdbc() {
        System.out.println("关闭连接诶！");
        if(stmt !=null){   // 关闭声明
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(con !=null){  // 关闭连接对象
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

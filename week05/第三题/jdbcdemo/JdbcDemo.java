package io.hust.pony.demoweb.jdbcdemo;

import io.hust.pony.demoweb.meta.UserMetaSpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.ref.PhantomReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class JdbcDemo {
    private Connection con = null;
    private Statement stmt = null;

    @Value("${database.url:NA}")
    private String url;

    @Value("${database.user.name:root}")
    private String name;

    @Value("${database.password:}")
    private String password;

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
        String url = this.url;
        String username = this.name;
        String password = this.password;
        try {
            con = DriverManager.getConnection(url , username , password );
            stmt = con.createStatement() ;
        } catch(SQLException se) {
            System.out.println("数据库连接失败！");
            se.printStackTrace() ;
        }
    }

    public int addUser(UserMetaSpec userMetaSpec) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(formatter);
        //
        String sql = "INSERT INTO user (id,name,age,perseverance,create_time,last_update_time) " + "VALUES ("
                + userMetaSpec.getId()+ ",'" + userMetaSpec.getName() + "'," + userMetaSpec.getAge() + ","+ userMetaSpec.getPerseverance() + ",'"
                + time + "','" + time + "')";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("数据库插入数据失败！");
            e.printStackTrace();
        }
        return 0;
    }

    public int addUserV2(UserMetaSpec userMetaSpec) {
        //sql
        String sql = "INSERT INTO user (id,name,age,perseverance,create_time,last_update_time)"
                + "values (" + "?,?,?,?,NOW(),NOW())";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1,userMetaSpec.getId());
            preparedStatement.setString(2,userMetaSpec.getName());
            preparedStatement.setInt(3,userMetaSpec.getAge());
            preparedStatement.setInt(4,userMetaSpec.getPerseverance());
            //执行
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("数据库插入数据失败 -----V2！");
            e.printStackTrace();
        }
        return 1;
    }

    public int updateUserV2(UserMetaSpec userMetaSpec) {
        //sql, 每行加空格
        String sql = "UPDATE user set name=?, age=?, perseverance=?,last_update_time=NOW()  where id=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1,userMetaSpec.getName());
            preparedStatement.setInt(2,userMetaSpec.getAge());
            preparedStatement.setInt(3,userMetaSpec.getPerseverance());
            preparedStatement.setInt(4,userMetaSpec.getId());
            //执行
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("数据库更新数据失败 -----V2！");
            e.printStackTrace();
        }
        return 1;
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

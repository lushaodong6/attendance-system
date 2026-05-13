package com.example.attendance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
public class DataSourceTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testConnection() throws SQLException {
        // 获取数据库连接
        Connection connection = dataSource.getConnection();
        if (connection != null) {
            System.out.println("✅ 数据库连接成功！");
            System.out.println("连接信息：" + connection);
        } else {
            System.out.println("❌ 数据库连接失败！");
        }
        connection.close();
    }
}
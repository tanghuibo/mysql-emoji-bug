package io.github.tanghuibo;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;
import java.util.Properties;

/**
 * @author tanghuibo
 * @date 2020/6/8下午11:43
 */
public class MysqlEmojiBugApplication {

    public static void main(String[] args) throws Exception {
        Properties properties = getProperties("/application.properties");
        InputStream in = MysqlEmojiBugApplication.class.getResourceAsStream("/emoji.txt");
        List<String> emojiList = IOUtils.readLines(in, StandardCharsets.UTF_8);
        Connection conn = getConnection(properties);
        conn.setAutoCommit(false);
        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO test_table(char_value) values (?)");
        for (String emoji : emojiList) {
            executeSql(preparedStatement, emoji);
        }
        conn.commit();
        preparedStatement.close();
    }

    private static Connection getConnection(Properties properties) throws SQLException, ClassNotFoundException {
        String driver = properties.getProperty("datasource.driver");
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        return DriverManager.getConnection(url, username, password);
    }

    private static Properties getProperties(String path) throws IOException {
        InputStream in = MysqlEmojiBugApplication.class.getResourceAsStream(path);
        Properties properties = new Properties();
        properties.load(in);
        return properties;
    }

    private static void executeSql(PreparedStatement preparedStatement, String emoji) throws SQLException {
        preparedStatement.setString(1, emoji);
        try {
            preparedStatement.executeUpdate();
            System.out.println("sql执行成功,数据:" + emoji);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("sql执行失败,数据:" + emoji);
        }
    }
}

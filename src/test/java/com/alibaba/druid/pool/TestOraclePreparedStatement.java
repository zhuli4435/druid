package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;

import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.OracleUtils;

public class TestOraclePreparedStatement extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;
    private String SQL;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        // jdbcUrl = "jdbc:oracle:thin:@20.20.149.85:1521:ocnauto"; // error url
        user = "alibaba";
        password = "ccbuauto";
        
        jdbcUrl = "jdbc:oracle:thin:@10.20.149.81:1521:ointest3";
        user = "alibaba";
        password = "deYcR7facWSJtCuDpm2r";
        SQL = "SELECT * FROM AV_INFO WHERE ID = ?";

        Class.forName(JdbcUtils.getDriverClassName(jdbcUrl));
    }

    public void createTable() throws Exception {
        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);

        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE T");
        ;
        stmt.execute("CREATE TABLE T (FID INT, FNAME VARCHAR2(4000), FDESC CLOB)");
        ;
        stmt.close();

        conn.close();
    }

    public void test_0() throws Exception {

        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);

        OracleConnection oracleConn = (OracleConnection) conn;

        // ResultSet metaRs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] {"TABLE"});
        // JdbcUtils.printResultSet(metaRs);
        // metaRs.close();

        int fetchRowSize = oracleConn.getDefaultRowPrefetch();

//        {
//            oracleConn.setStatementCacheSize(10);
//            oracleConn.setImplicitCachingEnabled(true);
//
//            PreparedStatement stmt = conn.prepareStatement(SQL);
//            stmt.close();
//            
//            PreparedStatement stmt2 = conn.prepareStatement(SQL);
//            stmt2.close();
//        }

        OraclePreparedStatement oracleStmt = null;
        PreparedStatement stmt = conn.prepareStatement(SQL);
        oracleStmt = (OraclePreparedStatement) stmt;
        oracleStmt.setRowPrefetch(10);
 
        {

            stmt.setInt(1, 327);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }

            rs.close();

            //oracleStmt.clearDefines();
        }
        for (int i = 0; i < 10; ++i){
            OracleUtils.enterImplicitCache(oracleStmt);
            OracleUtils.exitImplicitCacheToActive(oracleStmt);
            stmt.setInt(1, 327);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                
            }
            
            rs.close();
            
        }
        
        oracleStmt.setRowPrefetch(1000);
        {
            stmt.setInt(1, 11);
            ResultSet rs = stmt.executeQuery();
            rs.next();

            rs.close();
            stmt.close();
        }

        conn.close();
    }
}
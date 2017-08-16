package com.weidai.dataMigration.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

public class CompareTable {

    private Connection mainConn = null;
    private Connection otherConn = null;
    private String mainUrl;
    private String mainUsername;
    private String mainPw;
    private String otherUrl;
    private String otherUsername;
    private String otherPw;

    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CompareTable(String mainUrl, String mainUsername, String mainPw, String otherUrl, String otherUsername, String otherPw) {
        this.mainUsername = mainUsername;
        this.mainUrl = mainUrl;
        this.mainPw = mainPw;
        this.otherPw = otherPw;
        this.otherUrl = otherUrl;
        this.otherUsername = otherUsername;

    }

    public void match(String mainTableName, String otherTableName) {
        try {
            mainConn = DriverManager.getConnection("jdbc:mysql://" + mainUrl, mainUsername, mainPw);
            otherConn = DriverManager.getConnection("jdbc:mysql://" + otherUrl, otherUsername, otherPw);
            doMatch(mainTableName, otherTableName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                otherConn.close();
                mainConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public void match(List<String> tableNameList) {
        try {
            mainConn = DriverManager.getConnection("jdbc:mysql://" + mainUrl, mainUsername, mainPw);
            otherConn = DriverManager.getConnection("jdbc:mysql://" + otherUrl, otherUsername, otherPw);
            // for (String tableName : tableNameList) {
            // doMatch(tableName, tableName);
            // }
            doMatch(tableNameList.get(0), tableNameList.get(1));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                otherConn.close();
                mainConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @param mainTableName 主表
     * @param otherTableName 副表
     * @throws Exception
     */
    private void doMatch(String mainTableName, String otherTableName) throws Exception {
        PreparedStatement mainPs = null;
        PreparedStatement otherPs = null;
        ResultSet mainRs = null;
        ResultSet otherRs = null;
        ResultSetMetaData mainRsmd = null;
        ResultSetMetaData otherRsmd = null;
        Map<String, TableStructure> mainMap = new HashMap<String, TableStructure>();
        Map<String, TableStructure> otherMap = new HashMap<String, TableStructure>();
        try {
            System.out.println();
            String mainSql = "select * from " + mainTableName + " limit 1";
            String otherSql = "select * from " + otherTableName + " limit 1";
            System.out.println("***********正在比较表名" + mainTableName + " " + otherTableName + "***********");
            mainConn = DriverManager.getConnection("jdbc:mysql://" + mainUrl, mainUsername, mainPw);
            // 获得主表结构
            mainPs = mainConn.prepareStatement(mainSql);
            mainRs = mainPs.executeQuery();
            mainRsmd = mainRs.getMetaData();
            // 获取副表的表结构
            otherConn = DriverManager.getConnection("jdbc:mysql://" + otherUrl, otherUsername, otherPw);
            otherPs = otherConn.prepareStatement(otherSql);
            otherRs = otherPs.executeQuery();
            otherRsmd = otherRs.getMetaData();
            // 获得主表结构存入Map表中
            for (int i = 1; i <= mainRsmd.getColumnCount(); i++) {
                String colName = mainRsmd.getColumnName(i).toLowerCase(); // 获得对应列的列名
                String colType = mainRsmd.getColumnTypeName(i);
                // 获得对应列的数据类型
                int colLength = mainRsmd.getPrecision(i);
                int isNullable = mainRsmd.isNullable(i);

                // 获得数据长度
                TableStructure tableStructure = new TableStructure();
                tableStructure.setColName(colName.toLowerCase());
                tableStructure.setColType(colType);
                tableStructure.setColLength(colLength);
                tableStructure.setIsNullable(isNullable);
                mainMap.put(colName, tableStructure);
            }

            // 获得副表结构存入JobMap表中
            for (int i = 1; i <= otherRsmd.getColumnCount(); i++) {
                String colName = otherRsmd.getColumnName(i).toLowerCase();
                String colType = otherRsmd.getColumnTypeName(i);
                int colLength = otherRsmd.getPrecision(i);
                int isNullable = otherRsmd.isNullable(i);
                TableStructure tableStructure = new TableStructure();
                tableStructure.setColName(colName.toLowerCase());
                tableStructure.setColType(colType);
                tableStructure.setColLength(colLength);
                tableStructure.setIsNullable(isNullable);
                otherMap.put(colName, tableStructure);
            }
            // 以主表结构为依据比较副表结构
            for (Entry<String, TableStructure> entry : mainMap.entrySet()) {
                String mainTableColName = entry.getKey();
                TableStructure mainTableStructure = entry.getValue();
                TableStructure otherTableStructure = otherMap.get(mainTableColName);
                boolean isDifference = false;
                if (otherTableStructure == null) {
                    System.out.println(mainTableColName + " " + mainTableStructure.getColType() + "(" + mainTableStructure.getColLength() + ")"
                            + convertNull(mainTableStructure.getIsNullable()) + " other:" + "null");
                    continue;
                }
                if (!mainTableStructure.getColType().equals(otherTableStructure.getColType())) {
                    isDifference = true;
                }
                if (mainTableStructure.getColLength() != otherTableStructure.getColLength()) {
                    isDifference = true;
                }
                if (mainTableStructure.getIsNullable() != otherTableStructure.getIsNullable()) {
                    isDifference = true;
                }
                if (isDifference) {
                    System.out.println(mainTableColName + " " + mainTableStructure.getColType() + "(" + mainTableStructure.getColLength() + ")"
                            + convertNull(mainTableStructure.getIsNullable()) + " other: " + otherTableStructure.getColType() + "("
                            + otherTableStructure.getColLength() + ")" + convertNull(otherTableStructure.getIsNullable()));
                }
            }
            // 以副表结构为依据比较主表结构,仅处理主表中没有的
            for (Entry<String, TableStructure> entry : otherMap.entrySet()) {
                String otherTableColName = entry.getKey();
                TableStructure otherStructureTable = entry.getValue();
                TableStructure mainStructureTable = mainMap.get(otherTableColName);
                // 由于之前已经以主表为标本比较过副表,所以在此只需找出在副表中存在而在主表中没有的
                if (mainStructureTable == null) {
                    System.out.println(otherTableColName + " " + "null" + " other: " + otherStructureTable.getColType() + "("
                            + otherStructureTable.getColLength() + ")" + convertNull(otherStructureTable.getIsNullable()));
                }
            }

            System.out.println("***********比较表名" + mainTableName + " " + otherTableName + "结束***********");
            System.out.println();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            try {
                mainRs.close();
                otherRs.close();
                otherPs.close();
                mainPs.close();
            } catch (Exception e2) {
                System.out.println(e2);
            }

        }

    }

    private String convertNull(int isNullable) {
        if (ResultSetMetaData.columnNoNulls == isNullable) {
            return "not null";
        } else if (ResultSetMetaData.columnNullable == isNullable) {
            return "null";
        } else {
            return "unknown";
        }

    }

    private class TableStructure {

        private String colName;
        private String colType;
        private int colLength;
        private int isNullable;

        public int getIsNullable() {
            return isNullable;
        }

        public void setIsNullable(int isNullable) {
            this.isNullable = isNullable;
        }

        public String getColName() {
            return colName;
        }

        public void setColName(String colName) {
            this.colName = colName;
        }

        public String getColType() {
            return colType;
        }

        public void setColType(String colType) {
            this.colType = colType;
        }

        public int getColLength() {
            return colLength;
        }

        public void setColLength(int colLength) {
            this.colLength = colLength;
        }
    }

    // 测试
    public static void main(String[] args) {
        String mainUrl = "192.168.20.40:3306/ucenter1?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
        String mainUsername = "devucenter1";
        String mainPw = "DEVucenter1123!";
        String otherUrl = "172.20.100.11:3306/wdw?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
        String otherUsername = "uatwdw";
        String otherPw = "UATwdw123!";
        CompareTable test = new CompareTable(mainUrl, mainUsername, mainPw, otherUrl, otherUsername, otherPw);
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add("u_sub_account");
        tableNameList.add("u_base");
        test.match(tableNameList);
    }
}

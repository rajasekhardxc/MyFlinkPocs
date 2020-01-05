package com.bigdata.flink.tableapi
import org.apache.flink.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row
import org.apache.flink.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.api.scala._
import org.apache.flink.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.java.io.jdbc.{JDBCAppendTableSink, JDBCInputFormat}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.api.scala._
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.table.api.{TableConfig, TableEnvironment}
import org.apache.flink.table.api.scala.BatchTableEnvironment
import org.apache.flink.types.Row

object csvdata {
  case class aslcc(name:String, age:Int, city:String)
  def main(args: Array[String]) {
    // environment configuration
    val env = ExecutionEnvironment.getExecutionEnvironment
    //    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = BatchTableEnvironment.create(env)
   val data = "C:\\BIGDATA\\Datasets\\asldata.txt"
val ds = env.readCsvFile[aslcc](data,ignoreFirstLine = true) // create dataset
    val tab = tEnv.fromDataSet(ds) // dataset convert to table api
    tEnv.registerTable("tab",tab) /// create or replace temp view
    val res = tEnv.sqlQuery("select * from tab where age>50 ")
    val pr = tEnv.toDataSet[Row](res) // convert tableapi to dataset
    pr.print()
    val jdbcSink=JDBCAppendTableSink.builder()
      .setDrivername("com.mysql.jdbc.Driver")
      .setDBUrl("jdbc:mysql://learnmysql.cuae8tdqbpef.us-east-1.rds.amazonaws.com:3306/mysqldb")
      .setUsername("musername")
      .setPassword("mpassword")
      .setQuery("select ename, sal, job from emp where sal>2000")
     // .setRowTypeInfo(rowType)
      .setQuery("insert into test (id,name) values(?,?)").setParameterTypes(Types.STRING,Types.STRING).build()
    pr.writeToSink(jdbcSink)
    env.execute()
  }
}

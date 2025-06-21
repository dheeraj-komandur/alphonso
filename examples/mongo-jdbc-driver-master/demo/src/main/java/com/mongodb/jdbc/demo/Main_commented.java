/*
********* AI-Assistant Documentation for - Main_commented.java *********
This file implements a simple Java application that connects to a MongoDB database using JDBC, retrieves metadata, and displays data from a specified collection. It serves as a demonstration of how to interact with MongoDB through JDBC.
*/

package com.mongodb.jdbc.demo;

import java.sql.*;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;

// (AI Comment) - Main class responsible for connecting to a MongoDB database using JDBC and displaying results from a specified collection.
public class Main {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mongodb.jdbc.MongoDriver";
   static final String URL = "jdbc:mongodb://mhuser:pencil@localhost:27017/admin";
   private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

   // Data used for test, in the test.test and test2.test collections:
   //
   //{ "values" : [
   //    { "database" : "myDB", "table" : "foo", "tableAlias" : "foo", "column" : "a", "columnAlias" : "a", "value" : 1 },
   //    { "database" : "myDB", "table" : "foo", "tableAlias" : "foo", "column" : "b", "columnAlias" : "b", "value" : "hello" } ]
   //    }
   //{ "values" : [
   //    { "database" : "myDB", "table" : "foo", "tableAlias" : "foo", "column" : "a", "columnAlias" : "a", "value" : 42 },
   //    { "database" : "myDB", "table" : "foo", "tableAlias" : "foo", "column" : "b", "columnAlias" : "b", "value" : "hello 2" } ]
   //    }
   //
   // (AI Comment) - Main method that establishes a connection to the MongoDB database and retrieves metadata about the database.
   public static void main(String[] args) {

      // (AI Comment) - Try-catch block for handling exceptions during database connection and metadata retrieval.
      try{
         java.util.Properties p = new java.util.Properties();
         // These properties will be added to the URI.
         // Uncomment if you wish to specify user and password.
         // p.setProperty("user", "user");
         // p.setProperty("password", "foo");
         p.setProperty("database", "test");
         System.out.println("Connecting to database test...");
         Connection conn = DriverManager.getConnection(URL, p);

        DatabaseMetaData dbmd = conn.getMetaData();
        System.out.println(dbmd.getDriverVersion());
        System.out.println(dbmd.getDriverMajorVersion());
        System.out.println(dbmd.getDriverMinorVersion());
//         System.out.println("Creating statement...");
//         Statement stmt = conn.createStatement();
//         ResultSet rs = stmt.executeQuery("select * from foo");
//         System.out.println("++++++ Showing contents for test.foo ++++++++");
//         displayResultSet(rs);
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
   }

   // (AI Comment) - Displays the contents of a ResultSet, converting data types and handling potential exceptions.
   public static void displayResultSet(ResultSet rs) throws java.sql.SQLException {
	   Calendar c = new GregorianCalendar();
	   c.setTimeZone(UTC);
       // (AI Comment) - Loop through the ResultSet to retrieve and print values from the database.
       while(rs.next()){
          //Retrieve by column name
          double a = rs.getDouble("a");
		  String as = rs.getString("a");
          String b = rs.getString("b");
		  java.sql.Timestamp bd;
		  // (AI Comment) - Try-catch block for handling exceptions when retrieving a timestamp from the ResultSet.
		  try {
		  		bd = rs.getTimestamp("b", c);
				System.out.println("b as a Timestamp is: " + bd);
		  } catch (SQLException e) {
				System.out.println(e);
		  } catch (Exception e) {
				throw new RuntimeException(e);
		  }
          ResultSetMetaData metaData = rs.getMetaData();
		  System.out.println("a is: " + a + " as double"
				  + " b is: " + b + " as string");
		  System.out.println("a as a string is: " + as);
       }
   }
}

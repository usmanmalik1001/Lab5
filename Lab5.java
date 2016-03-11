/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication1;
import java.util.*;
import java.io.*;
import java.sql.*;
/**
 *
 * @author shirazi1
 */
public class JavaApplication1 {

    public static void main(String[] arg) throws Exception {
        
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
        String DB_URL = "jdbc:mysql://localhost/";
        
        Scanner scanner = new Scanner(System.in);

        //  Database credentials
        String USER = "root";
        String PASS = "root";
   
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating database...");
            stmt = conn.createStatement();

            String sql = "DROP SCHEMA IF EXISTS CITIES";
            stmt.executeUpdate(sql);

            sql = "CREATE DATABASE CITIES";
            stmt.executeUpdate(sql);
            System.out.println("Database created successfully...");

            sql = "USE CITIES";
            stmt.executeQuery(sql);

            sql = "CREATE TABLE INFO " +
                    "(locId INTEGER not NULL, " +
                    " country VARCHAR(50), " + 
                    " region VARCHAR(50), " + 
                    " city VARCHAR(50), " + 
                    " postalCode VARCHAR(50), " + 
                    " latitude DOUBLE, " + 
                    " longitude DOUBLE, " + 
                    " metroCode INT DEFAULT NULL, " + 
                    " areaCode INT DEFAULT NULL," + 
                    " PRIMARY KEY ( locId ))"; 

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");

            BufferedReader CSVFile = 
            new BufferedReader(new FileReader("D:\\GeoLiteCity-Location.csv"));

            CSVFile.readLine();
            CSVFile.readLine();
            String dataRow = CSVFile.readLine(); // Read first line.
             // The while checks to see if the data is null. If 
             // it is, we've hit the end of the file. If not, 
             // process the data.

            for (int i = 0; i < 300; i++)
            {
                String statement = "INSERT INTO INFO VALUES (" + dataRow + ")";
                statement = statement.replace(",,",",NULL,");
                statement = statement.replace(",)",",NULL)");
                stmt.executeUpdate(statement);
                dataRow = CSVFile.readLine(); // Read next line of data.
            }
             // Close the file once all data has been read.
            CSVFile.close();
            
            System.out.println("Press 1 to search for city, "
                    + "Press 2 to search nearby cities");
            int option = scanner.nextInt();
            
            if (option == 1)
            {
                System.out.println("Enter the city to search: ");
                String city = scanner.nextLine();

                String query = "SELECT latitude, longitude FROM INFO where city = \"" + city + "\"";
                // execute the query, and get a java resultset
                ResultSet rs = stmt.executeQuery(query);

               // iterate through the java resultset
                while (rs.next())
                {
                    Double latitude = rs.getDouble("latitude");
                    Double longitude = rs.getDouble("longitude");
                    System.out.println("Latitude: " + latitude);
                    System.out.println("Longitude: " + longitude);
                }
            }
            if (option == 2)
            {
                findNearby(conn);
            }
        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try
        System.out.println("Goodbye!");
    } 
    
    public static Double[] Coordinates( String c,Statement st ) throws SQLException{
        String sql="SELECT latitude,longitude from INFO where city=\"" +c+"\"";
        ResultSet rs = st.executeQuery(sql);
        Double longitude=0.0;
        Double latitude=0.0;
        while(rs.next()){
            latitude=rs.getDouble("latitude");
            longitude=rs.getDouble("longitude");
              
         }
         Double[] myList=new Double[2];
         myList[0]=latitude;
         myList[1]=longitude;
         return myList;
       
       
   }
   public static void findNearby( Connection conn ) throws Exception {
      Statement stmt = conn.createStatement();
      Scanner sc=new Scanner(System.in);
      String sql="";
      System.out.println("Press 1 to search using city name and 2 for latitude longitude"); 
      String input=sc.nextLine();
      String lat1="";
      String long1="";
      if(input.equals("1")){
          System.out.println("Enter City");
          String c=sc.nextLine();
          Double[] list=Coordinates(c,stmt);
          lat1=list[0].toString();
          long1=list[1].toString();
      }
      else{
        
     
        System.out.println("Enter Latitude");
        lat1=sc.nextLine();
        System.out.println("Enter Longitude");
        long1=sc.nextLine();
        
      }
      sql="SELECT city FROM INFO where DEGREES(ACOS(COS(RADIANS("+lat1+")) * COS(RADIANS(latitude)) *\n" +
            "COS(RADIANS("+long1+") - RADIANS(longitude)) +\n" +
            "SIN(RADIANS("+lat1+")) * SIN(RADIANS(latitude)))) < 5";
      
       ResultSet rs = stmt.executeQuery(sql);
      //STEP 5: Extract data from result set
        while(rs.next()){
         //Retrieve by column name
         String city = rs.getString("city");
         System.out.println(", City: " + city);
      }
      rs.close();
   }  
}

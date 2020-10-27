/**
 * lbettels
 *
 * Copyright (C) 2020 lbettels
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foo.bar;
import java.sql.*;
import java.util.Enumeration;

public class ExampleApp {
    private static String url = "";
    private static String user = "";
    private static String password="";
    private static Connection conn=null;

    public static void setup(){
        url="jdbc:mydriver:mysql://localhost:3306/your_db";
        user="root";
        password="your_root_pw";
    }

    public static void main(String[] args){

        //prints out the registered drivers
        System.out.println("Registered drivers: ");
        for (Enumeration<Driver> driverEnumeration = DriverManager.getDrivers(); driverEnumeration.hasMoreElements(); ) {
            System.out.println((driverEnumeration.nextElement().getClass().getName()));
        }

        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM test_table");
            System.out.println(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

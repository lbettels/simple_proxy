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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class MyProxyDriver implements Driver {
    private static Driver instance = new MyProxyDriver();

    static {
        try {
            DriverManager.registerDriver(MyProxyDriver.instance);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not register MyProxyDriver with DriverManager", e);
        }
    }

    @Override
    public boolean acceptsURL(final String url) {
        return url != null && url.startsWith("jdbc:mydriver:");
    }

    /**
      * Parses out the real JDBC connection URL by removing "mydriver:".
      *
      * @param url the connection URL
      * @return the parsed URL
      */
    private String extractRealUrl(String url) {
        return acceptsURL(url) ? url.replace("mydriver:", "") : url;
    }

    static List<Driver> registeredDrivers() {
        List<Driver> result = new ArrayList<Driver>();
        for (Enumeration<Driver> driverEnumeration = DriverManager.getDrivers(); driverEnumeration.hasMoreElements(); ) {
            result.add(driverEnumeration.nextElement());
        }
        return result;
    }

    @Override
    public Connection connect(String url, Properties properties) throws SQLException {
        // if there is no url, we have problems
        if (url == null) {
            throw new SQLException("url is required");
        }

        if( !acceptsURL(url) ) {
            return null;
        }

        // find the real driver for the URL
        Driver passThru = findPassthru(url);

        final Connection conn;

        try {
            conn =  passThru.connect(extractRealUrl(url), properties);
        } catch (SQLException e) {
            throw e;
        }

        return ConnectionWrapper.wrap(conn);
    }

    protected Driver findPassthru(String url) throws SQLException {

        String realUrl = extractRealUrl(url);
        Driver passthru = null;
        for (Driver driver: registeredDrivers() ) {
            try {
                if (driver.acceptsURL(extractRealUrl(url))) {
                    passthru = driver;
                    break;
                }
            } catch (SQLException e) {
            }
        }
        if( passthru == null ) {
            throw new SQLException("Unable to find a driver that accepts " + realUrl);
        }
        return passthru;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties properties) throws SQLException {
        return findPassthru(url).getPropertyInfo(url, properties);
    }

    @Override
    public int getMajorVersion() {
        // This is a bit of a problem since there is no URL to determine the passthru!
        return 1;
    }

    @Override
    public int getMinorVersion() {
        // This is a bit of a problem since there is no URL to determine the passthru!
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        // This is a bit of a problem since there is no URL to determine the passthru!
        return true;
    }

    // Note: @Override annotation not added to allow compilation using Java 1.6
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
}

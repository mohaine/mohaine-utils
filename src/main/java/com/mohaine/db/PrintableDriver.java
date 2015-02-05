package com.mohaine.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

public class PrintableDriver implements Driver {
	/*
	 * This class will proxy any connections made through DriverManager with a
	 * PrintableConnectionProxy,but it must be the first driver regisitered. To
	 * force this, either load this class from your startup class, or add the
	 * jvm arg -Djdbc.drivers=mohaine.db.PrintableDriver to the command line
	 */

	static ThreadLocal<String> theadLock = new ThreadLocal<String>();

	static {
		try {
			Enumeration<Driver> driversEnum = DriverManager.getDrivers();

			DriverManager.registerDriver(new PrintableDriver());

			// Move all the other drivers to the end
			while (driversEnum.hasMoreElements()) {
				Driver driver = (Driver) driversEnum.nextElement();
				if (!(driver instanceof PrintableDriver)) {
					DriverManager.deregisterDriver(driver);
					DriverManager.registerDriver(driver);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Driver#getMajorVersion()
	 */
	public int getMajorVersion() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Driver#getMinorVersion()
	 */
	public int getMinorVersion() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Driver#jdbcCompliant()
	 */
	public boolean jdbcCompliant() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Driver#acceptsURL(java.lang.String)
	 */
	public boolean acceptsURL(String arg0) throws SQLException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
	 */
	public Connection connect(String connectString, Properties props)
			throws SQLException {
		connectString = control.modifyConnection(connectString, props);
		if (theadLock.get() == null) {
			theadLock.set("");
			Connection conn = DriverManager.getConnection(connectString, props);

			if (conn != null) {

				SqlPrinter printer = control.getPrinterFor(connectString, conn);
				conn = new PrintableConnectionProxy(conn, printer);
			}

			theadLock.set(null);
			return conn;
		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Driver#getPropertyInfo(java.lang.String,
	 * java.util.Properties)
	 */
	public DriverPropertyInfo[] getPropertyInfo(String arg0, Properties arg1)
			throws SQLException {
		return null;
	}

	public interface PrintableDriverController {
		SqlPrinter getPrinterFor(String connectString, Connection conn);

		String modifyConnection(String connectString, Properties props);
	}

	public PrintableDriverController control = createControl();

	protected PrintableDriverController createControl() {

		String controlClass = System.getProperty("pd-control");
		if (controlClass != null && controlClass.length() > 0) {
			try {
				return (PrintableDriverController) Class.forName(controlClass)
						.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to create controlClass "
						+ controlClass, e);
			}
		}

		return new PrintableDriverController() {
			@Override
			public SqlPrinter getPrinterFor(String connectString,
					Connection conn) {
				return new SqlPrinterConsole();
			}

			@Override
			public String modifyConnection(String connectString,
					Properties props) {
				return connectString;
			}
		};
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}
}

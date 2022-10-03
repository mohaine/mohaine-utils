package com.mohaine.db.metadata.vendor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class H2Info extends GenericVendorInfo {

    public H2Info(Connection connection) throws SQLException {
        super(connection);
    }

}

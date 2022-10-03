package com.mohaine.db.metadata.vendor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class H2Apply extends GenericVendorApply {

    public H2Apply(Connection connection) throws SQLException {
        super(connection);
    }
}

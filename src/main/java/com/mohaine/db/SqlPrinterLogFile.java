package com.mohaine.db;

import java.io.File;
import java.io.FileOutputStream;

public class SqlPrinterLogFile extends SqlPrinterConsole {
    private static final Object LOCK = new Object();
    private File file;

    public SqlPrinterLogFile(File file) {
        this.file = file;
    }

    public SqlPrinterLogFile() {
        this(new File(System.getProperty("user.home") + "/Desktop", "db.log"));
    }

    public void logMessageNoNewline(String log) {
        synchronized (LOCK) {
            try {
                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(log.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void logMessage(Object sql) {
        logMessageNoNewline(sql.toString());
    }
}
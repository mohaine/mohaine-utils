package com.mohaine.db;

import java.io.File;
import java.io.FileOutputStream;

public class SqlCaptureLogFile extends SqlCaptureConsole {
    private static final Object LOCK = new Object();
    private File file;

    public SqlCaptureLogFile(File file) {
        this.file = file;
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
    public void logMessage(Object sql) {
        logMessageNoNewline(sql.toString());
    }
}

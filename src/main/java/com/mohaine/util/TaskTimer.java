package com.mohaine.util;

import java.util.ArrayList;
import java.util.List;

public class TaskTimer implements AutoCloseable {
    private long startTime;
    private long runTime;
    private String name;

    static ThreadLocal<TaskTimer> threadLocal = new ThreadLocal<TaskTimer>() {
        @Override
        protected TaskTimer initialValue() {
            return new TaskTimer(Thread.currentThread().getName() + " Timer");
        }
    };
    private TaskTimer parent;


    public static TaskTimer current() {
        return threadLocal.get();
    }

    private List<TaskTimer> subTimers;

    public TaskTimer(String name, Boolean autoStart) {
        this.name = name;
        reset();
        if (autoStart) {
            this.start();
        }
    }

    public TaskTimer() {
        this(null);
    }


    public TaskTimer(String name) {
        this(name, true);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void reset() {
        startTime = -1;
        runTime = 0;
    }

    public void start() {
        startTime = System.nanoTime();
        makeCurrent();
    }

    public void stop() {
        runTime += getActiveRunTime();
        startTime = -1;
        if (threadLocal.get() == this) {
            stopCurrent();
        }
    }

    public long getRunTime() {
        return getActiveRunTime() + runTime;
    }

    protected long getActiveRunTime() {
        if (startTime != -1) {
            return System.nanoTime() - startTime;
        }

        return 0;
    }

    public String getHumanRunTime() {
        return showNanosecondsInHumanForm(getRunTime());
    }

    public double getRunTimeInSeconds() {
        return ((double) getRunTime()) / 1000000000;
    }

    public String toStringReset() {
        String value = toString();
        reset();
        return value;
    }

    public String toStringRestart() {
        String value = toString();
        reset();
        start();
        return value;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        append(sb, this, 0, -1);
        return sb.toString();
    }

    static void append(StringBuffer sb, TaskTimer nt, int depth, long totalTime) {
        for (int i = 0; i < depth; i++) {
            sb.append("   ");
        }
        long runTime = nt.getRunTime();

        if (totalTime > 0 && runTime > 0) {
            sb.append(String.format("%.2f", ((double) (runTime) / (double) totalTime) * 100));
            sb.append("% ");
        }
        if (nt.name != null && nt.name.length() > 0) {
            sb.append(nt.name).append(' ');
        }
        sb.append(showNanosecondsInHumanForm(runTime));

        if (nt.subTimers != null) {
            sb.append("\r\n");
            for (int i = 0; i < nt.subTimers.size(); i++) {
                if (i > 0) {
                    sb.append("\r\n");
                }
                append(sb, nt.subTimers.get(i), depth + 1, runTime);
            }
        }
    }

    public static String showNanosecondsInHumanForm(long nanoSeconds) {

        StringBuffer sb = new StringBuffer();

        if (nanoSeconds < 1000) {
            sb.append(nanoSeconds).append(" Nanoseconds");
        } else if (nanoSeconds < 1000000) {
            double displayTime = Math.round((nanoSeconds) / 10);
            displayTime = displayTime / 100;
            sb.append(displayTime).append(" Microseconds");
        } else if (nanoSeconds < 1000000000) {
            double displayTime = Math.round((nanoSeconds) / 10000);
            displayTime = displayTime / 100;
            sb.append(displayTime).append(" Milliseconds");
        } else {

            long milliseconds = nanoSeconds / 1000000;
            if (milliseconds < 60000) {
                double displayTime = Math.round(((double) milliseconds) / 10);
                displayTime = displayTime / 100;
                sb.append(displayTime).append(" Seconds");
            } else if (milliseconds < (1000 * 60 * 60)) {
                double displayTime = Math.round(((double) milliseconds) / (10 * 60));
                displayTime = displayTime / 100;
                sb.append(displayTime).append(" Minutes");
            } else if (milliseconds < (1000 * 60 * 60 * 24)) {
                double displayTime = Math.round(((double) milliseconds) / (10 * 60 * 60));
                displayTime = displayTime / 100;
                sb.append(displayTime).append(" Hours");
            } else {
                double displayTime = Math.round(((double) milliseconds) / (10 * 60 * 60 * 24));
                displayTime = displayTime / 100;
                sb.append(displayTime).append(" Days");
            }
        }
        return sb.toString();
    }

    public TaskTimer getSub(String name) {
        if (subTimers == null) {
            subTimers = new ArrayList<TaskTimer>();
        }

        for (int i = 0; i < subTimers.size(); i++) {
            TaskTimer nt = subTimers.get(i);
            if (name.equals(nt.name)) {
                return nt;
            }
        }

        TaskTimer nt = new TaskTimer(name);
        nt.parent = this;
        subTimers.add(nt);
        return nt;
    }

    public List<TaskTimer> getChildren() {
        return subTimers;
    }

    public void makeCurrent() {
        threadLocal.set(this);
    }

    public void stopCurrent() {
        if (this.parent != null) {
            threadLocal.set(this.parent);
        }
    }

    @Override
    public void close() throws Exception {
        this.stop();
    }
}

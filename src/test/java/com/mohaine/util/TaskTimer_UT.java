package com.mohaine.util;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaskTimer_UT {


    @Test
    public void testChild() throws Exception {
        var tt = new TaskTimer("Parent");

        tt.makeCurrent();

        TaskTimer.current().startChild("Child1");
        Thread.sleep(100);

        TaskTimer.current().startSibling("Child2");
        Thread.sleep(25);

        tt.stop();
        Thread.sleep(200);


        assertTrue(tt.getRunTimeInSeconds() > 0.125);
        assertEquals(2, tt.getChildren().size());

        var child0 = tt.getChildren().get(0);
        assertTrue(child0.getRunTimeInSeconds() > 0.100);
        assertTrue(child0.getRunTimeInSeconds() < 0.150);


        var child1 = tt.getChildren().get(1);
        assertTrue(child1.getRunTimeInSeconds() > 0.025);
        assertTrue(child1.getRunTimeInSeconds() < 0.050);


        System.out.println("tt.getRunTimeInSeconds() = " + tt.getRunTimeInSeconds());
        System.out.println(tt);

    }

}

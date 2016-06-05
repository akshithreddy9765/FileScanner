package com.orgname.sdcard.filescanner;

import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.TestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class UtilityTest extends TestCase {

    @MediumTest
    public void testGetFileSizeInMetricFormat()
    {
        String fileSize = Utility.getFileSizeInMetricFormat(2000);
        assertTrue(fileSize.equalsIgnoreCase("1.95kb"));
    }
}
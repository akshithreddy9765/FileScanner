package com.orgname.sdcard.filescanner;

import android.test.SingleLaunchActivityTestCase;


public class DisplayFilesInfoActivityTest extends SingleLaunchActivityTestCase<DisplayFilesInfoActivity> {

    private DisplayFilesInfoActivity activity;
    public DisplayFilesInfoActivityTest()
    {
        super("com.orgname.sdcard.filescanner", DisplayFilesInfoActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        assertNotNull(activity);
    }
}

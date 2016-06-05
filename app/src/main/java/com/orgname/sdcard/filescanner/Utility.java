package com.orgname.sdcard.filescanner;

import java.util.Locale;

public class Utility {

    public static String getFileSizeInMetricFormat(double size) {
        String metric = "";
        // Logic to determine the file size units.
        if (size >= (Constants.ONE_KB
                * Constants.ONE_KB * Constants.ONE_KB)) {
            metric = Constants.GB;
            size = size
                    / (Constants.ONE_KB
                    * Constants.ONE_KB * Constants.ONE_KB);
        } else if (size >= (Constants.ONE_KB * Constants.ONE_KB)) {
            metric = Constants.MB;
            size = size
                    / (Constants.ONE_KB * Constants.ONE_KB);
        } else {
            metric = Constants.KB;
            size = size / Constants.ONE_KB;
        }
        String sizeWithLocale = String.format(Locale.getDefault(),
                Constants.TWO_DECIMALFORMATER, size);
        return sizeWithLocale + metric;
    }

    public static String[] getFormatArray() {

        String[] fileTypes = new String[]{".doc", ".docx", ".xls", ".txt", ".pdf"};
        return fileTypes;
    }

}

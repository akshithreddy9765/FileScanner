package com.orgname.sdcard.filescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DisplayFilesInfoActivity extends Activity {
    private String TAG = DisplayFilesInfoActivity.class.getSimpleName();
    private Activity activity;
    private ListView fileListView;
    private TextView totalFilesAverageView;
    private TextView top10FilesAverageView;
    private ListView frequencyCountList;
    private ProgressBar scanningProgress;
    //Files adapter
    private TopFilesAdapter fileAdapter;
    private FilesScanAsynTask filesScanAsynTask;
    private TreeMap<String, Integer> frequencyCountMap = new TreeMap<String, Integer>();
    private HashMap<String, Double> topSizedFiles = new HashMap<String, Double>();
    //Total files of SDCard list
    private ArrayList<SDCardFile> filesList = null;
    //Sorted based on size list
    private ArrayList<SDCardFile> sortedFiles = null;
    //Extensions array
    private String[] fleExtensions;
    //Adapter to display the frequency count
    private FrequencyCountAdapter frequencyAdapter;
    //Sorted frequency count map
    private TreeMap<String, Integer> sortedFrequencyCountMap;
    //Total files size
    private double totalFileSize;
    //Top 10 files sizes
    private double top10FileSize;
    private double totalFilesAverageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdcard_reader_activity);

        fileListView = (ListView) findViewById(R.id.file_list_view);
        fileAdapter = new TopFilesAdapter(R.layout.file_list_row,
                Utility.getFormatArray(), this);
        fileListView.setAdapter(fileAdapter);
        fleExtensions = Utility.getFormatArray();
        filesList = new ArrayList<SDCardFile>();
        sortedFiles = new ArrayList<SDCardFile>();
        sortedFrequencyCountMap = new TreeMap<String, Integer>();
        totalFilesAverageView = (TextView) findViewById(R.id.total_files_average_size);
        top10FilesAverageView = (TextView) findViewById(R.id.top_10_files_average_size);
        frequencyCountList = (ListView) findViewById(R.id.frequency_list_view);
        scanningProgress = (ProgressBar) findViewById(R.id.scan_progress);
        activity = this;
        if (savedInstanceState != null) {

            //Handling orientation change
            sortedFiles = savedInstanceState.getParcelableArrayList("sortedFiles");
            sortedFrequencyCountMap = (TreeMap) savedInstanceState.getSerializable("frequencyMap");

            if (sortedFiles.size() > 0) {
                fileAdapter.loadFiles(sortedFiles);
                frequencyAdapter = new FrequencyCountAdapter(sortedFrequencyCountMap);
                frequencyCountList.setAdapter(frequencyAdapter);
                frequencyAdapter.refresh();
                totalFilesAverageSize = savedInstanceState.getDouble("totalFilesAverageSize");
                top10FileSize = savedInstanceState.getDouble("top10FileSize");
                totalFilesAverageView.setText(getString(R.string.average_sd_card_file_size) + Utility.getFileSizeInMetricFormat(totalFilesAverageSize));
                top10FilesAverageView.setText(getString(R.string.average_top_10_files) + Utility.getFileSizeInMetricFormat(top10FileSize / sortedFiles.size()));
                scanningProgress.setVisibility(View.GONE);
            } else {
                Toast.makeText(DisplayFilesInfoActivity.this, getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
            }


        }

    }


    /**
     * File scanner asyn-task
     */
    private class FilesScanAsynTask extends AsyncTask<String, String, Void> {
        ProgressDialog dialog;

        @Override
        protected Void doInBackground(String... params) {
            File sdcardLoc = Environment.getExternalStorageDirectory();
            for (String fileExtension : fleExtensions) {
                frequencyCountMap.put(fileExtension, 0);
            }
            readDir(sdcardLoc);
            String fileExt = null;
            Log.d(TAG, "SDCard files");
            for (SDCardFile file : filesList) {
                fileExt = file.getExtension();
                topSizedFiles.put(file.getFileName(), file.getSize());
                int count = frequencyCountMap.get(fileExt);
                frequencyCountMap.put(fileExt, ++count);
                totalFileSize = totalFileSize + file.getSize();
                Log.d(TAG, "FileName:" + file.getFileName() + " fileExt:" + fileExt + " count:" + count);

            }
            TopSizeFilesComparator topSizeFilesComparator = new TopSizeFilesComparator(topSizedFiles);
            TreeMap<String, Double> topFilesMap = new TreeMap<String, Double>(topSizeFilesComparator);
            topFilesMap.putAll(topSizedFiles);

            // get the top 10 files
            Iterator it = topFilesMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String name = pair.getKey().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (sortedFiles.size() < 10) {
                        SDCardFile file = getFile(name);
                        sortedFiles.add(file);
                        top10FileSize = top10FileSize + file.getSize();
                    } else {
                        break;
                    }
                }
                it.remove();
            }

            ValueComparator bvc = new ValueComparator(frequencyCountMap);
            sortedFrequencyCountMap = new TreeMap<String, Integer>(bvc);
            sortedFrequencyCountMap.putAll(frequencyCountMap);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            scanningProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (sortedFiles.size() > 0) {
                fileAdapter.loadFiles(sortedFiles);
            } else {
                Toast.makeText(DisplayFilesInfoActivity.this, getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
            }

            frequencyAdapter = new FrequencyCountAdapter(sortedFrequencyCountMap);
            frequencyCountList.setAdapter(frequencyAdapter);
            frequencyAdapter.refresh();
            totalFilesAverageSize = totalFileSize / filesList.size();
            totalFilesAverageView.setText(getString(R.string.average_sd_card_file_size) + Utility.getFileSizeInMetricFormat(totalFilesAverageSize));
            top10FilesAverageView.setText(getString(R.string.average_top_10_files) + Utility.getFileSizeInMetricFormat(top10FileSize / sortedFiles.size()));
            scanningProgress.setVisibility(View.GONE);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);

            if (sortedFiles.size() > 0) {
                fileAdapter.loadFiles(sortedFiles);
            } else {
                Toast.makeText(DisplayFilesInfoActivity.this, getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
            }

            frequencyAdapter = new FrequencyCountAdapter(sortedFrequencyCountMap);
            frequencyCountList.setAdapter(frequencyAdapter);
            frequencyAdapter.refresh();
            totalFilesAverageSize = totalFileSize / filesList.size();
            totalFilesAverageView.setText(getString(R.string.average_sd_card_file_size) + Utility.getFileSizeInMetricFormat(totalFilesAverageSize));
            top10FilesAverageView.setText(getString(R.string.average_top_10_files) + Utility.getFileSizeInMetricFormat(top10FileSize / sortedFiles.size()));
            scanningProgress.setVisibility(View.GONE);
        }
    }


    /**
     * Sort files based on frequency count
     */
    class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;

        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    /**
     * Used to sort the files based on file size
     */
    class TopSizeFilesComparator implements Comparator<String> {

        Map<String, Double> base;

        public TopSizeFilesComparator(Map<String, Double> base) {
            this.base = base;
        }

        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    /**
     * Read file
     *
     * @param file
     */
    private void readDir(File file) {
        try {
            File[] files = file.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    for (String fileExtension : fleExtensions) {
                        String fileName = pathname.getName();
                        if (fileName != null)
                            fileName = fileName.toLowerCase(Locale.getDefault());
                        if (fileName.endsWith(fileExtension)
                                || ((pathname.isDirectory()))) {
                            return true;
                        }
                    }

                    return false;
                }
            });

            if (files != null) {
                for (int index = 0; index < files.length; index++) {
                    if (files[index].isDirectory()) {
                        readDir(files[index]);
                    } else {
                        if (files[index].isAbsolute()
                                && !files[index].isHidden()
                                && files[index].isFile()) {
                            String name = files[index].getName();
                            if (filesScanAsynTask.isCancelled()) {
                                return;
                            }
                            filesList.add(new SDCardFile(
                                    files[index].getAbsolutePath()
                                    , name, name.substring(name.lastIndexOf('.')),
                                    (double) files[index].length()));

                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception while reading :" + e.getMessage());
        }
    }

    /**
     * Get file based on name
     *
     * @param fileName
     * @return SDCardFile if any file found with name given
     */
    private SDCardFile getFile(String fileName) {
        for (SDCardFile file : filesList) {
            if (file.getFileName().equalsIgnoreCase(fileName)) {
                return file;
            }
        }
        return null;
    }

    /**
     * StartScan button click handler
     *
     * @param view
     */
    public void startScan(View view) {
        filesScanAsynTask = new FilesScanAsynTask();
        filesScanAsynTask.execute();
    }

    /**
     * StopScan buuton click handler
     *
     * @param view
     */
    public void stopScan(View view) {
        if (filesScanAsynTask != null && filesScanAsynTask.getStatus() == AsyncTask.Status.RUNNING) {
            filesScanAsynTask.cancel(true);
        }
    }

    @Override
    public void onBackPressed() {

        if (filesScanAsynTask != null && filesScanAsynTask.getStatus() == AsyncTask.Status.RUNNING) {
            filesScanAsynTask.cancel(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("sortedFiles", sortedFiles);
        outState.putSerializable("frequencyMap", sortedFrequencyCountMap);
        outState.putDouble("totalFilesAverageSize", totalFilesAverageSize);
        outState.putDouble("top10FileSize", top10FileSize);
        super.onSaveInstanceState(outState);

    }

}

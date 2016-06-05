
package com.orgname.sdcard.filescanner;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;


public class TopFilesAdapter extends ArrayAdapter<SDCardFile> {

    private String TAG = TopFilesAdapter.class.getSimpleName();
    private String[] fleExtensions;
    private LayoutInflater inflater;
    private Activity activity;
    private Holder viewHolder;
    private ArrayList<SDCardFile> filesList = null;

    /**
     * TopFilesAdapter class Constructor to create TopFilesAdapter class
     * object.
     *
     * @param resource       the resource ID for a layout file containing a TextView which
     *                       get assigned a file name.
     * @param fileExtensions a file extension to filter the device file system.
     * @param activity       Activity instance where the adapter is instantiated.
     * @throws NullPointerException throws null pointer exception if fileExtension is null.
     */
    public TopFilesAdapter(int resource, String[] fileExtensions,
                           Activity activity) throws NullPointerException {
        super(activity, resource);
        this.fleExtensions = fileExtensions;

        this.activity = activity;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        filesList = new ArrayList<SDCardFile>();
        String dot = ".";
        if (fleExtensions != null) {
            for (String fileExtension : fleExtensions) {
                if (!fileExtension.startsWith(dot)) {
                    fileExtension = dot + fileExtension;
                }
            }
        }
    }

    @Override
    public int getCount() {
        return filesList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.file_list_row, parent, false);
            viewHolder = new Holder();
            viewHolder.fileNameView = (TextView) view
                    .findViewById(R.id.filename);
            viewHolder.sizeView = (TextView) view.findViewById(R.id.size);

            view.setTag(viewHolder);
        } else {
            viewHolder = (Holder) view.getTag();
        }
        SDCardFile item = (SDCardFile) filesList.get(position);
        viewHolder.fileNameView.setText(item.getFileName());
        viewHolder.fileNameView.setTag(item.getPath());

        StringBuffer sb = new StringBuffer();
        String size = String.format(Locale.getDefault(),
                Constants.TWO_DECIMALFORMATER, item.getFormattedSize());
        sb.append(size).append(item.getMetric());
        viewHolder.sizeView.setText(sb.toString());

        return view;

    }


    /**
     * Holder class to hold list row views.
     */
    private class Holder {
        private TextView fileNameView;
        private TextView sizeView;
    }

    public void loadFiles(ArrayList<SDCardFile> filesList) {
        this.filesList.clear();
        this.filesList = filesList;
        notifyDataSetChanged();
    }

}

package tn.isi.ussef.mytracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tn.isi.ussef.mytracker.Model.TrackerItem;
import tn.isi.ussef.mytracker.R;

/**
 * Created by Ussef on 3/29/2017.
 */
public class TrackerAdapter extends BaseAdapter {
    private Context context ;
    private OnItemClickListener listener ;
    public ArrayList<TrackerItem> listnewsDataAdpater ;
    public interface OnItemClickListener {
        void onItemClick(TrackerItem item);
    }
    public TrackerAdapter(ArrayList<TrackerItem>  listnewsDataAdpater, Context context, OnItemClickListener listener) {
        this.listnewsDataAdpater=listnewsDataAdpater;
        this.context = context;
        this.listener = listener;
    }
    // View lookup cache
    private static class ViewHolder {
        TextView txtUserName;
        TextView txtPhoneNumber;
        public void bind(View v){
            txtUserName = (TextView) v.findViewById(R.id.txtUser);
            txtPhoneNumber = (TextView) v.findViewById(R.id.txtPhone);
        }
        public void update(TrackerItem item){
            this.txtPhoneNumber.setText(item.getPhoneNumber());
            this.txtUserName.setText(item.getUserName());
        }
    }

    @Override
    public int getCount() {
        return listnewsDataAdpater.size();
    }

    @Override
    public String getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater mInflater = LayoutInflater.from(context);
        final   TrackerItem s = listnewsDataAdpater.get(position);
        View result;
        if (s.UserName.equals("NoTicket")){
            result = mInflater.inflate(R.layout.news_ticket_no_news, null);
        } else{
            ViewHolder viewHolder = new ViewHolder();
            result = mInflater.inflate(R.layout.single_row_contact, null);
            viewHolder.bind(result);
            viewHolder.update(s);
            result.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(s);
                }
            });
        }
        return result;
    }

}

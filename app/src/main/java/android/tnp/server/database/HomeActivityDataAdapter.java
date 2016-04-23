package android.tnp.server.database;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.g38.tnp.R;
import android.support.v7.widget.RecyclerView;
import android.tnp.DAO.CreateDB;
import android.tnp.activities.DetailsActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ritik on 4/23/2016.
 */
public class HomeActivityDataAdapter extends RecyclerView.Adapter<HomeActivityDataAdapter.DataObjectHolder> {
    private int count, previousPosition = 0;
    ArrayList<String> listItems;
    LayoutInflater mInflater;
    Context context;
    public HomeActivityDataAdapter(Context context, LayoutInflater mInflater){
        this.mInflater = mInflater;
        CreateDB dbHelper = new CreateDB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        listItems = CreateDB.getDataForHomeActivity(db);
        db.close();
        dbHelper.close();
        count = listItems.size();
        this.context = context;
    }

    @Override
    public HomeActivityDataAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DataObjectHolder dataObjectHolder = new DataObjectHolder(mInflater.inflate(R.layout.list, parent, false));
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.textView.setText(listItems.get(position));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.container.setLayoutParams(layoutParams);
        MyUtils.homeActivityList(holder, position > previousPosition);
        previousPosition = position;
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener{
        RelativeLayout container;
        TextView textView;
        public DataObjectHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.tvListItem);
            container = (RelativeLayout)itemView.findViewById(R.id.rlHomeList);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DetailsActivity.class);
            CreateDB dbHelper = new CreateDB(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(CreateDB.TABLEDISPLAY,new String[]{CreateDB.SUBJECT, CreateDB.DATA},null,null,null,null,"ID DESC");
            cursor.moveToPosition(getAdapterPosition());
            intent.putExtra("data", cursor.getString(1));
            intent.putExtra("subject", cursor.getString(0));
            context.startActivity(intent);
            cursor.close();
            db.close();
            dbHelper.close();
        }
    }
}

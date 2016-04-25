package android.tnp.server.database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.g38.tnp.R;
import android.support.v7.widget.RecyclerView;
import android.tnp.DAO.BeanPlacementData;
import android.tnp.activities.DetailsActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

/**
 * Created by ritik on 4/25/2016.
 */
public class StarredList extends RecyclerView.Adapter<StarredList.DataObjectHolder>  {
    Context context;
    ArrayList<String> arrayList;
    int count;
    LayoutInflater mInflater;
    ArrayList<BeanPlacementData> list= new ArrayList<>();
    public StarredList(Context context, LayoutInflater mInflater){
        this.context = context;
        this.mInflater = mInflater;
        arrayList = displayContent();
        count = arrayList.size();
    }

    private ArrayList<String> displayContent(){
        BeanPlacementData obj;
        SharedPreferences sp = context.getSharedPreferences("starred", Context.MODE_PRIVATE);
        String subject="";
        String data="";
        int i=sp.getInt("count",0);

        for(int k=0;k<=i;k++){
            if(!(sp.getString("subject"+k,subject).equals(""))){
                obj=new BeanPlacementData();
                obj.setSubject(sp.getString("subject"+k,subject));
                obj.setData(sp.getString("data" + k, data));
                list.add(obj);
                //str.append(sp.getString("subject"+k,subject)+"\n");
            }
        }
        ArrayList<String> textString = new ArrayList<String>();
        if(list.size()==0)
            Toast.makeText(context,"Nothing Starred",Toast.LENGTH_LONG).show();
        for(int j=0;j < list.size();j++){
            textString.add(list.get(j).getSubject());
        }
        return textString;
    }
    @Override
    public StarredList.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DataObjectHolder dataObjectHolder = new DataObjectHolder(mInflater.inflate(R.layout.list, parent, false));
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(StarredList.DataObjectHolder holder, int position) {
        holder.textView.setText(arrayList.get(position));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.container.setLayoutParams(layoutParams);
        YoYo.with(Techniques.Pulse).duration(1000).playOn(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return count;
    }
    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            String str = list.get(getAdapterPosition()).getData();
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("id","1");
            intent.putExtra("data", str);
            intent.putExtra("subject", list.get(getAdapterPosition()).getSubject());
            context.startActivity(intent);
        }
    }
}

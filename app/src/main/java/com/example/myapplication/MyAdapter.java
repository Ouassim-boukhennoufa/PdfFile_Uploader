package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
RecyclerView recyclerView;
    Context context;
    ArrayList<String> items=new ArrayList<>();
    ArrayList<String> urls=new ArrayList<>();

    public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items,ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls=urls;
    }



    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler,parent,false);
        return new ViewHolder(view);
    }


    public void onBindViewHolder( ViewHolder holder, int position) {
        holder.namofile.setText(items.get(position));
    }


    public int getItemCount() {

        return items.size();
    }

    public void update(String name, String urrl) {
        items.add(name);
        urls.add(urrl);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
    TextView namofile;
    ImageButton image;
    public ViewHolder(final View itemView) {
        super(itemView);
        namofile=itemView.findViewById(R.id.pdf);
        image=itemView.findViewById(R.id.download);
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int posit=recyclerView.getChildLayoutPosition(itemView);
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(urls.get(posit)));
                context.startActivity(intent);
            }
        });
    }
}
}

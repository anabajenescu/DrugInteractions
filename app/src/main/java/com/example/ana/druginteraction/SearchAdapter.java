package com.example.ana.druginteraction;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    Context context;
    ArrayList<String> drugNameList;
    ArrayList<String> drugIdList;
    String pos;

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView drug_name;
        TextView drug_id;

        public SearchViewHolder(View itemView) {
            super(itemView);
            drug_name = (TextView) itemView.findViewById(R.id.drugName_text);
            drug_id = (TextView) itemView.findViewById(R.id.drugId_text);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> drugNameList, ArrayList<String> drugIdList, String pos) {
        this.context = context;
        this.drugNameList = drugNameList;
        this.drugIdList = drugIdList;
        this.pos = pos;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, int position) {
        holder.drug_name.setText(drugNameList.get(position));
        holder.drug_id.setText(drugIdList.get(position));
        holder.drug_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String drugName = holder.drug_name.getText().toString();
                String drugId = holder.drug_id.getText().toString();

                Intent intent = new Intent("searchDrug");
                intent.putExtra("drugName", drugName);
                intent.putExtra("drugId", drugId);
                intent.putExtra("position", pos);

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return drugNameList.size();
    }
}

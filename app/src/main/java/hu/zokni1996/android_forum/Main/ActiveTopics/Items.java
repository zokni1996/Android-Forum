package hu.zokni1996.android_forum.Main.ActiveTopics;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hu.zokni1996.android_forum.R;

public class Items extends RecyclerView.Adapter<ContactViewHolder> {

    private List<ItemAdapter> contactList;
    private Context context;

    public Items(List<ItemAdapter> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {
        ItemAdapter itemAdapter = contactList.get(position);
        contactViewHolder.textViewWhoWrite.setText(itemAdapter.getWhoWrite());
        contactViewHolder.textViewWhereWrite.setText(itemAdapter.getWhereWrite());
        contactViewHolder.textViewWhatWrite.setText(itemAdapter.getWhatWrite());
        contactViewHolder.textViewWhenWrite.setText(itemAdapter.getWhenWrite());
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.main_card_view, viewGroup, false);

        return new ContactViewHolder(context, itemView);
    }


}

package hu.zokni1996.android_forum.Main.ActiveTopics;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import hu.zokni1996.android_forum.Main.Main;
import hu.zokni1996.android_forum.R;

public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected TextView textViewWhoWrite;
    protected TextView textViewWhereWrite;
    protected TextView textViewWhatWrite;
    protected TextView textViewWhenWrite;
    private Context context;

    public ContactViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        textViewWhoWrite = (TextView) view.findViewById(R.id.textViewWhoWrite);
        textViewWhereWrite = (TextView) view.findViewById(R.id.textViewWhereWrite);
        textViewWhatWrite = (TextView) view.findViewById(R.id.textViewWhatWrite);
        Linkify.addLinks(textViewWhatWrite, Linkify.ALL);
        textViewWhenWrite = (TextView) view.findViewById(R.id.textViewWhenWrite);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}

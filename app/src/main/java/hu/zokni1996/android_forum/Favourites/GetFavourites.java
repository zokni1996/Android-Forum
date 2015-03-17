package hu.zokni1996.android_forum.Favourites;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;

import hu.zokni1996.android_forum.Main.Web.WebFragment;
import hu.zokni1996.android_forum.R;

public class GetFavourites {
    Context context;

    public GetFavourites(final Context context) {
        this.context = context;
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.main_favourite_list, true)
                .title(R.string.title_activity_favourites)
                .positiveText(R.string.Ok)
                .cancelable(false)
                .build();
        final ListView listView = (ListView) dialog.getCustomView().findViewById(R.id.favouritesListView);
        final TextView textView = (TextView) dialog.getCustomView().findViewById(R.id.favouritesTextView);
        final String string = context.getSharedPreferences("FAVOURITES", Context.MODE_PRIVATE).getString("Favourites", "");
        Log.i("FIRST_GET", "" + string);
        if (string != null) {
            if (!string.equals("")) {
                listView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                String[] strings = string.split("THIS_IS_THE_SPLIT");
                final String[] name = {""};
                final String[] link = {""};
                for (String string1 : strings) {
                    String[] oneName = string1.split("THIS_IS_LINK_SPLIT");
                    name[0] += oneName[1];
                    link[0] += oneName[0];
                    name[0] += "THIS_IS_SECOND_SPLIT";
                    link[0] += "THIS_IS_SECOND_SPLIT";
                }
                final String[] names = name[0].split("THIS_IS_SECOND_SPLIT");
                final String[] links = link[0].split("THIS_IS_SECOND_SPLIT");
                ArrayAdapter arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, names);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        new WebFragment().LoadURL(links[position]);
                    }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        new AlertDialogWrapper.Builder(context)
                                .setMessage(context.getString(R.string.ListItemDelete) + names[position])
                                .setCancelable(false)
                                .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        links[position] = "";
                                        names[position] = "";
                                        String string = "";
                                        for (int i = 0; i < links.length; i++) {
                                            if (!links[i].equals("")) {
                                                string += links[i];
                                                string += "THIS_IS_LINK_SPLIT";
                                            }
                                            if (!names[i].equals("")) {
                                                string += names[i];
                                                string += "THIS_IS_THE_SPLIT";
                                            }
                                        }
                                        context.getSharedPreferences("FAVOURITES", Context.MODE_PRIVATE).edit().putString("Favourites", string).commit();
                                        dialogInterface.dismiss();
                                        final String stringTwo = context.getSharedPreferences("FAVOURITES", Context.MODE_PRIVATE).getString("Favourites", "");
                                        if (stringTwo != null) {
                                            if (!stringTwo.equals("")) {
                                                listView.setVisibility(View.VISIBLE);
                                                textView.setVisibility(View.INVISIBLE);
                                                String[] stringTwoArray = string.split("THIS_IS_THE_SPLIT");
                                                name[0] = "";
                                                link[0] = "";
                                                for (String string1 : stringTwoArray) {
                                                    String[] oneName = string1.split("THIS_IS_LINK_SPLIT");
                                                    name[0] += oneName[1];
                                                    link[0] += oneName[0];
                                                    name[0] += "THIS_IS_SECOND_SPLIT";
                                                    link[0] += "THIS_IS_SECOND_SPLIT";
                                                }
                                                final String[] names = name[0].split("THIS_IS_SECOND_SPLIT");
                                                ArrayAdapter arrayAdapterTwo = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, names);
                                                listView.setAdapter(arrayAdapterTwo);
                                            } else {
                                                listView.setVisibility(View.INVISIBLE);
                                                textView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                })
                                .setTitle(R.string.Delete)
                                .show();
                        return true;
                    }


                });
            } else {
                listView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
            }
        }
        dialog.show();
    }
}

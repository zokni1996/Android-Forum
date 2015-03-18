package hu.zokni1996.android_forum.Favourites;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import hu.zokni1996.android_forum.Main.Main;
import hu.zokni1996.android_forum.R;

public class GetFavourites {
    private List<Bookmark> mBookmarksList = new ArrayList<>();

    public GetFavourites(final Context context) {
        final BookmarksHelper db = new BookmarksHelper(context);
        mBookmarksList = db.getAllBooks();
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.main_favourite_list, false)
                .title(R.string.title_activity_favourites)
                .positiveText(R.string.Ok)
                .cancelable(true)
                .build();
        final ListView listView = (ListView) dialog.getCustomView().findViewById(R.id.favouritesListView);
        final TextView textView = (TextView) dialog.getCustomView().findViewById(R.id.favouritesTextView);
        if (mBookmarksList.size() != 0) {
            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            String[] strings = new String[mBookmarksList.size()];
            for (int i = 0; i < mBookmarksList.size(); i++)
                strings[i] = mBookmarksList.get(i).getName();
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, strings);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    Main.getWebFragment().LoadURL(mBookmarksList.get(position).getUrl());
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    Bookmark bookmark = mBookmarksList.get(position);
                    new AlertDialogWrapper.Builder(context)
                            .setMessage(context.getString(R.string.ListItemDelete) + bookmark.getName())
                                    .setCancelable(true)
                                    .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            db.deleteBook(mBookmarksList.get(position));
                                            if (!mBookmarksList.isEmpty())
                                                mBookmarksList.clear();
                                            mBookmarksList = db.getAllBooks();
                                            if (mBookmarksList.size() != 0) {
                                                String[] strings = new String[mBookmarksList.size()];
                                                for (int i = 0; i < mBookmarksList.size(); i++)
                                                    strings[i] = mBookmarksList.get(i).getName();
                                                listView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, strings));
                                            } else {
                                                listView.setVisibility(View.GONE);
                                                textView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    })
                                    .setTitle(R.string.Delete)
                                    .show();
                    return true;
                }
            });
        } else {
            listView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }
}

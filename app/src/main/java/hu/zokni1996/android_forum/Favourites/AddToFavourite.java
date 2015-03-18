package hu.zokni1996.android_forum.Favourites;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import hu.zokni1996.android_forum.R;

public class AddToFavourite {

    String link;
    Context context;
    EditText editText;

    public AddToFavourite(final String link, final Context context) {
        this.link = link;
        this.context = context;
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.main_add_favourite, true)
                .title(context.getString(R.string.AddFavourite))
                .positiveText(context.getString(R.string.Save))
                .cancelable(true)
                .negativeText(context.getString(R.string.cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        new BookmarksHelper(context).addBookmark(new Bookmark(editText.getText().toString(), link));
                        dialog.dismiss();
                        Toast.makeText(context, context.getString(R.string.SavedSuccessFully), Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        editText = (EditText) dialog.getCustomView().findViewById(R.id.editTextFavouriteName);
        final View buttonSAVE = dialog.getActionButton(DialogAction.POSITIVE);
        buttonSAVE.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSAVE.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog.show();
        buttonSAVE.setEnabled(false);
    }
}

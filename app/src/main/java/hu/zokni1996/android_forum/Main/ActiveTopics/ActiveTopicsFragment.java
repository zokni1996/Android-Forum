package hu.zokni1996.android_forum.Main.ActiveTopics;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hu.zokni1996.android_forum.R;


public class ActiveTopicsFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    CardView cardView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_active_topics, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.cardList);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarActiveTopics);
        cardView = (CardView) view.findViewById(R.id.card_viewActiveTopicsFailed);
        progressBar.setIndeterminate(true);
        linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        new DataForum().execute();
        return view;
    }

    private class DataForum extends AsyncTask<String, Void, String> {
        String[] strings = new String[4];

        @Override
        protected String doInBackground(String... params) {
            String back = "+";
            for (int i = 0; i < strings.length; i++)
                strings[i] = "";
            try {
                Document document = Jsoup.connect("http://android-forum.hu/feed.php?mobile=mobile").timeout(10000).get();
                Elements entry = document.select("entry");
                for (int i = 0; i < entry.size(); i++) {
                    /* Get all the titles */
                    Elements title = entry.get(i).select("title");
                    for (int j = 0; j < title.size(); j++) {
                        Elements titleAttr = title.get(j).getElementsByAttributeValueContaining("type", "html");
                        for (int l = 0; l < titleAttr.size(); l++) {

                            strings[0] += titleAttr.get(l).text()
                                    .replace("<![CDATA[", "")
                                    .replace("]]>", "");
                            strings[0] += "THIS_IS_THE_SPLIT";
                        }
                    }
                    /* Get all the contents*/
                    Elements content = entry.get(i).select("content");
                    for (int j = 0; j < content.size(); j++) {
                         /* Get the latest post time */
                        strings[3] += content.get(j).text() + "THIS_IS_THE_SPLIT";
                        String string = content.get(j).text();
                        Document document1 = Jsoup.parse(string);
                        Element element = document1.select("p").first();
                        strings[2] += element.select("a").first().text() + "THIS_IS_THE_SPLIT";

                        String s = "";
                        String resultString = "";
                        s += content.get(j).text()
                                .replace("<br />", "THIS_IS_THE_NEW_LINE")
                                .replace(getActivity().getBaseContext().getString(R.string.statistic_send_who), "THIS_IS_THE_NEW_LINE ж");
                        int db = 0;
                        for (int l = 0; l < s.length(); l++) {
                            if (s.charAt(l) == 'ж') {
                                while (db != l) {
                                    resultString += s.charAt(db);
                                    db++;
                                }
                            }
                        }

                        String ss = Jsoup.parse(resultString, "UTF-8").body().text();
                        strings[1] += Jsoup.parse(ss, "UTF-8").body().ownText().replace("THIS_IS_THE_NEW_LINE", "\r\n") + "THIS_IS_THE_SPLIT";
                    }
                }
            } catch (IOException e) {
                Log.i("doInBackground", "" + e);
                back = "-";
            }
            return back;
        }

        @Override
        protected void onPostExecute(String back) {
            progressBar.setVisibility(View.GONE);
            if (back.equals("+")) {
                cardView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                List<ItemAdapter> list = new ArrayList<>();
                String[] title = strings[0].split("THIS_IS_THE_SPLIT");
                String[] content = strings[1].split("THIS_IS_THE_SPLIT");
                String[] who = strings[2].split("THIS_IS_THE_SPLIT");
                String[] when = strings[3].split("THIS_IS_THE_SPLIT");
                String whenParsed = getTime(when);
                String[] whenArrayParsed = whenParsed.split("THIS_IS_THE_SPLIT");
                for (int i = 0; i < content.length; i++) {
                    ItemAdapter itemAdapter = new ItemAdapter();
                    itemAdapter.whereWrite = title[i];
                    itemAdapter.whatWrite = content[i];
                    itemAdapter.whenWrite = whenArrayParsed[i];
                    itemAdapter.whoWrite = getActivity().getBaseContext().getString(R.string.who_write) + who[i];
                    list.add(itemAdapter);
                }


                Items items = new Items(list, getActivity().getBaseContext());
                recyclerView.setAdapter(items);

            } else {
                recyclerView.setVisibility(View.GONE);
                cardView.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(back);
        }
    }

    private String getTime(String[] CONTENT_ARRAY_TO_UPDATED_TIME) {
        String updatedString = "";
        for (String aContentArray : CONTENT_ARRAY_TO_UPDATED_TIME) {
            boolean you = true;
            int j = 0;
            while (you) {
                if (aContentArray.charAt(j) == '<')
                    if (aContentArray.charAt(j + 1) == '/')
                        if (aContentArray.charAt(j + 2) == 'a')
                            if (aContentArray.charAt(j + 3) == '>')
                                if (aContentArray.charAt(j + 4) == ' ')
                                    if (aContentArray.charAt(j + 5) == '—') {
                                        boolean there = true;
                                        int a = j + 6;
                                        while (there) {
                                            updatedString += aContentArray.charAt(a);
                                            a++;
                                            if (aContentArray.charAt(a) == '<') {
                                                updatedString += "THIS_IS_THE_SPLIT";
                                                there = false;
                                                you = false;
                                            }
                                        }
                                    }
                j++;
            }
        }
        return updatedString;
    }

}

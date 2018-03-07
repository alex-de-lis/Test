package com.alexdelis.test;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArticleActivity extends AppCompatActivity {

    ProgressBar pg;
    TextView Title,Article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Title=findViewById(R.id.title);
        Article=findViewById(R.id.article);
        pg=findViewById(R.id.ArtPG);
        Intent intent = getIntent();
        String article=intent.getStringExtra("article");
        DownloadAll(article);
    }


    private class AsyncTaskForArt extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String myurl = "http://mikonatoruri.win/post.php?article="+params[0];
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(myurl)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.contains("java.net"))
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Нет соединения с интернетом", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
            else if(result.contains("error"))
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Ошибка ответа сервера", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
            JSONObject JO;
            try {
                JO=new JSONObject(result);
                GenerateText(JO);
                pg.setVisibility(View.INVISIBLE);
                Title.setVisibility(View.VISIBLE);
                Article.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPreExecute(){}
    }

    private void DownloadAll(String article)
    {
        if(isOnline()!=null)
        {
            ArticleActivity.AsyncTaskForArt at=new ArticleActivity.AsyncTaskForArt();
            at.execute(article);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Нет соединения с интернетом", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    protected NetworkInfo isOnline()
    {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        return cm.getActiveNetworkInfo();
    }

    void GenerateText(JSONObject JO) throws JSONException {
        String team1="team1", team2="team2",time="time",tournament="tournament",place="place",article="article",prediction="prediction", text;
        String header="header", Text="text";
        StringBuilder sb= new StringBuilder();

        text=JO.getString(team1)+" - "+JO.getString(team2)+"\n"+JO.getString(time)+"\n"+JO.getString(tournament)+"\nМесто: "+JO.getString(place);
        Title.setText(text);

        JSONArray JMas=JO.getJSONArray(article);
        JSONObject NJO;
        for(int i=0;i<JMas.length();i++)
        {
            NJO=JMas.getJSONObject(i);
            sb.append("\n"+NJO.getString(header)+"\n"+NJO.getString(Text)+"\n");
        }
        sb.append("Прогноз\n"+JO.getString(prediction));
        Article.setText(sb);
    }
}

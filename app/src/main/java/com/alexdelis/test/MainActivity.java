package com.alexdelis.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ProgressBar pg;
    ListView SportsList;
    Spinner Sports;
    Button BTN;
    String articles[], choose[]={"football", "hockey", "tennis", "basketball", "volleyball", "cybersport"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Sports=findViewById(R.id.Spiner);
        pg=findViewById(R.id.PG);
        BTN=findViewById(R.id.Btn);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.sports, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sports.setAdapter(adapter);
        SportsList=findViewById(R.id.ListOfArt);
        Sports.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                SportsList.setAdapter(null);
                ((TextView) parent.getChildAt(0)).setTextSize(30);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                DownloadAll(choose[selectedItemPosition]);
                pg.setVisibility(View.VISIBLE);
                SportsList.setVisibility(View.INVISIBLE);
                BTN.setVisibility(View.INVISIBLE);
                /*AsyncTaskForListArt SpinAT=new AsyncTaskForListArt();
                SpinAT.execute(choose[selectedItemPosition]);*/
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Sports.setSelected(true);
        SportsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id)
            {
                Intent Article= new Intent(MainActivity.this, ArticleActivity.class);
                Article.putExtra("article",articles[position]);
                startActivity(Article);
            }
        });
    }

    private void DownloadAll(String sport)
    {
        if(isOnline()!=null)
        {
            AsyncTaskForListArt at=new AsyncTaskForListArt();
            at.execute(sport);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Нет соединения с интернетом", Toast.LENGTH_SHORT);
            toast.show();
            pg.setVisibility(View.INVISIBLE);
            BTN.setVisibility(View.VISIBLE);
        }
    }

    public void OnBtnClick(View view)
    {
        pg.setVisibility(View.VISIBLE);
        BTN.setVisibility(View.INVISIBLE);
        DownloadAll(choose[Sports.getSelectedItemPosition()]);
    }

    protected NetworkInfo isOnline()
    {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        return cm.getActiveNetworkInfo();
    }

    private class AsyncTaskForListArt extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String myurl = "http://mikonatoruri.win/list.php?category="+params[0];
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
            String mas[];
            String title="title", article="article";
            JSONArray JMas;
            JSONObject JO;
            int len;
            if(result.contains("java.net"))
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Нет соединения с интернетом", Toast.LENGTH_SHORT);
                toast.show();
                pg.setVisibility(View.INVISIBLE);
                BTN.setVisibility(View.VISIBLE);
            }
            else {
                try {
                    JO = new JSONObject(result);
                    JMas = JO.getJSONArray("events");
                    len = JMas.length();
                    mas = new String[len];
                    articles = new String[len];
                    for (int i = 0; i < len; i++) {
                        JO = JMas.getJSONObject(i);
                        mas[i] = JO.getString(title);
                        articles[i] = JO.getString(article);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.row, R.id.label, mas);

                    SportsList.setAdapter(adapter);
                    pg.setVisibility(View.INVISIBLE);
                    SportsList.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onPreExecute(){}
    }
}

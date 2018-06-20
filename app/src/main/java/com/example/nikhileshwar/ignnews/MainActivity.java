package com.example.nikhileshwar.ignnews;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   SQLiteDatabase sqLiteDatabase;
    ArrayAdapter arrayAdapter;
    ArrayList<String>urlList;
    ArrayList<String>titleList;
    public class DownloadTask extends AsyncTask<String, Void,String> {

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            String result = "";
            InputStream inputStream;
            InputStreamReader inputStreamReader;
            try {
                // itemList = new ArrayList<String>();
                url = new URL(params[0]);
                connection= (HttpURLConnection) url.openConnection();
                 inputStream= connection.getInputStream();
               inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1 ) {
                    char a = (char) data;


                    result += a;
                    data = inputStreamReader.read();
                }
                JSONObject jsonObject=new JSONObject(result);

               JSONArray jsonArray=new JSONArray(jsonObject.getString("articles"));
               // Log.i("articles",jsonObject.getString("articles"));
                sqLiteDatabase.execSQL("DELETE FROM articles");
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonPart=jsonArray.getJSONObject(i);

                    if(jsonPart.getString("title")!=""||jsonPart.getString("url")!="")
                    {
                       // String klaa="";
                        String sql="INSERT INTO articles (title,content)VALUES(? , ? )";
                        SQLiteStatement sqLiteStatement=sqLiteDatabase.compileStatement(sql);
                        sqLiteStatement.bindString(1,jsonPart.getString("title"));
                        //url=new URL(jsonPart.getString("url"));
                        //connection=(HttpURLConnection)url.openConnection();
                        //inputStream=connection.getInputStream();
                        //inputStreamReader=new InputStreamReader(inputStream);
                        //int data1=inputStreamReader.read();
                        /*while(data1!=-1)
                        {
                            char current=(char)data1;
                            klaa+=current;
                            data1=inputStreamReader.read();
                        }*/


                        sqLiteStatement.bindString(2,jsonPart.getString("url"));
                        sqLiteStatement.execute();
                    }
                       // Log.i("articles",jsonPart.getString("title"));
                    refreshList();

                }
                } catch (Exception e1) {
                e1.printStackTrace();
            }

            //sqLiteDatabase.execSQL("DELETE FROM articles");

               /* for(int i=0;i<10;++)
                {

                    String value=jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" +value+".json?print=pretty");
                    connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream1 = connection.getInputStream();
                    InputStreamReader inputStreamReader1 = new InputStreamReader(inputStream1);
                    int data1 = inputStreamReader1.read();
                    while (data1 != -1 ) {
                        char a = (char) data1;
                        info += a;
                        data1 = inputStreamReader1.read();
                    }

                    // Log.i("article info",info);
                    JSONObject jsonObject=new JSONObject(info);


                    // String value1=jsonArray.getString(i);
                    url = new URL(jsonObject.getString("url"));
                    connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream2 = connection.getInputStream();
                    InputStreamReader inputStreamReader2= new InputStreamReader(inputStream2);
                    int data2 = inputStreamReader2.read();
                    while (data2 != -1 ) {
                        char a = (char) data2;
                        info1 += a;
                        data2 = inputStreamReader2.read();
                    }


                    // sqLiteDatabase.execSQL("INSERT INTO articles (title,content,itemID)VALUES("+jsonObject.getString("title")+","+info1+","+jsonArray.get(i)+")");

                    //sqLiteStatement.bindString(3,jsonObject.get("title"));
                    sqLiteStatement.execute();


                }*/




            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            refreshList();
        }
    }
    public  void refreshList()
    {
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM articles",null);

        int titleID=cursor.getColumnIndex("title");
        int contentID=cursor.getColumnIndex("content");
        if(cursor.moveToFirst())
        {
            titleList.clear();
            urlList.clear();
            do {


                titleList.add(cursor.getString(titleID));
                urlList.add(cursor.getString(contentID));
            }while(cursor.moveToNext());

            arrayAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleList = new ArrayList<String>();
       urlList = new ArrayList<String>();
        ListView listView=(ListView)findViewById(R.id.listView);
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,titleList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                intent.putExtra("url",urlList.get(position));
                startActivity(intent);
            }
        });
       sqLiteDatabase=this.openOrCreateDatabase("Articles",MODE_PRIVATE,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS articles (title VARCHAR,content VARCHAR)");


        String html = "https://newsapi.org/v1/articles?source=ign&sortBy=latest&apiKey='your api key from NEWS API here'";
        DownloadTask task = new DownloadTask();

       refreshList();



       /* try {
            String result=task.execute(html).get();


            // Log.i("content", itemList.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }*/



    }
}

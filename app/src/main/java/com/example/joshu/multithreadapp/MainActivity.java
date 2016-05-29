package com.example.joshu.multithreadapp;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    // variables
    private String filename = "numbers.txt";
    private File file;
    private ArrayList list;
    private ArrayAdapter adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = new File(this.getFilesDir(), filename);
        Button create = (Button) findViewById(R.id.button);
        Button load = (Button) findViewById(R.id.button2);
        Button cache = (Button) findViewById(R.id.button3);

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList()));

        assert create != null;
        create.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // initiate create thread
                new createTask().execute();
            }
        });

        assert load != null;
        load.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // initiate load thread
                new loadTask().execute();
            }
        });

        assert cache != null;
        cache.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // clear the cache
                clearfile();
            }
        });
    }

    //create task AsyncTask method
    class createTask extends AsyncTask<Void,Integer,String> {
        ProgressBar progressBar;
        int count;

        @Override
        protected void onPreExecute() {
            adapter = (ArrayAdapter) lv.getAdapter();

            // set progress bar
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setMax(10);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
            count = 0;
        }

        @Override
        protected String doInBackground(Void... params) {

            //write to a file
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));

                for (int i = 1; i <= 10; i++) {
                    bw.write(i + "\n");
                    Thread.sleep(250);
                    publishProgress(0);
                    System.out.println("writing " + i + " to " + file.getName());
                }
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Wrote to file...";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            count++;
            progressBar.setProgress(count);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    //load task AsyncTask method
    class loadTask extends AsyncTask<Void,String,String> {
        ProgressBar progressBar;
        int count;

        @Override
        protected void onPreExecute() {
            adapter = (ArrayAdapter) lv.getAdapter();

            // set progress bar
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setMax(10);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
            count = 0;
        }

        @Override
        protected String doInBackground(Void... params) {

            // write to a file
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String out = "";

                while ((out = br.readLine()) != null) {
                    publishProgress(out);
                    Thread.sleep(250);
                    System.out.println("loading " + out + " from " + file.getName());
                }
                Toast.makeText(MainActivity.this, "output...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Read from file...";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            adapter.add(values[0]);
            count++;
            progressBar.setProgress(count);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    public void clearfile() {
        assert adapter != null;
        adapter.clear();
        Toast.makeText(this, "cleared cache...", Toast.LENGTH_SHORT).show();
    }
}

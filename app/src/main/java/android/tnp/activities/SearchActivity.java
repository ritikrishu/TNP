package android.tnp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.tnp.DAO.CreateDB;
import android.view.View;
import android.g38.tnp.R;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = (SearchView) findViewById(R.id.search);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                displayData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter!=null)
                   adapter.clear();
                displayData(newText);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayData(searchView.getQuery().toString());
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void displayData(String string) {
        CreateDB obj = new CreateDB(this);
        final Cursor cursor;
        if (string.equals(""))
            return;
        else
            cursor = obj.getSearchResult(string);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
//            Toast.makeText(MainActivity.this,"s"+result.getString(0)+result.getString(1),Toast.LENGTH_LONG).show();
            final ListView listView = (ListView) findViewById(R.id.lvData);

            ArrayList<String> textString = new ArrayList<String>();
            do {
                textString.add(cursor.getString(cursor.getColumnIndex(CreateDB.SUBJECT)));
            } while (cursor.moveToNext());
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, textString);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cursor.moveToFirst();
                    cursor.move(position);
                    Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
                    intent.putExtra("data", cursor.getString(cursor.getColumnIndex(CreateDB.DATA)));
                    intent.putExtra("subject", cursor.getString(cursor.getColumnIndex(CreateDB.SUBJECT)));
                    startActivity(intent);
                }
            });


        }

    }
}

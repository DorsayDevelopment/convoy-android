package com.dorsaydevelopment.convoy;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;


public class AddMemberActivity extends ActionBarActivity {

    private ParseQueryAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        listView = (ListView) findViewById(R.id.search_users_listview);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_member, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("Search", "String submitted > " + s);
                populateUsersList(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.i("Search", "String updated > " + s);
                populateUsersList(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void populateUsersList(final String queryString) {

        ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>() {
            @Override
            public ParseQuery<ParseUser> create() {
                ParseQuery<ParseUser> queryUsername = ParseUser.getQuery();
                queryUsername.whereEqualTo("username", queryString);
                return queryUsername;
            }
        };

        adapter = new ParseQueryAdapter<ParseUser>(this, factory) {
            @Override
            public View getItemView(ParseUser user, View view, ViewGroup parent) {
                if(view == null) {
                    view = View.inflate(getContext(), R.layout.user_list_item, null);
                }
                TextView name = (TextView) view.findViewById(R.id.text_view_user_name);
                if(user.getUsername().length() == 25)
                    name.setText(user.get("firstName") + " " + user.get("lastName"));
                else
                    name.setText(user.getUsername());

                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.dorsaydevelopment.convoy;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class AddMemberActivity extends ActionBarActivity {

    private ParseQueryAdapter adapter;
    private ListView listView;
    private String groupId;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("group_id");

        listView = (ListView) findViewById(R.id.search_users_listview);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_member, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

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
                queryUsername.whereStartsWith("username", queryString);
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
        listView.setClickable(true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseUser user = ((ParseUser) adapter.getItem(position));
                ParseQuery<Group> query = Group.getQuery();
                query.whereEqualTo("objectId", groupId);
                query.findInBackground(new FindCallback<Group>() {
                    @Override
                    public void done(List<Group> groups, ParseException e) {
                        if (e == null && groups.size() > 0) {
                            group = groups.get(0);
                            group.addMember(user);
                            group.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Toast.makeText(getApplicationContext(), user.getUsername() + " added to group", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else if (e != null) {
                            Log.e("Group", "Error getting group info > " + e.toString());
                            finish();
                        } else {
                            Log.e("Group", "No group found that matches id " + groupId);
                            finish();
                        }
                        // TODO: Hide progress spinner
                    }
                });
            }
        });
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

package com.dorsaydevelopment.convoy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    ParseUser currentUser;

    private SwipeRefreshLayout swipeLayout;
    private Handler handler;
    private ParseQueryAdapter adapter;
    private ListView listView;

    private boolean isRefreshing;

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentUser = ParseUser.getCurrentUser();
        setTitle("My Convoys");
        handler = new Handler();

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeLayout.setSoundEffectsEnabled(true);
        swipeLayout.setColorSchemeColors(Color.parseColor("#FF9800"), Color.GREEN, Color.YELLOW);

        listView = (ListView) findViewById(R.id.groups_list);

        context = this;
    }

    private void populateList() {
        isRefreshing = true;

        ParseQueryAdapter.QueryFactory<Group> factory = new ParseQueryAdapter.QueryFactory<Group>() {
            @Override
            public ParseQuery<Group> create() {
                ParseQuery<Group> query = Group.getQuery();
                query.whereEqualTo("members", ParseUser.getCurrentUser());
                return query;
            }
        };

        adapter = new ParseQueryAdapter<Group>(this, factory) {
            @Override
            public View getItemView(Group group, View view, ViewGroup parent) {
                if(view == null) {
                    view = View.inflate(getContext(), R.layout.group_list_item, null);
                }
                TextView groupName = (TextView) findViewById(R.id.group_item_name);
                groupName.setText(group.getGroupName());
                return view;
            }
        };

        isRefreshing = false;

        listView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        populateList();
        handler.post(refreshing);
    }

    private final Runnable refreshing = new Runnable(){
        public void run(){
            try {
                if(isRefreshing){
                    // re run the verification after 1 second
                    handler.postDelayed(this, 1000);
                } else {
                    // stop the animation after the data is fully loaded
                    adapter.notifyDataSetChanged();
                    swipeLayout.setRefreshing(false);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, DispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if(id == R.id.action_new_group) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Group");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                    intent.putExtra("group_name", input.getText().toString());
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;


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
                query.include("members");
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
                TextView groupName = (TextView) view.findViewById(R.id.group_item_name);
                groupName.setText(group.getGroupName());
                return view;
            }
        };

        isRefreshing = false;

        listView.setClickable(true);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Main", "Group: " + ((Group) adapter.getItem(position)).getGroupName() + " clicked");
                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                intent.putExtra("object_id", ((Group) adapter.getItem(position)).getObjectId());
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = ((Group) adapter.getItem(position));
                ParseUser leader = group.getLeader();
                if(currentUser.getObjectId().equals(leader.getObjectId())) {
                    Log.i("Main", "Current user IS the leader of the group that was long pressed");
                    group.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            populateList();
                        }
                    });
                } else {
                    Log.i("Main", "Current user is NOT the leader of the group that was long pressed");
                    group.removeMember(currentUser);
                }
                return false;
            }
        });
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
        populateList();

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
                public void onClick(final DialogInterface dialog, int which) {
                    final String groupName = input.getText().toString();
                    final Group group = new Group();
                    group.setGroupName(groupName);
                    group.setLeader(currentUser);
                    group.addMember(currentUser);
                    group.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                                intent.putExtra("object_id", group.getObjectId());
                                startActivity(intent);
                            } else {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), "Error creating group", Toast.LENGTH_SHORT).show();
                                Log.e("CreateGroup", "Error creating group > " + e.toString());
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

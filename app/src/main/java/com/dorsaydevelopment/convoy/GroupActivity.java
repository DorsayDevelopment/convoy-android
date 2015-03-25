package com.dorsaydevelopment.convoy;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class GroupActivity extends ActionBarActivity {

    private Group group;
    private Spinner leaderSpinner;
    private ListView membersListView;
    private Switch activateGroupSwitch;
    private ParseUser currentUser;
    private Button addMembersBtn;
    private String groupId;
    private SharedPreferences preferences;
    private ArrayAdapter<ParseUser> membersAdapter;
    private NotificationManager mNotificationManager;

    private int ACTIVE_GROUP_NOTIFICATION_ID = 100;
    private String PACKAGE_NAME = "com.dorsaydevelopment.convoy";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Lifecycle", "onCreate");
        setContentView(R.layout.activity_group);
        setTitle("");
        preferences = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);

        currentUser = ParseUser.getCurrentUser();

        // Define fields in layout
        leaderSpinner = (Spinner) findViewById(R.id.group_leader_spinner);
        membersListView = (ListView) findViewById(R.id.members_list_view);
        addMembersBtn = (Button) findViewById(R.id.group_add_members_btn);
        activateGroupSwitch = (Switch) findViewById(R.id.activate_group_switch);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        addMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMemberActivity.class);
                intent.putExtra("group_id", groupId);
                startActivity(intent);
            }
        });

        activateGroupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // TODO: Improve request efficiency of activating and deactivating groups
                    updateActiveGroup("activate");
                } else {
                    updateActiveGroup("deactivate");
                }
            }
        });

        // TODO: Show progress spinner

        // Get the group information from the database
        Intent intent = getIntent();
        groupId = intent.getStringExtra("object_id");

        if(groupId == null)
            groupId = preferences.getString(PACKAGE_NAME + ".groupId", "");
        else
            preferences.edit().putString(PACKAGE_NAME + ".groupId", groupId).apply();

        ParseQuery<Group> query = Group.getQuery();
        query.whereEqualTo("objectId", groupId);
        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e == null && groups.size() > 0) {
                    group = groups.get(0);
                    setTitle(group.getGroupName());
                    populateFields();
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

    private void updateActiveGroup(String mode) {
        if (mode.equals("activate")) {
            // Deactivate previously active group
            String lastActiveGroupId = preferences.getString(PACKAGE_NAME + ".activeGroup", "");
            ParseQuery<Group> query = Group.getQuery();
            query.whereEqualTo("objectId", lastActiveGroupId);
            query.findInBackground(new FindCallback<Group>() {
                @Override
                public void done(List<Group> groups, ParseException e) {
                    if(e == null && groups.size() > 0) {
                        Log.i("GroupActivate", "Deactivating previously active group");
                        Group lastActiveGroup = groups.get(0);
                        lastActiveGroup.removeActiveMember(currentUser);
                        lastActiveGroup.saveInBackground();
                    } else if(groups.size() == 0) {
                        Log.i("GroupActivate", "No previously active group to deactivate");
                    } else {
                        Log.e("GroupActivate", "Error deactivating currently active group");
                    }
                }
            });
            // Activate current group
            preferences.edit().putString(PACKAGE_NAME + ".activeGroup", group.getObjectId()).apply();
            group.addActiveMember(currentUser);
            group.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Group activated", Toast.LENGTH_SHORT).show();
                        notifyActiveGroup();
                    } else {
                        Log.e("ActivateGroup", e.toString());
                    }
                }
            });
        } else {
            // Update the active group in the shared preferences to save on requests
            preferences.edit().putString(PACKAGE_NAME + ".activeGroup", "").apply();
            // Remove the user from the active members list in the current group
            group.removeActiveMember(currentUser);
            group.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getApplicationContext(), "Group deactivated", Toast.LENGTH_SHORT).show();
                    mNotificationManager.cancel(ACTIVE_GROUP_NOTIFICATION_ID);
                }
            });
        }
    }

    private void notifyActiveGroup() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Convoy group is active")
                        .setContentText(group.getGroupName())
                        .setOngoing(true);
        // TODO: Change this line to use the map activity. Right now that doesn't exist
        Intent resultIntent = new Intent(this, GroupActivity.class);
        resultIntent.putExtra("object_id", group.getObjectId());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(GroupActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(ACTIVE_GROUP_NOTIFICATION_ID, mBuilder.build());
    }

    private void populateFields() {
        final GroupLeaderAdapter adapter = new GroupLeaderAdapter(this, android.R.layout.simple_spinner_item, group.getMembers());
        leaderSpinner.setAdapter(adapter);

        leaderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group.setLeader(adapter.getItem(position));
                group.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
//                        Toast.makeText(getApplicationContext(), "Group leader updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // Group switch state depends on if the current group is the active group
        if(group.getObjectId().equals(preferences.getString(PACKAGE_NAME + ".activeGroup", ""))) {
            activateGroupSwitch.setChecked(true);
        }

        membersAdapter = new ArrayAdapter<ParseUser>(this, R.layout.user_list_item, group.getMembers()) {
            @Override
            public View getView(int position, View v, ViewGroup parent) {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.user_list_item, null);
                }
                ParseUser user = group.getMembers().get(position);
                TextView nameView = (TextView) v.findViewById(R.id.text_view_user_name);
                if(user == group.getLeader()) {
//                    v.setBackgroundColor(Color.YELLOW);
                    ((ImageView)v.findViewById(R.id.is_group_leader_image)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_leader));
                }

                if(user.getUsername().length() == 25) {
                    nameView.setText(user.get("firstName") + " " + user.get("lastName"));
                } else {
                    nameView.setText(user.getUsername());
                }
                return v;
            }
        };
        membersListView.setAdapter(membersAdapter);
        registerForContextMenu(membersListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.members_list_view) {
            String[] menuItems;
            menuItems = getResources().getStringArray(R.array.group_member_kick);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final ParseUser user = ((ParseUser) membersAdapter.getItem(info.position));

        if(menuItemIndex == 0) { // Delete/remove
            group.removeMember(user);
            group.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), user.getUsername() + " kicked from group", Toast.LENGTH_SHORT).show();
                        membersAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Main", e.toString());
                    }
                }
            });
        }
        membersAdapter.notifyDataSetChanged();

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
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
            return true;
        } else if(id == R.id.action_edit_group_name) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Group Name");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    group.setGroupName(input.getText().toString());
                    group.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Toast.makeText(getApplicationContext(), "Group name updated", Toast.LENGTH_SHORT).show();
                                setTitle(group.getGroupName());
                            } else {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), "Error updating group name", Toast.LENGTH_SHORT).show();
                                Log.e("CreateGroup", "Error updating group name > " + e.toString());
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
        }

        return super.onOptionsItemSelected(item);
    }

    private class GroupLeaderAdapter extends ArrayAdapter<ParseUser> {
        private List<ParseUser> list;
        public GroupLeaderAdapter(Context context, int resourceId, List<ParseUser> list) {
            super(context, resourceId, list);
            this.list = list;
        }
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_spinner_item, null);
            }
            ParseUser user = list.get(position);
            TextView nameView = (TextView) view.findViewById(android.R.id.text1);

            if(user.getUsername().length() == 25) {
                nameView.setText(user.get("firstName") + " " + user.get("lastName"));
            } else {
                nameView.setText(user.getUsername());
            }
            return view;
        }
    }
}

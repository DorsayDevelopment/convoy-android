package com.dorsaydevelopment.convoy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class GroupActivity extends ActionBarActivity {

    private Group group;
    private EditText groupNameField;
    private Spinner leaderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // Define fields in layout
        groupNameField = (EditText) findViewById(R.id.group_name_text);
        leaderSpinner = (Spinner) findViewById(R.id.group_leader_spinner);

        // TODO: Show progress spinner

        // Get the group information from the database
        Intent intent = getIntent();
        final String objectId = intent.getStringExtra("object_id");
        ParseQuery<Group> query = Group.getQuery();
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if(e == null && groups.size() > 0) {
                    group = groups.get(0);
                    populateFields();
                } else if(e != null) {
                    Log.e("Group", "Error getting group info > " + e.toString());
                    finish();
                } else {
                    Log.e("Group", "No group found that matches id " + objectId);
                    finish();
                }
                // TODO: Hide progress spinner
            }
        });

        // Define listeners
        groupNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void populateFields() {
        groupNameField.setText(group.getGroupName());

        leaderSpinner.setAdapter(new GroupLeaderAdapter(this, android.R.layout.simple_spinner_item, group.getMembers()));

        leaderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Change leader in database
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, null);
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

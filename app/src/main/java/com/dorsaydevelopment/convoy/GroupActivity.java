package com.dorsaydevelopment.convoy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;


public class GroupActivity extends ActionBarActivity {

    private Group group;
    private EditText groupNameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // TODO: Show progress spinner

        // Get the group information from the database
        Intent intent = getIntent();
        final String objectId = intent.getStringExtra("object_id");
        // TODO: Create a group object from query
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

        // Define fields in layout
        groupNameField = (EditText) findViewById(R.id.group_name_text);

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
}

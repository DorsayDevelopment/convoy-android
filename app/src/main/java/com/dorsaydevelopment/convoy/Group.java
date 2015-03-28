package com.dorsaydevelopment.convoy;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brycen on 2015-03-18.
 */

@ParseClassName("Groups")
public class Group extends ParseObject {

    // Parse db class and column names
    private String ACTIVE_MEMBERS = "activeMembers";
    private String MEMBERS = "members";
    private String GROUP_NAME = "groupName";
    private String LEADER = "leader";
    private String DESTINATION = "destination";
    private String PIT_STOPS = "pitStops";


    public String getGroupName() {
        return getString(GROUP_NAME);
    }

    public void setGroupName(String groupName) {
        put(GROUP_NAME, groupName);
    }

    public ParseUser getLeader() {
        return getParseUser(LEADER);
    }

    public void setLeader(ParseUser value) {
        put(LEADER, value);
    }

    public ParseGeoPoint getDestination() {
        return getParseGeoPoint(DESTINATION);
    }

    public void setDestination(ParseGeoPoint value) {
        put(DESTINATION, value);
    }

    public List<ParseUser> getMembers() {
        return getList(MEMBERS);
    }

    public void addMember(ParseUser member) {
        List<ParseUser> list = new ArrayList<ParseUser>();
        list.add(member);
        addAll(MEMBERS, list);
    }

    public void removeMember(ParseUser member) {
        List<ParseUser> list = new ArrayList<ParseUser>();
        list.add(member);
        removeAll(MEMBERS, list);
    }

    public List<ParseUser> getActiveMembers() {
        return getList(ACTIVE_MEMBERS);
    }

    public void addActiveMember(ParseUser member) {
        List<ParseUser> list = new ArrayList<ParseUser>();
        list.add(member);
        addAll(ACTIVE_MEMBERS, list);
    }

    public void removeActiveMember(ParseUser member) {
        List<ParseUser> list = new ArrayList<ParseUser>();
        list.add(member);
        removeAll(ACTIVE_MEMBERS, list);
    }

    public List<ParseGeoPoint> getPitStops() {
        // TODO: Get all pitstop objects from the pitstop table
        return getList(PIT_STOPS);
    }

    public static ParseQuery<Group> getQuery() {
        return ParseQuery.getQuery(Group.class);
    }
}

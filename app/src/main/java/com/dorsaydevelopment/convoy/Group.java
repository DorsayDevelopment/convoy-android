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

    public String getGroupName() {
        return getString("groupName");
    }

    public void setGroupName(String groupName) {
        put("groupName", groupName);
    }

    public ParseUser getLeader() {
        return getParseUser("leader");
    }

    public void setLeader(ParseUser value) {
        put("leader", value);
    }

    public ParseGeoPoint getDestination() {
        return getParseGeoPoint("destination");
    }

    public void setDestination(ParseGeoPoint value) {
        put("destination", value);
    }

    public List<ParseUser> getMembers() {
        return getList("members");
    }

    public void addMember(ParseUser member) {
        List<ParseUser> list = new ArrayList<ParseUser>();
        list.add(member);
        put("members", list);
    }

    public List<ParseGeoPoint> getPitStops() {
        return getList("pitStops");
    }

    public void setPitStops(List<ParseGeoPoint> pitStops) {
        put("pitStops", pitStops);
    }

    public static ParseQuery<Group> getQuery() {
        return ParseQuery.getQuery(Group.class);
    }
}

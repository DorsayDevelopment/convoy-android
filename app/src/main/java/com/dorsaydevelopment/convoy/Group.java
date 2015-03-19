package com.dorsaydevelopment.convoy;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Brycen on 2015-03-18.
 */

@ParseClassName("Groups")
public class Group extends ParseObject {

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

    public void setMembers(List<Group> members) {
        put("members", members);
    }

    public static ParseQuery<Group> getQuery() {
        return ParseQuery.getQuery(Group.class);
    }
}

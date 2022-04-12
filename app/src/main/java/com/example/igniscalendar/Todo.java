package com.example.igniscalendar;

import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Todo {

    public String todoName;
    public String todoDescription;
    public boolean isDone;
    public Date todoDate;


    public Todo() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Todo(String todoName, String todoDescription, Date todoDate) {
        this.todoName = todoName;
        this.todoDescription = todoDescription;
        this.todoDate = todoDate;
    }

}

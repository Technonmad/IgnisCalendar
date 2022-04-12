package com.example.igniscalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceTodos;
    private List<Todo> todos = new ArrayList<Todo>();
    private List<String> keys;
    private Map<String, List<Todo>> todoDates = new HashMap<String, List<Todo>>();
    private Map<String, List<String>> todoDateKeys = new HashMap<String, List<String>>();

    public interface  DataStatus{
        void DataIsLoaded(List<Todo> todos, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseDatabaseHelper(String username){
        mDatabase = FirebaseDatabase.getInstance("https://ignis-project-default-rtdb.europe-west1.firebasedatabase.app/");
        mReferenceTodos = mDatabase.getReference("todos").child(username);
    }

    public void readTodos(final DataStatus dataStatus){
        mReferenceTodos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                todoDates.clear();
                todos.clear();
                keys = new ArrayList<>();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Todo todo = keyNode.getValue(Todo.class);
                    if(todoDates.get(df.format(todo.todoDate))==null)
                    {
                        todoDates.put(df.format(todo.todoDate), new ArrayList<Todo>() );
                        todoDateKeys.put(df.format(todo.todoDate), new ArrayList<String>() );
                    }

                    todoDates.get(df.format(todo.todoDate)).add(todo);
                    todoDateKeys.get(df.format(todo.todoDate)).add(keyNode.getKey());
                    todos.add(todo);
                }
                //dataStatus.DataIsLoaded(todoDates.get("2021-11-28"), keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public List<Todo> getToDoByDate(String date){
        if(todoDates.get(date)==null) todoDates.put(date,new ArrayList<Todo>());
        return todoDates.get(date);
    }

    public List<String> getTodoKeyByDate(String date){
        if(todoDateKeys.get(date)==null) todoDateKeys.put(date,new ArrayList<String>());
        return todoDateKeys.get(date);
    }

    public List<Todo> getAllTodos(){
        return todos;
    }

    public Map<String, List<Todo>> getTodosMap() { return todoDates; }

    public List<String> getKeys(){
        return keys;
    }

    public void addTodo(Todo todo, final DataStatus dataStatus){
        String key = mReferenceTodos.push().getKey();
        mReferenceTodos.child(key).setValue(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataIsInserted();
            }
        });
    }

    public void updateTodo(String key, Todo todo, final DataStatus dataStatus){
        mReferenceTodos.child(key).setValue(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataIsUpdated();
            }
        });
    }

    public void deleteTodo(String key, final DataStatus dataStatus){
        mReferenceTodos.child(key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataIsDeleted();
            }
        });
    }
}

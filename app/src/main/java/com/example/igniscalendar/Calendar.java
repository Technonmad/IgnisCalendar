package com.example.igniscalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Calendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendar extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Todo> cases;
    private ArrayList<String> allCases;
    private ArrayAdapter<String> caseAdapter;
    private ListView listView;
    private Button button;
    private CalendarView calendarView;
    private String curDate;
    private List<String> keysArray;

    private DatabaseReference mDatabase;
    private String username = "Pepe";
    FirebaseDatabaseHelper dbHelper =  new FirebaseDatabaseHelper(username);

    public Calendar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Calendar.
     */
    // TODO: Rename and change types and number of parameters
    public static Calendar newInstance(String param1, String param2) {
        Calendar fragment = new Calendar();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }


    //ОБРАБОТЧИКИ ВСЕ ТУТ!!!!!!!!!!!!!!!!
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Задержка (не всегда плохо) с последующим извлечением дел на текущую дату
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataOnCurrentDate(curDate);
            }
        }, 1200);

        listView = myView.findViewById(R.id.list_cases);
        button = myView.findViewById(R.id.saveButton);
        calendarView = myView.findViewById(R.id.calendar_view);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        curDate = sdf.format(new Date(calendarView.getDate()));
        List<String> case_names = new ArrayList<String>();


        dbHelper.readTodos(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Todo> todos, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });



        //ДОБАВЛЕНИЕ НОВОГО ДЕЛА ПО НАЖАТИЮ КНОПКИ
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCase(myView);
            }
        });

        //ВЫЗОВ ОКНА С УДАЛЕНИЕМ ПРИ ЗАЖАТИИ НА ДЕЛЕ
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteCase(myView, i);
                return true;
            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                getDataOnSelectedDate(i,i1,i2);
            }
        });

        caseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, case_names);
        listView.setAdapter(caseAdapter);

        return myView;
    }


    //ДОБАВЛЯЕТ ДЕЛО ИЗ СТРОКИ
    private void addCase(View view) {

        EditText input_case = view.findViewById(R.id.edit_text_string);
        String caseText = input_case.getText().toString();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Todo todo = new Todo(caseText,"", date);

        if (!(caseText.equals(""))){
            dbHelper.addTodo(todo, new FirebaseDatabaseHelper.DataStatus() {
                @Override
                public void DataIsLoaded(List<Todo> todos, List<String> keys) {

                }

                @Override
                public void DataIsInserted() {
                    getDataOnCurrentDate(curDate);
                }

                @Override
                public void DataIsUpdated() {

                }

                @Override
                public void DataIsDeleted() {

                }
            });

            input_case.setText("");

        }
        else {
            Toast.makeText(getContext(), "Кажется вы не описали ваше дело!", Toast.LENGTH_LONG).show();
        }

    }

    private void deleteCase(View view, int i){
        final int which_item = i;

        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_delete).setTitle("Удаление")
                .setMessage("Вы действительно хотите удалить это дело?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dbHelper.getKeys().forEach(names ->{
                            Log.d("BRAT", names);
                        });

                        List<String> keys = new ArrayList<>();
                        dbHelper.getKeys().forEach(names ->{
                            keys.add(names);
                        });

                        dbHelper.deleteTodo(Integer.toString(which_item), new FirebaseDatabaseHelper.DataStatus() {
                            @Override
                            public void DataIsLoaded(List<Todo> todos, List<String> keys) {

                            }

                            @Override
                            public void DataIsInserted() {

                            }

                            @Override
                            public void DataIsUpdated() {

                            }

                            @Override
                            public void DataIsDeleted() {

                            }
                        });
                        //cases.remove(which_item);
                        dbHelper.deleteTodo(keysArray.get(which_item), new FirebaseDatabaseHelper.DataStatus() {
                            @Override
                            public void DataIsLoaded(List<Todo> todos, List<String> keys) {

                            }

                            @Override
                            public void DataIsInserted() {

                            }

                            @Override
                            public void DataIsUpdated() {

                            }

                            @Override
                            public void DataIsDeleted() {

                            }
                        });
                        // Задержка (не всегда плохо) с последующим извлечением дел на текущую дату
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getDataOnCurrentDate(curDate);
                            }
                        }, 1200);
                        caseAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void getDataOnSelectedDate(int i, int i1, int i2){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s1, s2;
        if(i1 < 9)
            s1 = "0"+String.valueOf(i1+1);
        else
            s1 = String.valueOf(i1+1);
        if(i2 < 10)
            s2 = "0"+String.valueOf(i2);
        else
            s2 = String.valueOf(i2);
        curDate = String.valueOf(i)+"-"+s1+"-"+s2;
        cases = dbHelper.getToDoByDate(String.valueOf(i)+"-"+s1+"-"+s2);
        keysArray = dbHelper.getTodoKeyByDate(String.valueOf(i)+"-"+s1+"-"+s2);
        List<String> case_names = new ArrayList<String>();
        cases.forEach(name -> {
            case_names.add(name.todoName);
        });

        caseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, case_names);
        listView.setAdapter(caseAdapter);
    }

    private void getDataOnCurrentDate(String curDate){

        keysArray = dbHelper.getTodoKeyByDate(curDate);

        cases = dbHelper.getToDoByDate(curDate);
        List<String> case_names = new ArrayList<String>();
        cases.forEach(name -> {
            case_names.add(name.todoName);
        });

        caseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, case_names);
        listView.setAdapter(caseAdapter);
    }


}
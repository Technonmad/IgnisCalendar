package com.example.igniscalendar;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link all_cases#newInstance} factory method to
 * create an instance of this fragment.
 */
public class all_cases extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView lstView;
    private List<Todo> cases;
    private ArrayAdapter<String> caseAdapter;
    private Button updateBtn;
    private String username = "Pepe";
    FirebaseDatabaseHelper dbHelper =  new FirebaseDatabaseHelper(username);

    public all_cases() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment all_cases.
     */
    // TODO: Rename and change types and number of parameters
    public static all_cases newInstance(String param1, String param2) {
        all_cases fragment = new all_cases();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_all_cases, container, false);

        // Задержка (не всегда плохо) с последующим извлечением дел на текущую дату
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAllData();
            }
        }, 1200);

        updateBtn = myView.findViewById(R.id.updateButton);
        lstView = myView.findViewById(R.id.allCasesList);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getAllData();
            }
        });

        return myView;
    }

    public void getAllData(){

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cases = dbHelper.getAllTodos();
        List<String> case_names = new ArrayList<String>();
        cases.forEach(name -> {
            case_names.add(name.todoName + "\n"
                    + sdf.format(name.todoDate));
        });


        caseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, case_names);
        lstView.setAdapter(caseAdapter);
    }
}
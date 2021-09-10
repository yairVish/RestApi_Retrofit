package com.proj.theretrofitwebservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.proj.theretrofitwebservice.retrofit.Api;
import com.proj.theretrofitwebservice.retrofit.MyRetrofit;
import com.proj.theretrofitwebservice.room.NoteViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MyAdapter adapter;
    private List<Course> courses = new ArrayList<>();
    private NoteViewModel noteViewModel;
    private Api api;
    private FloatingActionButton addButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int idToPut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.add_button);
        recyclerView = findViewById(R.id.recyclerview);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(MainActivity.this, courses);
        recyclerView.setAdapter(adapter);

        api = MyRetrofit.getClient("http://192.168.5.59:3000/api/").create(Api.class);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction){
                int iDelete = courses.get(viewHolder.getPosition()).getId();
                api.deleteCourse(iDelete).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("TAG", "response: "+response.code());
                        Log.d("TAG", "onDelete: "+courses);
                        if(response.code() == 200)
                            noteViewModel.deleteCourse(courses.get(viewHolder.getPosition()));
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);

        getCoursesApi();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCoursesApi();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        noteViewModel.getAllCourses().observe(this, new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                MainActivity.this.courses = courses;
                Log.d("TAG", "coursesOn: "+courses);
                adapter.setCourses(courses);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog("new course", "add", "");
            }
        });

        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                idToPut = position;
                setDialog("edit course", "edit", courses.get(position).getName());
            }
        });
    }

    private void getCoursesApi() {
        api.getCourses().enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                Log.d("TAG", "response: "+response.body());
                adapter.setCourses(response.body());
                noteViewModel.deleteAllCourses();
                for(Course course: response.body()){
                    Log.d("TAG", "course in loop: "+course);
                    noteViewModel.insertCourse(course);
                }
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {

            }
        });
    }

    private void setDialog(String text, String btnText, String name) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
        EditText editText =mView.findViewById(R.id.editText);
        Button button = mView.findViewById(R.id.button);
        TextView textView = mView.findViewById(R.id.text);
        button.setText(btnText);
        textView.setText(text);
        editText.setText(name);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sText = editText.getText().toString();
                if(!sText.trim().equals("")){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("name", sText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(text.equals("new course")) {
                        Course course = new Course(courses.get(courses.size()-1).getId()+1, sText);
                        api.createCourse(course).enqueue(new Callback<Course>() {
                            @Override
                            public void onResponse(Call<Course> call, Response<Course> response) {
                                Log.d("TAG", "response.code: " + response.code());
                                Log.d("TAG", "response.body: " + response.body());
                                if (response.code() == 200)
                                    noteViewModel.insertCourse(course);
                            }

                            @Override
                            public void onFailure(Call<Course> call, Throwable t) {

                            }
                        });
                    }else{
                        Course course = courses.get(idToPut);
                        idToPut = course.getId();
                        course.setName(sText);
                        api.putCourse(idToPut, course).enqueue(new Callback<Course>() {
                            @Override
                            public void onResponse(Call<Course> call, Response<Course> response) {
                                Log.d("TAG", "responsePut: "+response.code());
                                if (response.code() == 200)
                                    noteViewModel.updateCourse(course);
                            }

                            @Override
                            public void onFailure(Call<Course> call, Throwable t) {

                            }
                        });
                    }

                    dialog.dismiss();
                }
            }
        });
    }
}
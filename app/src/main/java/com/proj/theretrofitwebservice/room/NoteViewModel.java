package com.proj.theretrofitwebservice.room;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.proj.theretrofitwebservice.Course;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
    }

    public void insertCourse(Course note) {
        repository.insertCourse(note);
    }

    public void deleteCourse(Course note) {
        repository.deleteCourse(note);
    }

    public void deleteAllCourses() {
        repository.deleteAllCourses();
    }

    public void updateCourse(Course note) {
        Log.d("TAG", "update pls: ");
        repository.updateCourse(note);
    }

    public LiveData<List<Course>> getAllCourses(){
        return repository.getAllCourses();
    }
}

package com.proj.theretrofitwebservice.room;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.proj.theretrofitwebservice.Course;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;

    public NoteRepository(Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
    }

    public void insertCourse(Course note) {
        new InsertCourseAsyncTask(noteDao).execute(note);
    }

    public void deleteCourse(Course note) {
        new DeleteCourseAsyncTask(noteDao).execute(note);
    }

    public void updateCourse(Course note){
        Log.d("TAG", "update n: ");
        new UpdateCourseAsyncTask(noteDao).execute(note);
    }

    public void deleteAllCourses() {
        new DeleteAllCoursesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Course>> getAllCourses(){
        return noteDao.getAllCourses();
    }



    private static class InsertCourseAsyncTask extends AsyncTask<Course, Void, Void> {
        private NoteDao noteDao;

        private InsertCourseAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Course... notes) {
            noteDao.insertCourse(notes[0]);
            return null;
        }
    }

    private static class DeleteCourseAsyncTask extends AsyncTask<Course, Void, Void> {
        private NoteDao noteDao;
        private DeleteCourseAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Course... notes) {
            noteDao.deleteCourse(notes[0]);
            return null;
        }
    }

    private static class UpdateCourseAsyncTask extends AsyncTask<Course, Void, Void> {
        private NoteDao noteDao;
        private UpdateCourseAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Course... notes) {
            Log.d("TAG", "update?: ");
            noteDao.updateCourse(notes[0]);
            return null;
        }
    }

    private static class DeleteAllCoursesAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        private DeleteAllCoursesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllCourses();
            return null;
        }
    }
}

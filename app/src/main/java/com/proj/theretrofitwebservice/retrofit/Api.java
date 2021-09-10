package com.proj.theretrofitwebservice.retrofit;

import com.proj.theretrofitwebservice.Course;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Api {
    @GET("courses")
    Call<List<Course>> getCourses();

    @DELETE("courses/{id}")
    Call<Void> deleteCourse(@Path("id") int id);

    @POST("courses")
    Call<Course> createCourse(@Body Course course);

    @PUT("courses/{id}")
    Call<Course> putCourse(@Path("id") int id,@Body Course course);
}

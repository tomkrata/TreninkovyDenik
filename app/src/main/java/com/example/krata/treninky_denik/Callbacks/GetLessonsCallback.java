package com.example.krata.treninky_denik.Callbacks;

import com.example.krata.treninky_denik.Lesson;

import java.util.ArrayList;

public interface GetLessonsCallback {
    public abstract void done(ArrayList<Lesson> lessons);
}

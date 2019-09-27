package com.example.r_clazz.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Student_Service extends Service {
    public Student_Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

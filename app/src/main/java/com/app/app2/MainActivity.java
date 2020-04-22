package com.app.app2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    String APP_NAME = "com.app.app1";
    EditText package_name ;
    Button b1;
    TextView tv;
    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        package_name = findViewById(R.id.packageName);
        b1 = findViewById(R.id.button1);
        tv = findViewById(R.id.tv);
        b1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        APP_NAME = package_name.getText().toString();
                        text = "";
                        Log.d("HARSHIT", "packageName: " + APP_NAME);
                        Context ctx = getPackageContext(getApplicationContext(), APP_NAME);
                        if (ctx == null) return;
                        ClassLoader classLoader = ctx.getClassLoader();

                        //List<String> activities;
                        DexFile dexFile = null;
                        try {
                            dexFile = new DexFile(ctx.getPackageCodePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Enumeration<String> classNames = dexFile.entries();
                        while (classNames.hasMoreElements()) {
                            String className = classNames.nextElement();
                            //Log.d("HARSHIT", "ALL: " + className);
                            if(className.startsWith(APP_NAME) && (!className.contains("$"))){
                                Log.d("HARSHIT", "Class: " + className);
                                text= text.concat("Class: " + className + "\n");
                                try {
                                    Class<?> testClass = classLoader.loadClass(className);
                                    for(Method m: testClass.getDeclaredMethods()) {
                                        Log.d("HARSHIT", String.valueOf(m));
                                        text= text.concat("Function: " + m + "\n");
                                        if(String.valueOf(m).equals("public int com.app.app1.MainActivity.sum(int,int)")){
                                            Object temp = testClass.newInstance();
                                            int add = (int) m.invoke(temp, 3,5);
                                            Log.d("HARSHIT", "sum(3,5): " + add);
                                            text= text.concat("sum(3,5): " + add + "\n");
                                        }
                                    }
                                    text= text.concat("\n");
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        Log.d("HARSHIT", "text = "+ text);
                        tv.setText(text);

                    }
                }
        );

/*
        PackageManager pm = getApplicationContext().getPackageManager();
        Intent queryIntent = new Intent(Intent.ACTION_MAIN);
        //queryIntent.addCategory("com.app.app1.TEST");
        List<ResolveInfo> infos = pm.queryIntentActivities(queryIntent, 0);
        final List<ApplicationInfo> apps = new ArrayList<>();
        for (ResolveInfo resolveInfo : infos) {
            if (resolveInfo.activityInfo != null) {
                apps.add(resolveInfo.activityInfo.applicationInfo);

            }
            //Log.d("HARSHIT", resolveInfo.toString());
        }

        ApplicationInfo appInfo = apps.get(0);
        // Package name
        String packageName = appInfo.packageName;
        // SDK version
        int targetSdk = appInfo.targetSdkVersion;
        // Application name
        String appName = appInfo.loadLabel(pm).toString();
*/
    }

    public static Context getPackageContext(Context context, String packageName) {
        try {
            return context.getApplicationContext().createPackageContext(packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


}

package com.exercise.sharing;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Philip
 * @since 2017-12-06
 */

public class Test implements Serializable{


    Context mContext;
    String content;
    int i;

    public Test(int i) {
        this.i = i;
    }

    private Test(String s) {
        content = s;
    }

    public Test(int i, Context context) {
        this.i = i;
        mContext = context.getApplicationContext();
    }

    @Override
    public String toString() {
        return "-Test("+i+")";
    }

    private static final String val = "this is a const";

    public static class Builder {
        String content;

        public Builder addName(String name) {
            content += name;
            return this;
        }

        public Builder addAge(int age) {
            content += " Age: " + age + " ";
            return this;
        }

        public Test build() {
            return new Test(content);
        }
    }



    public String method() {
        String h = "local ";
        String newStr = h + val;

        if (newStr != null) {
            newStr += "...";
            throw new NullPointerException(newStr);
        }

        return newStr;
    }

    public static class InnerTest {

        public void method2() {
//            int innerInt = i;
//            method();
        }

    }

}

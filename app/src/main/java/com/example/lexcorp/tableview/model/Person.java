package com.example.lexcorp.tableview.model;

import android.support.annotation.NonNull;

public class Person {
    private String name;
    private String lastName;
    private String age;

    public Person(String name, String lastName, String age) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getLastName() {
        return lastName;
    }

    @SuppressWarnings("unused")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @NonNull
    @Override
    public String toString() {
        return "NAME = " +
                name +
                ";\nLAST NAME = " +
                lastName +
                ";\nAGE = " +
                age;
    }
}

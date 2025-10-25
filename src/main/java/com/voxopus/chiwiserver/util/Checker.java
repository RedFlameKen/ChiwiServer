package com.voxopus.chiwiserver.util;

public class Checker<T> {

    private T data;
    private String message;

    public Checker(T data){
        this.data = data;
        this.message = "";
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public T get(){
        return data;
    }

    public boolean isOk(){
        return data != null;
    }

    public static <T> Checker<T> fail(String message){
        Checker<T> checker = new Checker<>(null);
        checker.setMessage(message);
        return checker;
    }

    public static <T> Checker<T> ok(T data){
        return new Checker<T>(data);
    }
    
}

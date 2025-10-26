package com.voxopus.chiwiserver.util;

public class Checker<T> {

    private T data;
    private String message;
    private Exception exception;

    public Checker(T data){
        this.data = data;
        this.message = "";
        this.exception = null;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException(){
        return exception;
    }

    public T get(){
        return data;
    }

    public boolean isOk(){
        return data != null && exception == null;
    }

    public static <T> Checker<T> fail(String message){
        Checker<T> checker = new Checker<>(null);
        checker.setMessage(message);
        return checker;
    }

    public static <T> Checker<T> fail(Exception exception){
        Checker<T> checker = new Checker<>(null);
        checker.setException(exception);
        return checker;
    }

    public static <T> Checker<T> ok(T data){
        return new Checker<T>(data);
    }
    
}

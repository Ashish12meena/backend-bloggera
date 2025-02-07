package com.example.Blogera_demo.exceptions;

public class ResourceNotFoundException extends RuntimeException{
   public ResourceNotFoundException(String message){
        super(message);
    }
}

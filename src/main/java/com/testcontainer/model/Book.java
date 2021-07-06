package com.testcontainer.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Book {

    @Id
    @GeneratedValue
    private long id;
    private String title;

}
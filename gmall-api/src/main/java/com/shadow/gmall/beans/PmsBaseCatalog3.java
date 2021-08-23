package com.shadow.gmall.beans;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class PmsBaseCatalog3 implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private String catalog2Id;

    public PmsBaseCatalog3() {
    }

    @Override
    public String toString() {
        return "PmsBaseCatalog2{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", catalog1Id='" + catalog2Id + '\'' +
                '}';
    }


    public String getCatalog2Id() {
        return catalog2Id;
    }

    public void setCatalog2Id(String catalog2Id) {
        this.catalog2Id = catalog2Id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
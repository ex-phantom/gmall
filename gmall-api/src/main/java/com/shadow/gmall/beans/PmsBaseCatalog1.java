package com.shadow.gmall.beans;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class PmsBaseCatalog1 implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    @Transient
    private List<PmsBaseCatalog2> catalog2List;

    public PmsBaseCatalog1() {
    }

    public PmsBaseCatalog1(String name) {
        this.name = name;
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

    public List<PmsBaseCatalog2> getCatalog2List() {
        return catalog2List;
    }

    public void setCatalog2List(List<PmsBaseCatalog2> catalog2List) {
        catalog2List = catalog2List;
    }

    @Override
    public String toString() {
        return "PmsBaseCatalog1{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", Catalog2List=" + catalog2List +
                '}';
    }
}

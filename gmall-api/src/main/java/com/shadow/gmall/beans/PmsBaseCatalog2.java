package com.shadow.gmall.beans;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class PmsBaseCatalog2 implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private String catalog1Id;
    @Transient
    private List<PmsBaseCatalog3> catalog3List;

    public PmsBaseCatalog2() {
    }

    @Override
    public String toString() {
        return "PmsBaseCatalog2{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", catalog1Id='" + catalog1Id + '\'' +
                ", Catalog3List=" + catalog3List +
                '}';
    }

    public String getCatalog1Id() {
        return catalog1Id;
    }

    public void setCatalog1Id(String catalog1Id) {
        this.catalog1Id = catalog1Id;
    }

    public List<PmsBaseCatalog3> getCatalog3List() {
        return catalog3List;
    }

    public void setCatalog3List(List<PmsBaseCatalog3> catalog3List) {
        this.catalog3List = catalog3List;
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

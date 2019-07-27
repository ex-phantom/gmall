package com.shadow.gmall.ums.test;

public class Stu implements Comparable{
    private int id;
    private String name;
    private int age;

    public Stu() {
    }



    public Stu(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stu stu = (Stu) o;

        if (id != stu.id) return false;
        if (age != stu.age) return false;
        return name != null ? name.equals(stu.name) : stu.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + age;
        return result;
    }

    @Override
    public int compareTo(Object o) {
        //先判断当前对象是否和比较对象引用的地址
        if(this==o){
            return 0;
        }
        //判断传入比较的对象
        if(o==null){
            throw new RuntimeException();
        }
        //进行hash判断
        if(o instanceof Stu){
            Stu s=(Stu)o;
            return this.hashCode()-o.hashCode();
        }else{
            throw new RuntimeException();
        }
    }
}

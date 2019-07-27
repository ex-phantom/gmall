package com.shadow.gmall.ums.test;

import org.junit.Test;

import java.util.*;

public class TestOne {


    @Test
    public void test0(){
        int i=6;

        System.out.println(i);
    }
    @Test
    public void test1(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("aa");
        strings.add("bb");
        strings.add("cc");
        strings.add("aa");
        strings.add("dd");
        strings.add(null);
        strings.add("null");

        HashSet<String> strings1 = new HashSet<>(strings);
        System.out.println(strings1);
    }
    @Test
    public void test2(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("aa");
        strings.add("a");
        strings.add("b");
        strings.add("bb");
        strings.add("cc");
        strings.add("c");
        strings.add("d");
        strings.add("dd");
        strings.add("a");
        strings.add("r");
//        strings.forEach(System.out::println);
        System.out.println(strings);
        strings.subList(0,5).clear();
        System.out.println(strings);
    }

    @Test
    public void test3(){
        Stu stu3 = new Stu(3,"王五",22);
        Stu stu1 = new Stu(1,"张三",20);
        Stu stu4 = new Stu(4,"马六",23);
        Stu stu5 = new Stu(5,"赵七",24);
        Stu stu2 = new Stu(2,"李四",21);
        Stu stu6=null;
        ArrayList<Stu> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        stus.add(stu3);
        stus.add(stu4);
        stus.add(stu5);
        Collections.sort(stus);
        System.out.println(stus);
        Collections.sort(stus,new Comparator<Stu>(){

            @Override
            public int compare(Stu o1, Stu o2) {
                return -(o1.hashCode()-o2.hashCode());
            }
        });
        System.out.println(stus);

    }
    @Test
    public void test4(){
        ArrayList<String> stus = new ArrayList<>();
        ArrayList<String> stus2 = new ArrayList<>();
        Map<List<String>,String> map=new HashMap<>();
        stus.add("aa");
        stus.add("aa");
        stus.add("aa");
        stus2.add("aa");
        stus2.add("aa");
        stus2.add("aa");
        map.put(stus,"bb");
        map.put(stus,"dd");
        map.put(stus,"cc");
        System.out.println(map.size());
        System.out.println(map.get(stus).toString());
        System.out.println(stus.hashCode()==stus2.hashCode()?true:false);
    }
    @Test
    public void test5(){
        String a1=new String("asd");
        Integer i1=new Integer(120);
        String a2="qwe";
        Stu stu = new Stu(1, "a", 10);

        this.change(a1,i1,a2,stu);
        System.out.println(a1);
        System.out.println(i1);
        System.out.println(a2);
        System.out.println(stu.toString());

    }

    private void change(String a1, int i1,String a2,Stu stu) {

        a1="zxc";
        a2="zxc";
        i1=127;
        stu.setName("b");
        stu.setAge(9);
    }
    @Test
    public void test(){
       System.out.println( this.pt(5));
    }

    private int pt(int n) {
        if(n==1){
            return 1;
        }
        return pt(n-1)+n;
    }


}

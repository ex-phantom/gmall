package com.shadow.gmall.ums.test;

interface  Car{
    public void print();
};
class FordCar implements Car{
    @Override
    public void print() {
        System.out.println(FordCar.class.getName());
    }
};
class BuickCar implements Car{
    @Override
    public void print() {
        System.out.println(BuickCar.class.getName());
    }
};
class benchiCar{};

public class A {
    public static Car createCar(Class<? extends Car> c){
        try {
            return (Car)c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Car car = A.createCar(FordCar.class);
        //Class<? extends Car>泛型中的的？代表输入的对象必须继承或者实现Car
       // Car car1 = A.createCar(benchiCar.class);
        System.out.println(car.toString());

    }
}

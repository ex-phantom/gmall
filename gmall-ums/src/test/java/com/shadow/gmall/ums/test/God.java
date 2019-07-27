package com.shadow.gmall.ums.test;

public class God {
    private  static int i=1;

    public static void main(String[] args) {
        System.out.println(i);
        int i=11;
        getI();

        System.out.println(i);

    }

    public static void getI() {

       ++i;
        System.out.println(i);
    }
}

package com.academy.envy.Utils;

import com.academy.envy.R;

public class DefaultImage {
    String name;
    int res;

    public DefaultImage() {
    }
    public DefaultImage(String name) {
        this.name = name;
    }

    public int getDefaultImage(){

        switch (name.toUpperCase().charAt(0)){
            case ('A') :  return R.drawable.a;
            case ('B') :  return R.drawable.b;
            case ('C') :  return R.drawable.c;
            case ('D') :  return R.drawable.d;
            case ('E') :  return R.drawable.e;
            case ('F') :  return R.drawable.f;
            case ('G') :  return R.drawable.g;
            case ('H') :  return R.drawable.h;
            case ('I') :  return R.drawable.i;
            case ('J') :  return R.drawable.j;
            case ('K') :  return R.drawable.k;
            case ('L') :  return R.drawable.l;
            case ('M') :  return R.drawable.m;
            case ('N') :  return R.drawable.n;
            case ('O') :  return R.drawable.o;
            case ('P') :  return R.drawable.p;
            case ('Q') :  return R.drawable.q;
            case ('R') :  return R.drawable.r;
            case ('S') :  return R.drawable.s;
            case ('T') :  return R.drawable.t;
            case ('U') :  return R.drawable.u;
            case ('V') :  return R.drawable.v;
            case ('W') :  return R.drawable.w;
            case ('X') :  return R.drawable.x;
            case ('Y') :  return R.drawable.y;
            case ('Z') :  return R.drawable.z;
        }
        return 0;
    }
}

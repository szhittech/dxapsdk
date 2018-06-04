package com.het.core.observer;

import java.util.*;

/**
 * Created by uuxia-mac on 16/1/10.
 */
public class Observable {
    //    private static List<Observer> observers = new ArrayList<Observer>();
    private static List<Observer> observers = new ArrayList<>();

    public synchronized void addObserver(Observer observer) {
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        if (!observers.contains(observer)){
            observers.add(observer);
        }
    }

    public synchronized void deleteObserver(Observer observer) {
        observers.remove(observer);
    }

    public synchronized void deleteObservers() {
        observers.clear();
    }

    public synchronized void notifyObservers1(Object data) {
        if (observers != null) {
            Iterator<Observer> it = observers.iterator();
            while (it.hasNext()) {
                Observer mgr = it.next();
                if (data != null && mgr != null){
                    mgr.update(this, data);
                }
            }
        }
    }

    public void notifyObservers(Object data) {
        int size = 0;
        Observer[] arrays = null;
        synchronized (this) {
            size = observers.size();
            arrays = new Observer[size];
            observers.toArray(arrays);
        }
        if (arrays != null) {
            for (Observer observer : arrays) {
                observer.update(this,data);
            }
        }
    }
}

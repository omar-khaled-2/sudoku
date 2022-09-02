package com.sphinx.sudoku



interface Observer{
    fun update()
}


abstract class Observable{

    private val observers = mutableListOf<Observer>();

    fun addObserver(observer: Observer){
        observers.add(observer)
    }

    fun deleteObserver(observer: Observer){
        observers.remove(observer)
    }


    fun notifyObservers(){
        for(observer in observers){
            observer.update()
        }
    }
}
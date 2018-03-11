package org.zkoss.zss.model.impl;

/**
 * Created by Sajjadur on 3/10/2018.
 */
public class KeyIndexMap<T> {
    T key;
    int pos;

    public KeyIndexMap(T key,int pos)
    {
        this.key = key;
        this.pos = pos;
    }

    public T getKey()
    {
        return key;
    }
    public int getPos()
    {
        return  pos;
    }
}

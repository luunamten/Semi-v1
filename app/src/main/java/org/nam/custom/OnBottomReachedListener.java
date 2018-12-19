package org.nam.custom;

import org.nam.object.IHaveIdAndName;

public interface OnBottomReachedListener<T> {
    public void onBottomReached(T obj, int position);
}

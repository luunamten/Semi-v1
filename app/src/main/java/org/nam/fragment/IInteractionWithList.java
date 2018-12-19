package org.nam.fragment;

import org.nam.object.IHaveIdAndName;

public interface IInteractionWithList<T> {
    public void onItemClick(T obj);
    public void onScrollToLimit(T obj, int position);
}

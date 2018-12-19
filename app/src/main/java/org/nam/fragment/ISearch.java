package org.nam.fragment;

public interface ISearch {
    public void search(int type, String query);
    public void scroll(String lastId);
    public void clickItem(String id);
}

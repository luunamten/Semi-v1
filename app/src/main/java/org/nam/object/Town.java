package org.nam.object;

public class Town {
    private int id;
    private String name;

    public Town() {}

    public Town(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Town(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

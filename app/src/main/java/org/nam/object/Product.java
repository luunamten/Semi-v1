package org.nam.object;

public class Product {
    private String id;
    private Store store;
    private String title;
    private String fullName;
    private String description;
    private long cost;
    private String imageURL;
    private Type type;
    public static class Type implements IHaveIdAndName {
        private int id;
        private String name;
        public Type() {}
        public Type(int id, String name) {
            this.id = id;
            this.name = name;
        }
        @Override
        public int getId() {
            return id;
        }
        @Override
        public void setId(int id) {
            this.id = id;
        }
        @Override
        public String getName() {
            return name;
        }
        @Override
        public void setName(String name) {
            this.name = name;
        }
        @Override
        public String toString() {return this.name;}
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

package org.nam.object;
import java.util.List;

public class Store {
    private String id;
    private String title;
    private String fullName;
    private String description;
    private Type type;
    private List<Utility> utilities;
    private Address address;
    private float rating;
    private Location geo;
    private String contact;
    private String imageURL;
    private String startEnd;
    private List<Product> products;

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

    public static class Utility implements IHaveIdAndName {
        private int id;
        private String name;
        public Utility() {}
        public Utility(int id, String name) {
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

    public String getTitle() {
        return title;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Utility> getUtilities() {
        return utilities;
    }

    public void setUtilities(List<Utility> utilities) {
        this.utilities = utilities;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Location getGeo() {
        return geo;
    }

    public void setGeo(Location geo) {
        this.geo = geo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStartEnd() {
        return startEnd;
    }

    public void setStartEnd(String startEnd) {
        this.startEnd = startEnd;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
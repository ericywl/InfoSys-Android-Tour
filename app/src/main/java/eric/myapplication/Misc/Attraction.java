package eric.myapplication.Misc;


import android.support.annotation.NonNull;

import java.io.Serializable;

public class Attraction implements Comparable<Attraction>, Serializable {
    private String name;
    private String address;
    private Integer image;
    private String description;

    public Attraction(String name, String address, Integer image, String description) {
        this.name = name;
        this.address = address;
        this.image = image;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getImage() {
        return image;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;

        Attraction compared = (Attraction) obj;
        return compared.getName().equals(this.getName());
    }

    @Override
    public int compareTo(@NonNull Attraction compared) {
        return this.getName().compareTo(compared.getName());
    }
}

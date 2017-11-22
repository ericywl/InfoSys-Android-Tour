package eric.myapplication.misc;


import android.support.annotation.NonNull;

import java.io.Serializable;

public class Attraction implements Comparable<Attraction>, Serializable {
    private String name;
    private String description;
    private Integer image;

    public Attraction(String name, String description, Integer image) {
        this.name = name;
        this.description = description;
        this.image = image;
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

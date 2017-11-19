package eric.myapplication;


import android.support.annotation.NonNull;

public class Attraction implements Comparable<Attraction>{
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
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

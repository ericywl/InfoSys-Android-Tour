package eric.myapplication;


public class Attraction {
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
        if (obj.getClass() != getClass()) return false;

        Attraction compared = (Attraction) obj;
        return compared.getName().equals(getName());
    }
}

package com.example.auraperfume;

import java.io.Serializable;

public class Aura implements Serializable {

    private String id;
    private String name;
    private String type;
    private String description;
    private String location;
    private String logoUrl;
    private boolean isSoldOut;

    public Aura() {
    }

    public Aura(String id, String name, String type, String description, String location, String logoUrl) {
        this(id, name, type, description, location, logoUrl, false);
    }

    public Aura(String id, String name, String type, String description, String location, String logoUrl, boolean isSoldOut) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.location = location;
        this.logoUrl = logoUrl;
        this.isSoldOut = isSoldOut;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }
}

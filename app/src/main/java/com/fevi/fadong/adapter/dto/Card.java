package com.fevi.fadong.adapter.dto;

/**
 * Created by 1000742 on 15. 1. 5..
 */
public class Card {
    private String id;

    private String source;
    private String picture;
    private String description;

    /*
        Page 관련 도메인
    */
    private String name;
    private String category;
    private String profile_image;

    private String updated_time;
    private String created_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", picture='" + picture + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", updated_time='" + updated_time + '\'' +
                ", created_time='" + created_time + '\'' +
                '}';
    }
}

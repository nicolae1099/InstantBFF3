package com.example.instantbff;


public class User {

    private String fullName;
    private String email;
    private String city;
    private String instagram;
    private String age;
    private String personality;
    private String description;

    public User() {

    }

    public User(String fullName, String email, String city, String instagram, String age, String personality, String description) {
        this.fullName = fullName;
        this.email = email;
        this.city = city;
        this.instagram = instagram;
        this.age = age;
        this.personality = personality;
        this.description = description;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

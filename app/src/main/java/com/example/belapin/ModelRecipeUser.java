package com.example.belapin;

public class ModelRecipeUser {
    String recipeId;
    String timestamp;
    String recipeName;
    String personCount;
    String time;
    String ingredients;
    String howTo;
    String imageUrl;
    String uid;

    public ModelRecipeUser() {

    }

    public ModelRecipeUser(String recipeId, String timestamp, String recipeName, String personCount, String time, String ingredients, String howTo, String imageUrl, String uid) {
        this.recipeId = recipeId;
        this.timestamp = timestamp;
        this.recipeName = recipeName;
        this.personCount = personCount;
        this.time = time;
        this.ingredients = ingredients;
        this.howTo = howTo;
        this.imageUrl = imageUrl;
        this.uid = uid;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getPersonCount() {
        return personCount;
    }

    public void setPersonCount(String personCount) {
        this.personCount = personCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getHowTo() {
        return howTo;
    }

    public void setHowTo(String howTo) {
        this.howTo = howTo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

package com.bihar.pratidin.Model;

public class Category {

    public int id;
    public int lang_id;
    public String name;
    public String name_slug;
    public int parent_id;
    public String description;
    public String keywords;
    public String color;
    public String block_type;
    public int category_order;
    public int show_at_homepage;
    public int show_on_menu;
    public String created_at;
    public String cat_id;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

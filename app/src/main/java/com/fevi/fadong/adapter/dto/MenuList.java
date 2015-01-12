package com.fevi.fadong.adapter.dto;

import android.graphics.drawable.Drawable;

/**
 * Created by 1000742 on 15. 1. 2..
 */
public class MenuList {
    private Drawable menuIcon;
    private String menuName;

    public MenuList() {
    }

    public MenuList(Drawable menuIcon, String menuName) {
        this.menuIcon = menuIcon;
        this.menuName = menuName;
    }

    public Drawable getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(Drawable menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
}

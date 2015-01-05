package com.fevi.fadong.adapter.dto;

/**
 * Created by 1000742 on 15. 1. 2..
 */
public class MenuList {
    private int menuIcon;
    private String menuName;

    public MenuList() {
    }

    public MenuList(int menuIcon, String menuName) {
        this.menuIcon = menuIcon;
        this.menuName = menuName;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
}

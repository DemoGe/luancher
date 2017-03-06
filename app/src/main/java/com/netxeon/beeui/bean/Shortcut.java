package com.netxeon.beeui.bean;

/**
 * 快捷方式bean类
 */
public class Shortcut {

    private String category;
    private String packName;
    private Boolean persistent;

    public Boolean getPersistent() {
        return persistent;
    }

    public void setPersistent(Boolean selected) {
        persistent = selected;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComponentName() {
        return packName;
    }

    public void setComponentName(String packName) {
        this.packName = packName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Shortcut) {
            Shortcut shortcut= (Shortcut) o;
            return this.packName.equals(shortcut.packName);
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return packName;
    }
}

package com.example.feedback;

public class CustomisedCriteria {
    private String criteria;
    private String subSection;
    private String shortText;
    private String longText;

    @Override
    public String toString() {
        return "CustomisedCriteria{" +
                "criteria='" + criteria + '\'' +
                ", subSection='" + subSection + '\'' +
                ", shortText='" + shortText + '\'' +
                ", longText='" + longText + '\'' +
                '}';
    }

    public CustomisedCriteria() {
        criteria = "";
        subSection = "";
        shortText = "";
        longText = "";
    }

    public String getCriteria() {
        return criteria;
    }

    public String getSubSection() {
        return subSection;
    }

    public String getShortText() {
        return shortText;
    }

    public String getLongText() {
        return longText;
    }

    public void setSubSection(String subSection) {
        this.subSection = subSection;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

}

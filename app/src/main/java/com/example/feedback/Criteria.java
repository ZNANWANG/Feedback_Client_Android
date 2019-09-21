package com.example.feedback;

import java.util.ArrayList;

public class Criteria {

    private String name;
    private int weighting;
    private int maximunMark;
    private String markIncrement; //"full" means 1; "half" means 1/2; "quarter" means 1/4;
    private ArrayList<SubSection> subsectionList = new ArrayList<SubSection>();

    public String getName() {

        return name;

    }

    public void setName(String name) {

        this.name = name;

    }

    public int getWeighting() {

        return weighting;

    }

    public void setWeighting(int weighting) {

        this.weighting = weighting;

    }

    public int getMaximunMark() {

        return maximunMark;

    }

    public void setMaximunMark(int maximunMark) {

        this.maximunMark = maximunMark;

    }

    public String getMarkIncrement() {

        return markIncrement;

    }

    public void setMarkIncrement(String markIncrement) {

        this.markIncrement = markIncrement;

    }

    public ArrayList<SubSection> getSubsectionList() {

        return subsectionList;

    }

    public void setSubsectionList(ArrayList<SubSection> subsectionList) {

        this.subsectionList = subsectionList;

    }

    @Override
    public boolean equals(Object anObject) {
        if (!(anObject instanceof Criteria)) {
            return false;
        }
        Criteria otherMember = (Criteria) anObject;
        return otherMember.getName().equals(getName());
    }

}

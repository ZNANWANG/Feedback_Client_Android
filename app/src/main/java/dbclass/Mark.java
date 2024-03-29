package dbclass;

import java.util.ArrayList;

public class Mark {

    private ArrayList<Criteria> criteriaList = new ArrayList<Criteria>();
    private ArrayList<Double> markList = new ArrayList<Double>();
    private ArrayList<Criteria> commentList= new ArrayList<Criteria>();
    private String comment;
    private double totalMark;
    private String lecturerName;
    private String lecturerEmail;
    
    public String getLecturerName() {
    	return lecturerName;
    }
    
    public void setLecturerName(String lecturerName) {
    	this.lecturerName= lecturerName;
    }

    public double getTotalMark(){
        return totalMark;
    }

    public void setTotalMark(double totalMark){
        this.totalMark = totalMark;
    }

    public String getLecturerEmail() {
        return lecturerEmail;
    }

    public void setLecturerEmail(String lecturerEmail) {
        this.lecturerEmail = lecturerEmail;
    }

    public ArrayList<Criteria> getCommentList() {

        return commentList;

    }

    public void setCommentList(ArrayList<Criteria> commentList) {

        this.commentList = commentList;

    }


    public String getComment() {

        return comment;
    
    }

    public void setComment(String comment) {

        this.comment = comment;

    }

    public void setCriteriaList(ArrayList<Criteria> criteriaList) {

        this.criteriaList = criteriaList;

    }

    public void setMarkList(ArrayList<Double> markList) {

        this.markList = markList;

    }

    public ArrayList<Criteria> getCriteriaList() {

        return criteriaList;

    }

    public ArrayList<Double> getMarkList() {

        return markList;

    }
}

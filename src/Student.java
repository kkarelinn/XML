
import java.util.ArrayList;

public class Student {
    private String firstName;
    private String lastName;
    private String groupNumber;
    private ArrayList<Subject> subjectList;
    private double average;

    public Student(String firstName, String lastName, String groupNumber
            , ArrayList<Subject> subjectList, double average) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupNumber = groupNumber;
        this.subjectList = subjectList;
        this.average = average;
    }

   /* public double getAverage() {
        return average;
    }*/

    public void setAverage(double average) {
        this.average = (getAvgFromSubjects(subjectList) == average)? average : getAvgFromSubjects(subjectList);
    }

    private double getAvgFromSubjects(ArrayList<Subject> subjectList){
       double d = subjectList.stream().mapToDouble(Subject::getMark).average().orElse(0.0);
//        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
//       DecimalFormat f = new DecimalFormat("#.#", symbols);

        return Math.round(d*10)/10d;
    }

    public String getSubjects(){
        StringBuilder s = new StringBuilder("\n[");
        for (Subject subject: subjectList) {
            s.append("      \""+subject.getTitle()+"\" mark:"+subject.getMark()+"\n");
        }
        s.append("]\n");
        int id = s.lastIndexOf("\n");
        return s.toString().replaceFirst(" ","").replaceFirst("\n]","    ]");
    }
    @Override
    public String toString() {
        return "Student{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", subjectList=" + getSubjects() +
                ", average=" + average +
                '}';
    }
}

package stone.philosophers.com.driversed;


public class Student {

    private String name;
    private String teacher;
    private double hoursDriven;
    private double nightHoursDriven;
    private String emailAddress;

    public Student(){}

    public Student(String studentName, String studentTeacher, double studentHours, double studentNightHours, String studentEmailAddress) {
        name = studentName;
        teacher = studentTeacher;
        hoursDriven = studentHours;
        nightHoursDriven = studentNightHours;
        emailAddress = studentEmailAddress;
    }

    public String getName(){
        return name;
    }

    public String getTeacher() {
        return teacher;
    }

    public double getHoursDriven(){
        return hoursDriven;
    }

    public double getNightHoursDriven(){
        return nightHoursDriven;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}

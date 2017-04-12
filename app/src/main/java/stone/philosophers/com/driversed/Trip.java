package stone.philosophers.com.driversed;

public class Trip {
    public Trip(String studentName, String teacherName, long startTime, long endTime, double totalMilesDriven, String studentEmail) {
        this.studentName = studentName;
        this.teacherName = teacherName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalMilesDriven = totalMilesDriven;
        this.studentEmail = studentEmail;
    }

    public Trip(){}

    private String studentName;
    private String teacherName;
    private long startTime;
    private long endTime;
    private double totalMilesDriven;
    private String studentEmail;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getTotalMilesDriven() {
        return totalMilesDriven;
    }

    public void setTotalMilesDriven(double totalMilesDriven) {
        this.totalMilesDriven = totalMilesDriven;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }
}

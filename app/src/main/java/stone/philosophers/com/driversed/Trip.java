package stone.philosophers.com.driversed;

public class Trip {
    private String studentName;
    private String teacherName;
    private long startTime;
    private long endTime;
    private double totalMilesDriven;

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
}

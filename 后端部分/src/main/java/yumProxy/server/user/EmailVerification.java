package yumProxy.server.user;

public class EmailVerification {
    private int id;
    private String email;
    private String code;
    private String createTime;
    private int status;

    public EmailVerification() {}
    public EmailVerification(int id, String email, String code, String createTime, int status) {
        this.id = id;
        this.email = email;
        this.code = code;
        this.createTime = createTime;
        this.status = status;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    @Override
    public String toString() {
        return "EmailVerification{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", createTime='" + createTime + '\'' +
                ", status=" + status +
                '}';
    }
} 

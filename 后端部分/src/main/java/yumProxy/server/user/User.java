package yumProxy.server.user;

public class User {
    private int id;
    private String username;
    private String password;
    private String createTime;
    private int rank;
    private int pid;

    public User() {}
    public User(int id, String username, String password, String createTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createTime = createTime;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public int getPid() { return pid; }
    public void setPid(int pid) { this.pid = pid; }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", rank=" + rank +
                ", pid=" + pid +
                ", createTime='" + createTime + '\'' +
                '}';
    }
} 

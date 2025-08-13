package yumProxy.server.key;

public class Key {
    private String prefix;
    private String code;

    public Key(String prefix, String code) {
        this.prefix = prefix;
        this.code = code;
        this.time = 0;
    }

    public Key(String prefix, String code, int time) {
        this.prefix = prefix;
        this.code = code;
        this.time = time;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return prefix + "-" + code;
    }
}

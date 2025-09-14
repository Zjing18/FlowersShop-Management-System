package model;
public class Customer {
    private int id;
    private String account;
    private String password;
    private String name;
    private int sex;
    private String tel;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getSex() { return sex; }
    public void setSex(int sex) { this.sex = sex; }
    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }
}
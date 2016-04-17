package android.tnp.DAO;

/**
 Created by SANYAM TYAGI on 2/10/2016.
 */

public class BeanPlacementData {

    private int num;
    private String data;
    private String subject;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public int getNum() {
        return num;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setNum(int num) {
        this.num = num;
    }
}

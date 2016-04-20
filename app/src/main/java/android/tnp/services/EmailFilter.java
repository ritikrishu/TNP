package android.tnp.services;

/**
 * Created by SANYAM TYAGI on 2/12/2016.
 */
public class EmailFilter {

    public String filter(String msg) {
        String[] data = msg.split("\n");
        boolean flag = false;
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < data.length; i++) {

            if (data[i].contains("Content-Type: text/plain; charset=UTF-8")) {
           // if(data[i].contains("Date:")){
                flag = true;
                continue;
            }

            if (flag) {
                if ((data[i].trim().equalsIgnoreCase("::Disclaimer::"))||
                        (data[i].trim().equalsIgnoreCase("--"))) {
                    break;
                }
                result.append(data[i]);
                result.append("\n");
            }

        }

        return (result.toString());
    }

}

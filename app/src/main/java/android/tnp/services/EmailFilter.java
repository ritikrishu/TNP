package android.tnp.services;

import android.util.Log;

/**
 * Created by SANYAM TYAGI on 2/12/2016.
 */
public class EmailFilter {

    public String filter(String msg) {
        String[] data = msg.split("\n");
        boolean flag = false;
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            Log.i("if", "infor");
//                Log.d("test", data.length+"");
            if (data[i].contains("Content-Type: text/plain; charset=UTF-8")) {
                Log.i("if", "infalse");
                flag = true;
                continue;
            }

            if (flag) {
                Log.i("if", "intrue");
                if ((data[i].trim().equalsIgnoreCase("::Disclaimer::"))||(data[i].trim().equalsIgnoreCase("send from my tnp app"))) {
                    break;
                }
                result.append(data[i]);
                result.append("\n");
            }

        }

        return (result.toString());
    }

}

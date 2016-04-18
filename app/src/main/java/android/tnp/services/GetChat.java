package android.tnp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Created by ritik on 4/15/2016.
 */
public class GetChat extends IntentService {
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {GmailScopes.MAIL_GOOGLE_COM};
    private static final String PREF_ACCOUNT_NAME = "accountName";
    static Gmail mService = null;
    public static final String name = "GetChatService";
    public GetChat(){
        super(name);
    }

    // # this is supposed to be vikrams' email id
    private static final String SPECIFIEDEMAILIDFORCHATACTIVITY = "ritikrishu@gmail.com";

    @Override
    protected void onHandleIntent(Intent intent) {
        final java.text.DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        SharedPreferences settings = getSharedPreferences(PREF_ACCOUNT_NAME,Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        new Thread(){
            @Override
            public void run(){
//                Looper.prepare();
                Comparator<MessageAndDate> forShorting = new Comparator<MessageAndDate>() {
                    @Override
                    public int compare(MessageAndDate lhs, MessageAndDate rhs) {
                        try {
                            return df.parse(lhs.date).compareTo(df.parse(rhs.date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                };
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                mService = new Gmail.Builder(
                        transport, jsonFactory, mCredential)
                        .setApplicationName("Gmail API ")
                        .build();

                // you need to use the list below-------
                try {
                    List<MessageAndDate> list = getMsgFromSpecificUser();
                    Collections.sort(list , forShorting);
                    Log.e("testLastMailTime",list.get(list.size()-1).date);
                    /*
                        # Variable "list" is a list of MessageAndDateObject
                        # MessageAndDateobject contains two Strings date in     04/15/2016 23:58:34     <-this format and mail as given by EmailFIlter.filter()

                        ::list is shorted in accending order as per date of each mail::

                     */
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            private List<MessageAndDate> getMsgFromSpecificUser() throws IOException{
                String user = "me";
                List<MessageAndDate> messages = new ArrayList<>();
                ListMessagesResponse listContainingUnreadMail = mService.users().messages().list(user).setQ("is:unread from:(" + SPECIFIEDEMAILIDFORCHATACTIVITY + ")").execute();
                byte[] sam;
                for (Message message : listContainingUnreadMail.getMessages()){
                    sam = mService.users().messages().get(user, "" + message.getId()).setFormat("raw").execute().decodeRaw();
                    try {
                        messages.add(new MessageAndDate(df.format(getMimeMessage(message.getId()).getSentDate()) , new EmailFilter().filter(new String(sam))));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
                return messages;
            }

            private MimeMessage getMimeMessage(String messageId)
                    throws IOException, MessagingException {
                Message message = mService.users().messages().get("me", messageId).setFormat("raw").execute();

                byte[] emailBytes = Base64.decodeBase64(message.getRaw());

                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);

                MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
                return email;
            }

        }.start();
    }
}

class MessageAndDate{
    public String date;
    public String mail;
    MessageAndDate(String date, String mail){
        this.date = date;
        this.mail = mail;
    }
}

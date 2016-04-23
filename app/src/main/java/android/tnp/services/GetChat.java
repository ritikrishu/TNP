package android.tnp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.g38.tnp.R;
import android.support.v4.app.NotificationCompat;
import android.tnp.DAO.CreateDB;
import android.tnp.chat.ChatActivity;
import android.tnp.chat.ChatAdapter;
import android.tnp.chat.ChatMessage;

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
    private ChatAdapter adapter;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {GmailScopes.MAIL_GOOGLE_COM};
    private static final String PREF_ACCOUNT_NAME = "accountName";
    static Gmail mService = null;
    public static final String name = "GetChatService";
    public GetChat(){
        super(name);
    }

    // # this is supposed to be vikrams' email id
    private static final String SPECIFIEDEMAILIDFORCHATACTIVITY = "sanyamtyagi95@gmail.com";

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
                    if(list!=null){
                    Collections.sort(list , forShorting);
                    CreateDB createDB=new CreateDB(getApplicationContext()) ;
                    ChatMessage chatMessage;
                        Boolean flag;
                        SharedPreferences sharedPreferences=getSharedPreferences("caller",MODE_PRIVATE);
                        flag=sharedPreferences.getBoolean("flag",false);
                    for(int i=0;i<list.size();i++){
                        chatMessage = new ChatMessage();
                        chatMessage.setMessage(list.get(i).mail);
                        chatMessage.setDate(list.get(i).date);
                        chatMessage.setMe(false);
                        if(!(createDB.insertChatData(chatMessage)==-1)){
                            if(!(flag))
                                buildNotification("New Message From TNP",list.get(i).mail);
                            else {
//                                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                getApplication().startActivity(intent);
//                                Activity a=(Activity)getApplicationContext();
//                                ImageView imageView=(ImageView)a.findViewById(R.id.ivNewMsg);
//                                imageView.setVisibility(View.VISIBLE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putBoolean("image",true);

                                editor.commit();


                            }
                        }
                    }
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean("flag",false);
                        editor.commit();


                    }

                    /*
                        # Variable "list" is a list of MessageAndDateObject
                        # MessageAndDateobject contains two Strings date in     04/15/2016 23:58:34     <-this format and mail as given by EmailFIlter.filter()

                        ::list is shorted in accending order as per date of each mail::

                     */

                }
                catch (Exception e){
                    //kuch nhi
                    //e.printStackTrace();
                }
                finally {
                    SharedPreferences sharedPreferences=getSharedPreferences("caller",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("servicedone",true);
                    editor.commit();
                    }
            }
            private List<MessageAndDate> getMsgFromSpecificUser() throws IOException{
                String user = "me";
                List<MessageAndDate> messages = new ArrayList<>();
                ListMessagesResponse listContainingUnreadMail = mService.users().messages().list(user).setQ("is:unread from:(" + SPECIFIEDEMAILIDFORCHATACTIVITY + ")").execute();
                byte[] sam;
                if(listContainingUnreadMail.size()==1) {
                    return messages;
                }
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
    private void buildNotification(String title,String msg){
        Intent notificationIntent = new Intent(getApplicationContext(),ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.applogo)
                .setContentTitle(title).setContentText(msg).setAutoCancel(true).setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND);


        NotificationManager notificationmanager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());
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

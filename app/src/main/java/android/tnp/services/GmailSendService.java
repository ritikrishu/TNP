package android.tnp.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.g38.tnp.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;


import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 * Created by ritik on 4/18/2016.
 */
public class GmailSendService extends BroadcastReceiver {
    public GmailSendService() {
        super();
    }
    private static final String SENDMAILTO = "samtyagi.111@gmail.com";
    private static final String SUBJECT = "T & P query";
    private static Gmail mService;
    private static final String[] SCOPES = {GmailScopes.MAIL_GOOGLE_COM};
    private GoogleAccountCredential mCredential;
    @Override
    public void onReceive(final Context context, final Intent intent) {

        SharedPreferences preferences = context.getSharedPreferences("accountName",Context.MODE_PRIVATE);
        if(isNetworkAvailable(context)) {
            mCredential = GoogleAccountCredential.usingOAuth2(
                    context, Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff())
                    .setSelectedAccountName(preferences.getString("accountName",null));
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, mCredential)
                    .setApplicationName("Gmail API ")
                    .build();
            new Thread() {
                @Override
                public void run() {
                    try {
                        sendMessage(createMessageWithEmail(createEmail(intent.getStringExtra("body"))));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.applogo)
                                .setContentTitle("Mail send failed!!!").setContentText("IO Error").setAutoCancel(true);

                        NotificationManager notificationmanager = (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationmanager.notify(0, builder.build());
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.applogo)
                    .setContentTitle("Query send failed!!!").setContentText("No internet connection found!!!").setAutoCancel(false);

            NotificationManager notificationmanager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationmanager.notify(0, builder.build());
        }
    }
    private static Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();

        message.setRaw(encodedEmail);
        message.setId("this is fake id");
        return message;

    }

    private static void sendMessage(Message message)
            throws MessagingException, IOException {
        if(mService != null)
            mService.users().messages().send("me", message).execute();
    }

    private static MimeMessage createEmail(String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress("me"));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(SENDMAILTO));
        email.setSubject(SUBJECT);
        email.setContent(bodyText, "text/html; charset=ISO-8859-1");
        email.setText(bodyText);
        return email;
    }
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}

package me.calebjones.spacelaunchnow.content.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import me.calebjones.spacelaunchnow.content.models.Launch;


public class LaunchImminentReceiver extends BroadcastReceiver {
    private Launch launch;

        public void onReceive(Context context, Intent intent){

            //GetNotification preferences
//            SharedPreferences sharedPreferences = PreferenceManager
//                    .getDefaultSharedPreferences(context);
//            Boolean notificationCheckBox = sharedPreferences
//                    .getBoolean("notifications_new_message", false);
//            Boolean vibrateCheckBox = sharedPreferences
//                    .getBoolean("notifications_new_message_vibrate", false);
//            String ringtoneBox = sharedPreferences
//                    .getString("notifications_new_message_ringtone", "default ringtone");
//            int priority;
//            if (vibrateCheckBox){
//                priority = 1;
//            } else {
//                priority = -1;
//            }
//
//            Timber.d("The Jones Theory", "NewPost - Hello World!");
//            if (intent.getAction().equals(PostDownloader.NEW_POST) && notificationCheckBox){
//
//                //Init a DB connection
//                DatabaseManager databaseManager = new DatabaseManager(context);
//
//                //Grab the new postID and Bitmap
//                int postID = intent.getExtras().getInt(LaunchDataService.EXTRA_NUM);
//                byte[] bytes = intent.getByteArrayExtra("BitmapImage");
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                Timber.d("The Jones Theory-NPR", "Post - ID: " + postID );
//
//                post = databaseManager.getPostByID(postID);
//
//                // Specify the 'big view' content to display the long
//                // event description that may not fit the normal content text.
//                android.support.v4.app.NotificationCompat.BigPictureStyle
//                        bigStyle = new NotificationCompat.BigPictureStyle();
//
//                //Get information about the post that was selected and start activity.
//                Intent mainActIntent = new Intent(context, DetailActivity.class);
//                mainActIntent.putExtra("PostTitle", post.getTitle());
//                mainActIntent.putExtra("PostImage", post.getFeaturedImage());
//                mainActIntent.putExtra("PostText", post.getContent());
//                mainActIntent.putExtra("PostURL", post.getURL());
//                mainActIntent.putExtra("PostID", post.getPostID());
//                PendingIntent clickIntent = PendingIntent.getActivity(context, 57836,
//                        mainActIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                //Set up the PendingIntent for the Share action button
//                Intent sendThisIntent = new Intent();
//                sendThisIntent.setAction(Intent.ACTION_SEND);
//                sendThisIntent.putExtra(Intent.EXTRA_TEXT, post.getURL());
//                sendThisIntent.setType("text/plain");
//
//                PendingIntent sharePendingIntent = PendingIntent
//                        .getActivity(context, 0, sendThisIntent, 0);
//
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//                mBuilder.setContentTitle("New " + post.getCategories() + " post available!")
//                        .setContentText(Html.fromHtml(post.getTitle()))
//                        .setContentIntent(clickIntent)
//                        .setSmallIcon(R.drawable.ic_notificaiton)
//                        .setSound(Uri.parse(ringtoneBox))
//                        .setLargeIcon(BitmapFactory
//                                .decodeResource(context.getResources(), R.mipmap.ic_launcher))
//                        .setStyle(bigStyle
//                                .bigPicture(bitmap)
//                                .setSummaryText(Html.fromHtml(post.getExcerpt()))
//                                .setBigContentTitle(post.getTitle()))
//                        .setPriority(priority)
//                        .addAction(R.drawable.ic_action_share, "Share", sharePendingIntent)
//                        .addAction(R.drawable.ic_fullscreen, "Open", clickIntent)
//                        .setAutoCancel(true);
//
//                // Issues the notification
//                NotificationManager mNotifyManager = (NotificationManager)
//                        context.getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotifyManager.notify(NOTIF_ID, mBuilder.build());
//                Timber.d("The Jones Theory", "Notification issued for new post.");
//            }
        }
    }
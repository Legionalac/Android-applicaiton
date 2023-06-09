package nikola.pavicevic.shoppinglist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;

import java.io.IOException;

public class UpdateService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ListDbHelper listDbHelper= new ListDbHelper(this,getString(R.string.DB_NAME),null,1);
        TaskDbHelper taskDbHelper= new TaskDbHelper(this,getString(R.string.DB_NAME),null,1);
        HttpHelper httpHelper = new HttpHelper();
        String url = getString(R.string.ipAdress) + "lists";
        Context context = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                NotificationChannel channel = new NotificationChannel("shoppingList",
                        "UpdateService",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Channel for ShoppingList");
                notificationManager.createNotificationChannel(channel);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"shoppingList")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Shopping list")
                        .setContentText("Local database is updated");
                while (true) {
                    try {
                        List[] httpLists = httpHelper.getSharedLists(url);
                        List[] dbList = listDbHelper.readSharedLists();
                        for(List i: dbList) {
                            Task[] tasks = taskDbHelper.readTasks(i.getName());
                            for(Task j: tasks){
                                taskDbHelper.removeTask(String.valueOf(j.getID()));
                            }
                        }
                        for(List i: httpLists){
                            Task[] tasks = httpHelper.getTasks(getString(R.string.ipAdress) + "tasks/",i.getName());
                            for(Task j: tasks){
                                int checked;
                                if(j.isCheck())
                                    checked = 1;
                                else
                                    checked = 0;
                                taskDbHelper.insertWithID(j.getName(),i.getName(),checked,j.getID());
                            }
                        }
                        listDbHelper.deleteSharedLists();
                        listDbHelper.insertLists(httpLists);
                        notificationManager.notify(0, builder.build());
                        Thread.sleep(10000);
                        Log.d("UpdateService", "DB updated");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
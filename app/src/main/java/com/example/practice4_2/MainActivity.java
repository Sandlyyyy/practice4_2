package com.example.practice4_2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private ImageView imgLab, imgSpace, imgCabin;
    private ConstraintLayout layoutBridge;
    private NotificationManager notificationManager;

    private static final int NOTIFY_ID = 2026;
    private static final String CHANNEL_ID = "SPACE_STATION_CHANNEL";
    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        imgLab = findViewById(R.id.imgLab);
        imgSpace = findViewById(R.id.imgSpace);
        imgCabin = findViewById(R.id.imgCabin);
        layoutBridge = findViewById(R.id.layoutBridge);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setupNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    public void onBridgeClick(View view) {
        layoutBridge.setVisibility(View.VISIBLE);
        imgLab.setVisibility(View.INVISIBLE);
        imgSpace.setVisibility(View.INVISIBLE);
        imgCabin.setVisibility(View.INVISIBLE);

        sendStationNotification(R.drawable.ic_bridge, "Командный мостик", "Смена началась. Проверьте диагностику систем.");
    }

    public void onLabClick(View view) {
        layoutBridge.setVisibility(View.INVISIBLE);
        imgLab.setVisibility(View.VISIBLE);
        imgSpace.setVisibility(View.INVISIBLE);
        imgCabin.setVisibility(View.INVISIBLE);

        sendStationNotification(R.drawable.ic_lab, "Биолаборатория", "Время снять показания с гидропонных установок.");
    }

    public void onSpacewalkClick(View view) {
        layoutBridge.setVisibility(View.INVISIBLE);
        imgLab.setVisibility(View.INVISIBLE);
        imgSpace.setVisibility(View.VISIBLE);
        imgCabin.setVisibility(View.INVISIBLE);

        sendStationNotification(R.drawable.ic_spacewalk, "Шлюзовой отсек", "Внимание: запланирован выход в открытый космос!");
    }

    public void onCabinClick(View view) {
        layoutBridge.setVisibility(View.INVISIBLE);
        imgLab.setVisibility(View.INVISIBLE);
        imgSpace.setVisibility(View.INVISIBLE);
        imgCabin.setVisibility(View.VISIBLE);

        sendStationNotification(R.drawable.ic_cabin, "Жилой модуль", "Суточный цикл завершен. Режим сна активирован.");
    }

    public void onItemComputerClick(View view) {
        Toast.makeText(this, "Главный бортовой компьютер: Все системы корабля в норме", Toast.LENGTH_SHORT).show();
    }

    public void onItemWindowClick(View view) {
        Toast.makeText(this, "Панорамный иллюминатор: Прямая видимость туманности", Toast.LENGTH_LONG).show();
    }

    public void onItemRadarClick(View view) {
        Toast.makeText(this, "Антенна дальней связи: Поступают радиосигналы с Земли", Toast.LENGTH_SHORT).show();
    }

    public void onItemSuitClick(View view) {
        Toast.makeText(this, "Скафандр EVA: Давление и запас кислорода — 100%", Toast.LENGTH_SHORT).show();
    }

    public void onItemCoffeeClick(View view) {
        Toast.makeText(this, "Термокружка: Горячий синтезированный кофе с шоколадом", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    private void sendStationNotification(int iconResId, String title, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Bitmap largeIconBitmap = getBitmapFromDrawable(iconResId);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(iconResId)
                .setLargeIcon(largeIconBitmap)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    private Bitmap getBitmapFromDrawable(int drawableResId) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableResId);
        if (drawable == null) {
            return BitmapFactory.decodeResource(getResources(), drawableResId);
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 96,
                drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 96,
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Space Station Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Оповещения систем космической станции");
            notificationManager.createNotificationChannel(channel);
        }
    }
}
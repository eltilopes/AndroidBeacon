package com.atrativa.beacon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.text.DecimalFormat;

public class MapActivity extends AppCompatActivity {

    private Bitmap imagem;
    private String nome;
    private Float distancia;
    private Float xBeacon;
    private Float yBeacon;

    class NossaTela extends View
    {    NossaTela(Context c){super(c);}
        @Override protected void onDraw(Canvas c)
        {    super.onDraw(c);
            int x = getWidth();
            int y = getHeight();
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            c.drawPaint(paint);
            desenhandoSinalAntena(c, x, y, paint);
            desenhandoMinhaPosicao( c, x, y, paint );
            desenhandoBeacon( c, x, paint );
        }

        private void desenhandoBeacon(Canvas c, int x, Paint paint) {
            paint.setColor(Color.parseColor("#3CB371"));
            c.drawCircle(xBeacon, yBeacon, x / 30, paint);
            paint.setColor(Color.parseColor("#3CB371"));
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(50);
            //c.drawText("Beacon Localizado",xBeacon + (x / 30) + 5, yBeacon, paint );
            c.drawText(nome,xBeacon + (x / 30) -5, yBeacon, paint );
            c.drawText("Distância: " + new DecimalFormat("0.00").format(distancia) + " mts." ,xBeacon + (x / 30) - 5, yBeacon + (x / 30) + 15, paint );
        }

        private void desenhandoMinhaPosicao(Canvas c, int x, int y, Paint paint) {
            paint.setColor(Color.parseColor("#CD5C5C"));
            c.drawCircle(x / 2, y / 2, x/20, paint);
            paint.setColor(Color.parseColor("#CD5C5C"));
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(50);
            c.drawText("Você",x / 2 + (x / 20) + 5, y / 2, paint );
        }
        private void desenhandoSinalAntena(Canvas c, int x, int y, Paint paint) {
            paint.setColor(Color.parseColor("#B0E0E6"));
            c.drawCircle(x / 2, y / 2, x/2, paint);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        nome = getIntent().getStringExtra("nome");
        distancia = getIntent().getFloatExtra("distancia", 0f);
        xBeacon = getIntent().getFloatExtra("x", 0f);
        yBeacon = getIntent().getFloatExtra("y", 0f);
        setContentView(new NossaTela(this));
    }

}

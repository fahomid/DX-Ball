package fahomid.com.dxball.dx_ball;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //variable, view and flags declaration
    private TextView highScore;
    private GameCanvas gameCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        highScore = findViewById(R.id.highScore);
        highScore.setText(String.valueOf(getHighScore()));
        gameCanvas = new GameCanvas(this, this);
    }

    public void startGame(View view) {
        setContentView(gameCanvas);
    }

    private int getHighScore() {
        SharedPreferences prefs = getSharedPreferences("app_data", MODE_PRIVATE);
        return prefs.getInt("highScore", 0);
    }
}

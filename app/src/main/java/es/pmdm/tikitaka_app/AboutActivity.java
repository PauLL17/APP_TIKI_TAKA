package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;
    private Button btnAbrirWeb, btnContactar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();
        setupToolbar();
        setupVideo();
        setupListeners();
    }

    private void initViews() {
        videoView = findViewById(R.id.videoView);
        btnAbrirWeb   = findViewById(R.id.btnAbrirWeb);
        btnContactar  = findViewById(R.id.btnContactar);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.about);
        }
    }

    private void setupListeners() {
        btnAbrirWeb.setOnClickListener(v -> abrirWeb());
        btnContactar.setOnClickListener(v -> contactarSoporte());
    }

    private void abrirWeb() {
        Uri webpage = Uri.parse("https://www.laliga.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
    }

    private void contactarSoporte() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"soporte@tikitaka.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_asunto));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_cuerpo));
        startActivity(intent);
    }

    private void setupVideo() {
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        videoView.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
package es.pmdm.tikitaka_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Noticia;

public class DetalleNoticiaActivity extends BaseActivity {

    private TextView tvTitulo, tvFecha, tvContenido;
    private Button btnEliminar;

    private long noticiaId;
    private Noticia noticia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_noticia);

        noticiaId = getIntent().getLongExtra("noticia_id", -1);

        initViews();
        setupToolbar();
        setupListeners();
        cargarNoticia();
    }

    private void initViews() {
        tvTitulo    = findViewById(R.id.tvTituloNoticia);
        tvFecha     = findViewById(R.id.tvFechaNoticia);
        tvContenido = findViewById(R.id.tvContenidoNoticia);
        btnEliminar = findViewById(R.id.btnEliminarNoticia);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.detalle_noticia);
        }
    }

    private void setupListeners() {
        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    private void cargarNoticia() {
        String token = SessionManager.getToken(this);

        API.getNoticia(noticiaId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                noticia = UtilJSONParser.parseNoticia(r.content);
                mostrarInfoNoticia();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(DetalleNoticiaActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }

    private void mostrarInfoNoticia() {
        tvTitulo.setText(noticia.getTitulo());
        tvFecha.setText(noticia.getFechaPublicacion());
        tvContenido.setText(noticia.getContenido());
    }

    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialogo_eliminar_noticia_titulo))
                .setMessage(getString(R.string.dialogo_eliminar_noticia_mensaje))
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    eliminarNoticia();
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void eliminarNoticia() {
        String token = SessionManager.getToken(this);

        API.deleteNoticia(noticiaId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                ToastPersonalizado.mostrarCorto(DetalleNoticiaActivity.this,
                        getString(R.string.noticia_eliminada));
                finish();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(DetalleNoticiaActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
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
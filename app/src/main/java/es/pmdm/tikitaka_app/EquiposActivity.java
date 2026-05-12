package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.pmdm.tikitaka_app.adapters.EquiposAdapter;
import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Equipo;

public class EquiposActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISO_NOTIFICACIONES = 100;

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView tvNoEquipos;
    private ListView lvEquipos;

    private EquiposAdapter adapter;
    private List<Equipo> listaEquipos = new ArrayList<>();
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipos);

        initViews();
        setupToolbar();
        setupListView();
        notificationHelper = new NotificationHelper(this);
        pedirPermisoNotificaciones();
        cargarEquipos();
    }

    private void initViews() {
        toolbar     = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        tvNoEquipos = findViewById(R.id.tvNoEquipos);
        lvEquipos   = findViewById(R.id.lvEquipos);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.equipos);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListView() {
        adapter = new EquiposAdapter(this, listaEquipos);
        lvEquipos.setAdapter(adapter);

        lvEquipos.setOnItemClickListener((parent, view, position, id) -> {
            Equipo equipo = listaEquipos.get(position);
            Intent intent = new Intent(this, DetalleEquipoActivity.class);
            intent.putExtra("equipo_id", equipo.getId());
            startActivity(intent);
        });

        registerForContextMenu(lvEquipos);
    }

    private void cargarEquipos() {
        progressBar.setVisibility(View.VISIBLE);
        lvEquipos.setVisibility(View.GONE);
        tvNoEquipos.setVisibility(View.GONE);

        String token = SessionManager.getToken(this);

        API.getEquipos(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaEquipos = UtilJSONParser.parseArrayEquipos(r.content);
                progressBar.setVisibility(View.GONE);

                if (listaEquipos.isEmpty()) {
                    tvNoEquipos.setVisibility(View.VISIBLE);
                } else {
                    lvEquipos.setVisibility(View.VISIBLE);
                    adapter = new EquiposAdapter(EquiposActivity.this, listaEquipos);
                    lvEquipos.setAdapter(adapter);
                }
            }

            @Override
            public void onError(UtilREST.Response r) {
                progressBar.setVisibility(View.GONE);
                ToastPersonalizado.mostrarError(EquiposActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_equipos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_aniadir_equipo) {
            mostrarDialogoCrearEquipo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_contextual_equipos, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == R.id.action_eliminar_equipo) {
            mostrarDialogoEliminar(info.position);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void mostrarDialogoCrearEquipo() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_equipo, null);

        EditText etNombre     = dialogView.findViewById(R.id.etNombreEquipo);
        EditText etCiudad     = dialogView.findViewById(R.id.etCiudadEquipo);
        EditText etEstadio    = dialogView.findViewById(R.id.etEstadioEquipo);
        EditText etEntrenador = dialogView.findViewById(R.id.etEntrenadorEquipo);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.aniadir_equipo))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    String nombre     = etNombre.getText().toString().trim();
                    String ciudad     = etCiudad.getText().toString().trim();
                    String estadio    = etEstadio.getText().toString().trim();
                    String entrenador = etEntrenador.getText().toString().trim();

                    if (nombre.isEmpty() || ciudad.isEmpty()) {
                        ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
                        return;
                    }

                    crearEquipo(nombre, ciudad, estadio, entrenador);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void crearEquipo(String nombre, String ciudad, String estadio, String entrenador) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("nombre", nombre);
            body.put("ciudad", ciudad);
            body.put("estadio", estadio);
            body.put("entrenador", entrenador);

            API.postEquipo(body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    ToastPersonalizado.mostrarCorto(EquiposActivity.this,
                            getString(R.string.equipo_creado));
                    notificationHelper.mostrarNotificacion(
                            getString(R.string.equipo_creado),
                            nombre,
                            NotificationHelper.NOTIF_CREAR);
                    cargarEquipos();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarError(EquiposActivity.this,
                            getString(R.string.error_cargar_datos));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarDialogoEliminar(int position) {
        Equipo equipo = listaEquipos.get(position);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialogo_eliminar_equipo_titulo))
                .setMessage(getString(R.string.dialogo_eliminar_equipo_mensaje))
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    eliminarEquipo(equipo, position);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void eliminarEquipo(Equipo equipo, int position) {
        String token = SessionManager.getToken(this);

        API.deleteEquipo(equipo.getId(), token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                ToastPersonalizado.mostrarCorto(EquiposActivity.this,
                        getString(R.string.equipo_eliminado));
                notificationHelper.mostrarNotificacion(
                        getString(R.string.equipo_eliminado),
                        equipo.getNombre(),
                        NotificationHelper.NOTIF_ELIMINAR);
                listaEquipos.remove(position);
                adapter = new EquiposAdapter(EquiposActivity.this, listaEquipos);
                lvEquipos.setAdapter(adapter);
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(EquiposActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }

    private void pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        CODIGO_PERMISO_NOTIFICACIONES);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISO_NOTIFICACIONES) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ToastPersonalizado.mostrarError(this,
                        getString(R.string.permiso_notificaciones_denegado));
            }
        }
    }
}
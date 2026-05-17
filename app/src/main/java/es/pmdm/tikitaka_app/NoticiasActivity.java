package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.pmdm.tikitaka_app.adapters.NoticiasAdapter;
import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Equipo;
import es.pmdm.tikitaka_app.modelos.Noticia;

public class NoticiasActivity extends BaseActivity {

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView tvNoNoticias;
    private ListView lvNoticias;

    private NoticiasAdapter adapter;
    private List<Noticia> listaNoticias = new ArrayList<>();
    private List<Equipo> listaEquipos   = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);

        initViews();
        setupToolbar();
        setupListView();
        cargarEquipos();
        cargarUltimasNoticias();
    }

    private void initViews() {
        toolbar      = findViewById(R.id.toolbar);
        progressBar  = findViewById(R.id.progressBar);
        tvNoNoticias = findViewById(R.id.tvNoNoticias);
        lvNoticias   = findViewById(R.id.lvNoticias);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.noticias);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListView() {
        adapter = new NoticiasAdapter(this, listaNoticias);
        lvNoticias.setAdapter(adapter);

        lvNoticias.setOnItemClickListener((parent, view, position, id) -> {
            Noticia noticia = listaNoticias.get(position);
            Intent intent = new Intent(this, DetalleNoticiaActivity.class);
            intent.putExtra("noticia_id", noticia.getId());
            startActivity(intent);
        });

        registerForContextMenu(lvNoticias);
    }

    private void cargarEquipos() {
        String token = SessionManager.getToken(this);

        API.getEquipos(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaEquipos = UtilJSONParser.parseArrayEquipos(r.content);
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(NoticiasActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }

    private void cargarUltimasNoticias() {
        mostrarCargando();
        String token = SessionManager.getToken(this);

        API.getUltimasNoticias(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaNoticias = UtilJSONParser.parseArrayNoticias(r.content);
                mostrarNoticias();
            }

            @Override
            public void onError(UtilREST.Response r) {
                progressBar.setVisibility(View.GONE);
                ToastPersonalizado.mostrarError(NoticiasActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }

    private void cargarNoticiasMiEquipo() {
        mostrarCargando();
        String token = SessionManager.getToken(this);
        long equipoFavoritoId = SessionManager.getEquipoFavoritoId(this);

        if (equipoFavoritoId == -1) {
            progressBar.setVisibility(View.GONE);
            ToastPersonalizado.mostrarError(this, getString(R.string.no_equipo_favorito));
            return;
        }

        API.getNoticiasByEquipo(equipoFavoritoId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaNoticias = UtilJSONParser.parseArrayNoticias(r.content);
                mostrarNoticias();
            }

            @Override
            public void onError(UtilREST.Response r) {
                progressBar.setVisibility(View.GONE);
                ToastPersonalizado.mostrarError(NoticiasActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }

    private void mostrarCargando() {
        progressBar.setVisibility(View.VISIBLE);
        lvNoticias.setVisibility(View.GONE);
        tvNoNoticias.setVisibility(View.GONE);
    }

    private void mostrarNoticias() {
        progressBar.setVisibility(View.GONE);

        if (listaNoticias.isEmpty()) {
            tvNoNoticias.setVisibility(View.VISIBLE);
        } else {
            lvNoticias.setVisibility(View.VISIBLE);
            adapter = new NoticiasAdapter(this, listaNoticias);
            lvNoticias.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_noticias, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_todas) {
            cargarUltimasNoticias();
            return true;
        }
        if (item.getItemId() == R.id.action_mi_equipo) {
            cargarNoticiasMiEquipo();
            return true;
        }
        if (item.getItemId() == R.id.action_crear_noticia) {
            mostrarDialogoCrearNoticia();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoCrearNoticia() {
        if (listaEquipos.isEmpty()) {
            ToastPersonalizado.mostrarError(this, getString(R.string.error_cargar_datos));
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_noticia, null);

        EditText etTitulo    = dialogView.findViewById(R.id.etTituloNoticia);
        EditText etContenido = dialogView.findViewById(R.id.etContenidoNoticia);
        Spinner  spEquipo    = dialogView.findViewById(R.id.spinnerEquipoNoticia);

        List<String> nombresEquipos = new ArrayList<>();
        for (Equipo equipo : listaEquipos) {
            nombresEquipos.add(equipo.getNombre());
        }

        ArrayAdapter<String> adapterEquipos = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombresEquipos);
        adapterEquipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEquipo.setAdapter(adapterEquipos);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.crear_noticia))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    String titulo    = etTitulo.getText().toString().trim();
                    String contenido = etContenido.getText().toString().trim();

                    if (titulo.isEmpty() || contenido.isEmpty()) {
                        ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
                        return;
                    }

                    Equipo equipoSeleccionado = listaEquipos.get(spEquipo.getSelectedItemPosition());
                    crearNoticia(titulo, contenido, equipoSeleccionado.getId());
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void crearNoticia(String titulo, String contenido, long equipoId) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("titulo", titulo);
            body.put("contenido", contenido);
            body.put("equipoId", equipoId);

            API.postNoticia(body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    ToastPersonalizado.mostrarCorto(NoticiasActivity.this,
                            getString(R.string.noticia_creada));
                    new NotificationHelper(NoticiasActivity.this).mostrarNotificacion(
                            getString(R.string.noticia_creada),
                            titulo,
                            NotificationHelper.NOTIF_CREAR);
                    cargarUltimasNoticias();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarError(NoticiasActivity.this,
                            getString(R.string.error_cargar_datos));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_contextual_noticias, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == R.id.action_eliminar_noticia) {
            mostrarDialogoEliminar(info.position);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void mostrarDialogoEliminar(int position) {
        Noticia noticia = listaNoticias.get(position);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialogo_eliminar_noticia_titulo))
                .setMessage(getString(R.string.dialogo_eliminar_noticia_mensaje))
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    eliminarNoticia(noticia, position);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void eliminarNoticia(Noticia noticia, int position) {
        String token = SessionManager.getToken(this);

        API.deleteNoticia(noticia.getId(), token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                ToastPersonalizado.mostrarCorto(NoticiasActivity.this,
                        getString(R.string.noticia_eliminada));
                listaNoticias.remove(position);
                adapter = new NoticiasAdapter(NoticiasActivity.this, listaNoticias);
                lvNoticias.setAdapter(adapter);

                new NotificationHelper(NoticiasActivity.this).mostrarNotificacion(
                        getString(R.string.noticia_eliminada),
                        noticia.getTitulo(),
                        NotificationHelper.NOTIF_ELIMINAR);
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(NoticiasActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }
}
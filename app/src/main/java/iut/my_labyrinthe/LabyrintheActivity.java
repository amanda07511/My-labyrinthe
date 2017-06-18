package iut.my_labyrinthe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import iut.my_labyrinthe.models.Boule;
import iut.my_labyrinthe.models.LabyrintheView;
import iut.my_labyrinthe.models.Bloc;
import iut.my_labyrinthe.models.MoteurPhysique;
import iut.my_labyrinthe.models.Sounds;

/**
 * Created by amanda on 10/04/2017.
 */
public class LabyrintheActivity extends Activity {

    // Identifiant de la boîte de dialogue de victoire
    public static final int VICTORY_DIALOG = 0;
    // Identifiant de la boîte de dialogue de défaite
    public static final int DEFEAT_DIALOG = 1;

    // Le moteur graphique du jeu
    private LabyrintheView mView = null;
    // Le moteur physique du jeu
    private MoteurPhysique mEngine = null;

    public Sounds theme;

    String fileName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("file");

        mView = new LabyrintheView(this);
        setContentView(mView);

        mEngine = new MoteurPhysique(this);

        Boule b = new Boule();
        mView.setBoule(b);
        mEngine.setBoule(b);

        List<Bloc> mList = mEngine.buildLabyrinthe(fileName);
        mView.setBlocks(mList);

        theme = new Sounds(this);
        theme.play_mp(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mEngine.resume();
        theme.play_mp(this);
    }

    @Override
    protected void onPause() {
        super.onStop();
        mEngine.stop();
        if (theme != null) {
            theme.pause_mp();
        }
    }

    public void explosion() {
        theme.play_sp();
    }

    @Override
    public Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case VICTORY_DIALOG:
                builder.setCancelable(false)
                        .setMessage("Bravo, vous avez gagné !")
                        .setTitle("Champion ! Le roi des Zörglubienotchs est mort grâce à vous !")
                        .setNeutralButton("Recommencer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // L'utilisateur peut recommencer s'il le veut
                                mEngine.reset();
                                mEngine.resume();
                            }
                        });
                break;

            case DEFEAT_DIALOG:

                builder.setCancelable(false)
                        .setMessage("La Terre a été détruite à cause de vos erreurs.")
                        .setTitle("Bah bravo !")
                        .setNeutralButton("Recommencer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEngine.reset();
                                mEngine.resume();
                            }
                        })
                        .setNegativeButton("Returner a l'accueil", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEngine.stop();
                                theme.stop_mp();
                                Intent i = new Intent(LabyrintheActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });
        }
        return builder.create();
    }

    @Override
    public void onPrepareDialog(int id, Dialog box) {
        // A chaque fois qu'une boîte de dialogue est lancée, on arrête le moteur physique
        mEngine.stop();
    }

}

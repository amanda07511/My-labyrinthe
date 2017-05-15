package iut.my_labyrinthe.models;

import android.app.Service;
import android.graphics.Color;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
//import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import iut.my_labyrinthe.LabyrintheActivity;
import iut.my_labyrinthe.R;
import iut.my_labyrinthe.models.Bloc.Type;

/**
 * Created by amanda on 10/04/2017.
 */

public class MoteurPhysique {
    //Le Boule
    private Boule mBoule = null;
    public Boule getBoule() {
        return mBoule;
    }
    public void setBoule(Boule pBoule) {
        this.mBoule = pBoule;
    }

    // Le labyrinthe
    private List<Bloc> mBlocks = null;
    private LabyrintheActivity mActivity = null;

    //Sensors
    private SensorManager mManager = null;
    private Sensor mAccelerometre = null;
    private Sensor mLight = null;
    private Sensor mMagnetometer = null;



    SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent pEvent) {

            if (pEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
                float lux = pEvent.values[0];

                if (lux > 50 && lux < 100) {
                    LabyrintheView.color = mActivity.getResources().getColor(R.color.medium);
                    LabyrintheView.colorTrou = mActivity.getResources().getColor(R.color.black_medium);
                } else if (lux > 100) {
                    LabyrintheView.color = mActivity.getResources().getColor(R.color.light);
                    LabyrintheView.colorTrou = Color.BLACK;
                } else if (lux < 50 && lux > 0) {
                    LabyrintheView.color = mActivity.getResources().getColor(R.color.darkness);
                    LabyrintheView.colorTrou = mActivity.getResources().getColor(R.color.gray);
                } else {
                    LabyrintheView.color = mActivity.getResources().getColor(R.color.dark);
                    LabyrintheView.colorTrou = mActivity.getResources().getColor(R.color.black_light);
                }


            }
            if (pEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                float x = pEvent.values[0];
                float y = pEvent.values[1];
                float z = pEvent.values[2];

                if (mBoule != null) {
                    // On met à jour les coordonnées de la Boule
                    RectF hitBox = mBoule.putXAndY(x, y);

                    // Pour tous les blocs du labyrinthe
                    for (Bloc block : mBlocks) {
                        // On crée un nouveau rectangle pour ne pas modifier celui du Bloc
                        RectF inter = new RectF(block.getRectangle());
                        if (inter.intersect(hitBox)) {
                            // On agit différement en fonction du type de Bloc
                            switch (block.getType()) {
                                case TROU:
                                    mActivity.showDialog(LabyrintheActivity.DEFEAT_DIALOG);
                                    break;

                                case DEPART:
                                    break;

                                case ARRIVEE:
                                    mActivity.showDialog(LabyrintheActivity.VICTORY_DIALOG);
                                    break;
                            }
                            break;
                        }
                    }
                }


            }//End if

            if (pEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                float x = pEvent.values[0];
                float y = pEvent.values[1];
                float z = pEvent.values[2];
                if (x > 0 && y > 0 && z > 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color1));
                } else if (x > 0 && y > 0 && z < 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color2));
                } else if (x > 0 && y < 0 && z > 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color3));
                } else if (x > 0 && y < 0 && z < 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color4));
                } else if (x < 0 && y > 0 && z > 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color5));
                } else if (x < 0 && y > 0 && z < 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color6));
                } else if (x < 0 && y < 0 && z > 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color7));
                } else if (x < 0 && y < 0 && z < 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color8));
                } else {
                    mBoule.setCouleur(Color.GREEN);
                }

            }


        }//onSensorChanged

        @Override
        public void onAccuracyChanged(Sensor pSensor, int pAccuracy) {

        }
    };

    public MoteurPhysique(LabyrintheActivity pView) {
        mActivity = pView;
        mManager = (SensorManager) mActivity.getBaseContext().getSystemService(Service.SENSOR_SERVICE);
        mAccelerometre = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLight = mManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mMagnetometer = mManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }


    // Remet à zéro l'emplacement de la Boule
    public void reset() {
        mBoule.reset();

    }

    // Arrête le capteur
    public void stop() {
        mManager.unregisterListener(mSensorEventListener, mAccelerometre);
        mManager.unregisterListener(mSensorEventListener, mLight);
        mManager.unregisterListener(mSensorEventListener, mMagnetometer);

    }

    // Redémarre le capteur
    public void resume() {
        mManager.registerListener(mSensorEventListener, mAccelerometre, SensorManager.SENSOR_DELAY_GAME);
        mManager.registerListener(mSensorEventListener, mLight , SensorManager.SENSOR_DELAY_GAME);
        mManager.registerListener(mSensorEventListener, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    // Construit le labyrinthe
    public List<Bloc> buildLabyrinthe() {
        mBlocks = new ArrayList<Bloc>();
        mBlocks.add(new Bloc(Type.TROU, 0, 0));
        mBlocks.add(new Bloc(Type.TROU, 0, 1));
        mBlocks.add(new Bloc(Type.TROU, 0, 2));
        mBlocks.add(new Bloc(Type.TROU, 0, 3));
        mBlocks.add(new Bloc(Type.TROU, 0, 4));
        mBlocks.add(new Bloc(Type.TROU, 0, 5));
        mBlocks.add(new Bloc(Type.TROU, 0, 6));
        mBlocks.add(new Bloc(Type.TROU, 0, 7));
        mBlocks.add(new Bloc(Type.TROU, 0, 8));
        mBlocks.add(new Bloc(Type.TROU, 0, 9));
        mBlocks.add(new Bloc(Type.TROU, 0, 10));
        mBlocks.add(new Bloc(Type.TROU, 0, 11));
        mBlocks.add(new Bloc(Type.TROU, 0, 12));
        mBlocks.add(new Bloc(Type.TROU, 0, 13));

        mBlocks.add(new Bloc(Type.TROU, 1, 0));
        mBlocks.add(new Bloc(Type.TROU, 1, 13));

        mBlocks.add(new Bloc(Type.TROU, 2, 0));
        mBlocks.add(new Bloc(Type.TROU, 2, 13));

        mBlocks.add(new Bloc(Type.TROU, 3, 0));
        mBlocks.add(new Bloc(Type.TROU, 3, 13));

        mBlocks.add(new Bloc(Type.TROU, 4, 0));
        mBlocks.add(new Bloc(Type.TROU, 4, 1));
        mBlocks.add(new Bloc(Type.TROU, 4, 2));
        mBlocks.add(new Bloc(Type.TROU, 4, 3));
        mBlocks.add(new Bloc(Type.TROU, 4, 4));
        mBlocks.add(new Bloc(Type.TROU, 4, 5));
        mBlocks.add(new Bloc(Type.TROU, 4, 6));
        mBlocks.add(new Bloc(Type.TROU, 4, 7));
        mBlocks.add(new Bloc(Type.TROU, 4, 8));
        mBlocks.add(new Bloc(Type.TROU, 4, 9));
        mBlocks.add(new Bloc(Type.TROU, 4, 13));

        mBlocks.add(new Bloc(Type.TROU, 5, 0));
        mBlocks.add(new Bloc(Type.TROU, 5, 13));

        mBlocks.add(new Bloc(Type.TROU, 6, 0));
        mBlocks.add(new Bloc(Type.TROU, 6, 13));

        mBlocks.add(new Bloc(Type.TROU, 7, 0));
        mBlocks.add(new Bloc(Type.TROU, 7, 13));

        mBlocks.add(new Bloc(Type.TROU, 8, 0));
        mBlocks.add(new Bloc(Type.TROU, 8, 1));
        mBlocks.add(new Bloc(Type.TROU, 8, 2));
        mBlocks.add(new Bloc(Type.TROU, 8, 5));
        mBlocks.add(new Bloc(Type.TROU, 8, 6));
        mBlocks.add(new Bloc(Type.TROU, 8, 9));
        mBlocks.add(new Bloc(Type.TROU, 8, 10));
        mBlocks.add(new Bloc(Type.TROU, 8, 11));
        mBlocks.add(new Bloc(Type.TROU, 8, 12));
        mBlocks.add(new Bloc(Type.TROU, 8, 13));

        mBlocks.add(new Bloc(Type.TROU, 9, 0));
        mBlocks.add(new Bloc(Type.TROU, 9, 5));
        mBlocks.add(new Bloc(Type.TROU, 9, 9));
        mBlocks.add(new Bloc(Type.TROU, 9, 13));

        mBlocks.add(new Bloc(Type.TROU, 10, 0));
        mBlocks.add(new Bloc(Type.TROU, 10, 5));
        mBlocks.add(new Bloc(Type.TROU, 10, 9));
        mBlocks.add(new Bloc(Type.TROU, 10, 13));

        mBlocks.add(new Bloc(Type.TROU, 11, 0));
        mBlocks.add(new Bloc(Type.TROU, 11, 5));
        mBlocks.add(new Bloc(Type.TROU, 11, 9));
        mBlocks.add(new Bloc(Type.TROU, 11, 13));

        mBlocks.add(new Bloc(Type.TROU, 12, 0));
        mBlocks.add(new Bloc(Type.TROU, 12, 5));
        mBlocks.add(new Bloc(Type.TROU, 12, 9));
        mBlocks.add(new Bloc(Type.TROU, 12, 13));

        mBlocks.add(new Bloc(Type.TROU, 13, 0));
        mBlocks.add(new Bloc(Type.TROU, 13, 1));
        mBlocks.add(new Bloc(Type.TROU, 13, 2));
        mBlocks.add(new Bloc(Type.TROU, 13, 3));
        mBlocks.add(new Bloc(Type.TROU, 13, 4));
        mBlocks.add(new Bloc(Type.TROU, 13, 5));
        mBlocks.add(new Bloc(Type.TROU, 13, 9));
        mBlocks.add(new Bloc(Type.TROU, 13, 8));
        mBlocks.add(new Bloc(Type.TROU, 13, 13));

        mBlocks.add(new Bloc(Type.TROU, 14, 0));
        mBlocks.add(new Bloc(Type.TROU, 14, 8));
        mBlocks.add(new Bloc(Type.TROU, 14, 13));

        mBlocks.add(new Bloc(Type.TROU, 15, 0));
        mBlocks.add(new Bloc(Type.TROU, 15, 8));
        mBlocks.add(new Bloc(Type.TROU, 15, 13));

        mBlocks.add(new Bloc(Type.TROU, 16, 0));
        mBlocks.add(new Bloc(Type.TROU, 16, 8));
        mBlocks.add(new Bloc(Type.TROU, 16, 13));

        mBlocks.add(new Bloc(Type.TROU, 17, 0));
        mBlocks.add(new Bloc(Type.TROU, 17, 4));
        mBlocks.add(new Bloc(Type.TROU, 17, 5));
        mBlocks.add(new Bloc(Type.TROU, 17, 6));
        mBlocks.add(new Bloc(Type.TROU, 17, 7));
        mBlocks.add(new Bloc(Type.TROU, 17, 8));
        mBlocks.add(new Bloc(Type.TROU, 17, 9));
        mBlocks.add(new Bloc(Type.TROU, 17, 13));

        mBlocks.add(new Bloc(Type.TROU, 18, 0));
        mBlocks.add(new Bloc(Type.TROU, 18, 13));

        mBlocks.add(new Bloc(Type.TROU, 19, 0));
        mBlocks.add(new Bloc(Type.TROU, 19, 13));

        mBlocks.add(new Bloc(Type.TROU, 20, 0));
        mBlocks.add(new Bloc(Type.TROU, 20, 1));
        mBlocks.add(new Bloc(Type.TROU, 20, 2));
        mBlocks.add(new Bloc(Type.TROU, 20, 3));
        mBlocks.add(new Bloc(Type.TROU, 20, 4));
        mBlocks.add(new Bloc(Type.TROU, 20, 5));
        mBlocks.add(new Bloc(Type.TROU, 20, 6));
        mBlocks.add(new Bloc(Type.TROU, 20, 7));
        mBlocks.add(new Bloc(Type.TROU, 20, 8));
        mBlocks.add(new Bloc(Type.TROU, 20, 9));
        mBlocks.add(new Bloc(Type.TROU, 20, 10));
        mBlocks.add(new Bloc(Type.TROU, 20, 11));
        mBlocks.add(new Bloc(Type.TROU, 20, 12));
        mBlocks.add(new Bloc(Type.TROU, 20, 13));

        Bloc b = new Bloc(Type.DEPART, 2, 2);
        mBoule.setInitialRectangle(new RectF(b.getRectangle()));
        mBlocks.add(b);

        mBlocks.add(new Bloc(Type.ARRIVEE, 10, 11));

        return mBlocks;
    }


}

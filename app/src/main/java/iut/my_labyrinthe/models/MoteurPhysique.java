package iut.my_labyrinthe.models;

import java.util.List;

import iut.my_labyrinthe.LabyrintheActivity;
import iut.my_labyrinthe.R;
import android.app.Service;
import android.graphics.Color;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class MoteurPhysique {
    // Le Boule
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
    private CreateLabyrinthe mLabyrinthe = null;

    // Sensors
    private SensorManager mManager;
    private Sensor mAccelerometre = null;
    private Sensor mMagnetometer = null;
    private Sensor mLigth = null;


    SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent pEvent) {

            if (pEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = pEvent.values[0];
                float y = pEvent.values[1];

                if (mBoule != null) {
                    // On met à jour les coordonnées de la boule
                    RectF hitBox = mBoule.putXAndY(x, y);
                    Log.d("hitbox!  ", "X = " + x + "Y = " + y);
                    // Pour tous les blocs du labyrinthe
                    for (Bloc block : mBlocks) {
                        // On crée un nouveau rectangle pour ne pas modifier celui du bloc
                        RectF inter = new RectF(block.getRectangle());
                        if (inter.intersect(hitBox)) {
                            // On agit différement en fonction du type de bloc
                            switch (block.getType()) {
                                case TROU:
                                    mActivity.explosion();
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
            }

            if (pEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                float x1 = pEvent.values[0];
                float y1 = pEvent.values[1];
                float z1 = pEvent.values[2];

                if (x1 > 0 && y1 > 0 && z1 > 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color1));
                } else if (x1 > 0 && y1 > 0 && z1 < 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color2));
                } else if (x1 > 0 && y1 < 0 && z1 > 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color3));
                } else if (x1 > 0 && y1 < 0 && z1 < 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color4));
                } else if (x1 < 0 && y1 > 0 && z1 > 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color5));
                } else if (x1 < 0 && y1 > 0 && z1 < 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color6));
                } else if (x1 < 0 && y1 < 0 && z1 > 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color7));
                } else if (x1 < 0 && y1 < 0 && z1 < 0) {
                    mBoule.setCouleur(mActivity.getResources().getColor(R.color.color8));
                } else {
                    mBoule.setCouleur(Color.GREEN);
                }

            }

            if (pEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
                float lux = pEvent.values[0];

                if (lux > 50 && lux < 100) {
                    LabyrintheView.mCouleur = mActivity.getResources().getColor(R.color.medium);
                    LabyrintheView.mCouleurTrou = mActivity.getResources().getColor(R.color.black_medium);
                } else if (lux > 100) {
                    LabyrintheView.mCouleur = mActivity.getResources().getColor(R.color.light);
                    LabyrintheView.mCouleurTrou = Color.BLACK;
                } else if (lux < 50 && lux > 0) {
                    LabyrintheView.mCouleur = mActivity.getResources().getColor(R.color.darkness);
                    LabyrintheView.mCouleurTrou = mActivity.getResources().getColor(R.color.gray);
                } else {
                    LabyrintheView.mCouleur = mActivity.getResources().getColor(R.color.dark);
                    LabyrintheView.mCouleurTrou = mActivity.getResources().getColor(R.color.black_light);
                }

            }

        }

        @Override
        public void onAccuracyChanged(Sensor pSensor, int pAccuracy) {

        }
    };

    public MoteurPhysique(LabyrintheActivity pView) {
        mActivity = pView;
        mManager = (SensorManager) mActivity.getBaseContext().getSystemService(Service.SENSOR_SERVICE);
        mAccelerometre = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mLigth = mManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    // Remet à zéro l'emplacement de la boule
    public void reset() {
        mBoule.reset();
    }

    // Arrête le capteur
    public void stop() {
        mManager.unregisterListener(mSensorEventListener, mAccelerometre);
        mManager.unregisterListener(mSensorEventListener, mMagnetometer);
        mManager.unregisterListener(mSensorEventListener, mLigth);
    }

    // Redémarre le capteur
    public void resume() {
        mManager.registerListener(mSensorEventListener, mAccelerometre, SensorManager.SENSOR_DELAY_GAME);
        mManager.registerListener(mSensorEventListener, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        mManager.registerListener(mSensorEventListener, mLigth, SensorManager.SENSOR_DELAY_GAME);
    }

    // Construit le labyrinthe
    public List<Bloc> buildLabyrinthe(String fileName) {
        mLabyrinthe = new CreateLabyrinthe(fileName, mActivity);
        mBlocks = mLabyrinthe.getLabyrinthe();



        Bloc b = mLabyrinthe.getDepart();
        mBoule.setInitialRectangle(new RectF(b.getRectangle()));



        return mBlocks;
    }

}

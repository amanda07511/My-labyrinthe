package iut.my_labyrinthe.models;

import android.graphics.RectF;

/**
 * Created by amanda on 10/04/2017.
 */

public class Bloc {

    public enum  Type { TROU, DEPART, ARRIVEE };

    private float size = Boule.RAYON * 2;

    public Type mType = null;
    private RectF mRectangle = null;

    public Type getType() {
        return mType;
    }

    public RectF getRectangle() {
        return mRectangle;
    }

    public Bloc(Type pType, int pX, int pY) {
        this.mType = pType;
        this.mRectangle = new RectF(pX * size, pY * size, (pX + 1) * size, (pY + 1) * size);
    }


}

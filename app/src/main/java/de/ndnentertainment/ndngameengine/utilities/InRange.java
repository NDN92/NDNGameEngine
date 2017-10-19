package de.ndnentertainment.ndngameengine.utilities;

/**
 * Created by nickn on 04.10.2017.
 */

public abstract class InRange {

    public static boolean check(float[] outer, float[] inner, boolean partiallyInRange) {
        if(outer.length < 2 || inner.length < 1) {
            throw new Error("Error!");
        }
        float outerMin = Float.POSITIVE_INFINITY;
        float outerMax = Float.NEGATIVE_INFINITY;
        for(float n : outer) {
            outerMin = n < outerMin ? n : outerMin;
            outerMax = n > outerMax ? n : outerMax;
        }

        //Punkt in Range
        if(inner.length == 1) {
            return outerMin <= inner[0] && inner[0] <= outerMax;
        }

        //Bereich in Range
        float innerMin = Float.POSITIVE_INFINITY;
        float innerMax = Float.NEGATIVE_INFINITY;
        for(float n : inner) {
            innerMin = n < innerMin ? n : innerMin;
            innerMax = n > innerMax ? n : innerMax;
        }

        //Wenn nur ein Teil des Bereiches in der Range liegen muss
        if(partiallyInRange) {
            return (outerMin <= innerMin && innerMin <= outerMax)
                    || (outerMin <= innerMax && innerMax <= outerMax);
        }

        //Wenn der komplette Bereich in der Range liegen muss
        return outerMin <= innerMin && innerMax <= outerMax;
    }

}

package pl.edu.platinum.archiet.jchess3man.engine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Created by Michał Krzysztof Feiler on 25.01.17.
 */
public abstract class ContinuousVector implements Vector, KingVector {
    public final int abs;

    ContinuousVector(int abs) {
        assert (abs > 0);
        this.abs = abs;
    }

    @Override
    public boolean toBool() {
        return abs > 0;
    }

    /**
     * head unit ContinuousVector
     *
     * @param fromrank is the rank of the from Pos
     * @return head unit ContinuousVector
     */
    public abstract @NotNull ContinuousVector head(int fromrank);

    public abstract @Nullable ContinuousVector tail(int fromrank);

    public boolean isUnit() {
        return abs <= 1;
    }

    Iterable<ContinuousVector> unitsContinuous(int fromRank) {
        return () -> new Iterator<ContinuousVector>() {
            private boolean headRemaining = false;
            private ContinuousVector curHead = head(fromRank);
            private Vector curTail = tail(fromRank);
            private int ourFromRank = fromRank;

            @Override
            public boolean hasNext() {
                return curTail != null || headRemaining;
            }

            @Override
            public ContinuousVector next() {
                ourFromRank += curHead.rank();
                ContinuousVector toReturn = curHead;
                if (curTail instanceof ContinuousVector) {
                    ContinuousVector theTail = ((ContinuousVector) curTail);
                    curHead = theTail.head(ourFromRank);
                    curTail = theTail.tail(ourFromRank);
                    if (curTail == null) headRemaining = true;
                } else if (curTail == null) {
                    headRemaining = false;
                    curHead = null;
                }
                return toReturn;
            }
        };
    }

    public Iterable<ContinuousVector> units(int fromRank) {
        return unitsContinuous(fromRank);
    }

    @Override
    public Iterable<Pos> emptiesFrom(Pos from) {
        return emptiesBetween(from);
    }

    public Iterable<Pos> emptiesBetween(Pos from) {
        return () -> new Iterator<Pos>() {
            private Pos pos = from;
            private final Iterator<ContinuousVector> it =
                    unitsContinuous(from.rank).iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Pos next() {
                try {
                    pos = pos.addVec(it.next());
                } catch (VectorAdditionFailedException e) {
                    e.printStackTrace();
                }
                return pos;
            }
        };
    }
}

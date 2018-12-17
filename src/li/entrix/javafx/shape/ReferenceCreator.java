package li.entrix.javafx.shape;

import java.awt.geom.Point2D;
import java.lang.ref.Reference;

public interface ReferenceCreator {

    Reference<?> createReference(Runnable run);

    void cleaAll();
}

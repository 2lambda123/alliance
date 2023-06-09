/**
 * Copyright (c) Codice Foundation
 * <p/>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.video.stream.mpegts.metacard;

import ddf.catalog.data.Metacard;

public interface MetacardUpdater {

    void update(Metacard parent, Metacard child);

    void accept(Visitor visitor);

    interface Visitor {

        void visit(FrameCenterMetacardUpdater frameCenterMetacardUpdater);

        void visit(LineStringMetacardUpdater lineStringMetacardUpdater);

        void visit(LocationMetacardUpdater locationMetacardUpdater);

        void visit(ModifiedDateMetacardUpdater modifiedDateMetacardUpdater);

        void visit(TemporalEndMetacardUpdater temporalEndMetacardUpdater);

        void visit(TemporalStartMetacardUpdater temporalStartMetacardUpdater);
    }

}

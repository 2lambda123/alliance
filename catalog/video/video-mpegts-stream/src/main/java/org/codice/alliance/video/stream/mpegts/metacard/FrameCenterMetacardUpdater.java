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

import org.codice.alliance.libs.klv.AttributeNameConstants;
import org.codice.alliance.libs.klv.GeometryOperator;

public class FrameCenterMetacardUpdater extends LineStringMetacardUpdater {
    public FrameCenterMetacardUpdater(GeometryOperator geometryOperator) {
        super(AttributeNameConstants.FRAME_CENTER, geometryOperator);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "FrameCenterMetacardUpdater{} " + super.toString();
    }
}

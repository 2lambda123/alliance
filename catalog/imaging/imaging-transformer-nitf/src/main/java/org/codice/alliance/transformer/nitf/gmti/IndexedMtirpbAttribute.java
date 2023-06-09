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
package org.codice.alliance.transformer.nitf.gmti;

import java.io.Serializable;
import java.util.function.Function;

import org.codice.alliance.transformer.nitf.common.NitfAttribute;
import org.codice.alliance.transformer.nitf.common.TreUtility;
import org.codice.imaging.nitf.core.tre.TreGroup;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;

enum IndexedMtirpbAttribute implements NitfAttribute<TreGroup> {
    INDEXED_TARGET_CLASSIFICATION_CATEGORY("target-classification-category",
            "TGT_CAT",
            tre -> getClassificationCategory(tre)),
    INDEXED_TARGET_AMPLITUDE("target-amplitude",
            "TGT_AMPLITUDE",
            tre -> TreUtility.getTreValue(tre, "TGT_AMPLITUDE")),
    INDEXED_TARGET_HEADING("target-heading",
            "TGT_HEADING",
            tre -> TreUtility.getTreValue(tre, "TGT_HEADING")),
    INDEXED_TARGET_GROUND_SPEED("target-ground-speed",
            "TGT_SPEED",
            tre -> TreUtility.getTreValue(tre, "TGT_SPEED")),
    INDEXED_TARGET_RADIAL_VELOCITY("target-radial-velocity",
            "TGT_VEL_R",
            tre -> TreUtility.getTreValue(tre, "TGT_VEL_R")),
    INDEXED_TARGET_LOCATION_ACCURACY("target-location-accuracy",
            "TGT_LOC_ACCY",
            tre -> TreUtility.getTreValue(tre, "TGT_LOC_ACCY")),
    INDEXED_TARGET_LOCATION("target-location",
            "TGT_LOC",
            tre -> TreUtility.getTreValue(tre, "TGT_LOC"));

    private static final String ATTRIBUTEAME_PREFIX = "nitf.mtirpb.";

    private String shortName;

    private String longName;

    private Function<TreGroup, Serializable> accessorFunction;

    private AttributeDescriptor attributeDescriptor;

    IndexedMtirpbAttribute(String longName, String shortName,
            Function<TreGroup, Serializable> accessorFunction) {
        this.longName = longName;
        this.shortName = shortName;
        this.accessorFunction = accessorFunction;
        this.attributeDescriptor = new AttributeDescriptorImpl(ATTRIBUTEAME_PREFIX + longName,
                true,
                true,
                false,
                true,
                BasicTypes.STRING_TYPE);
    }

    @Override
    public String getLongName() {
        return this.longName;
    }

    @Override
    public String getShortName() {
        return this.shortName;
    }

    @Override
    public Function<TreGroup, Serializable> getAccessorFunction() {
        return this.accessorFunction;
    }

    @Override
    public AttributeDescriptor getAttributeDescriptor() {
        return this.attributeDescriptor;
    }

    private static String getClassificationCategory(TreGroup treGroup) {
        Serializable value = TreUtility.getTreValue(treGroup,
                INDEXED_TARGET_CLASSIFICATION_CATEGORY.getShortName());

        if (value == null) {
            return MtiTargetClassificationCategory.U.getLongName();
        }

        return MtiTargetClassificationCategory.valueOf((String) value)
                .getLongName();
    }
}

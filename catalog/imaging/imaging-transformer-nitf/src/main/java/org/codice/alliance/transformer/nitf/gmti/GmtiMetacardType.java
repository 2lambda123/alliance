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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.alliance.transformer.nitf.common.AcftbAttribute;
import org.codice.alliance.transformer.nitf.common.NitfAttribute;
import org.codice.alliance.transformer.nitf.common.NitfHeaderAttribute;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.MetacardTypeImpl;

/**
 * Defines the Gmti Metacard type.
 */
public class GmtiMetacardType extends MetacardTypeImpl {
    private static final String GMTI = "gmti";

    /**
     * Default Constructor
     */
    public GmtiMetacardType() {
        super(GMTI, (Set<AttributeDescriptor>) null);
        getDescriptors();
    }

    private void getDescriptors() {
        descriptors.addAll(BasicTypes.BASIC_METACARD.getAttributeDescriptors());
        descriptors.addAll(getDescriptors(NitfHeaderAttribute.values()));
        descriptors.addAll(getDescriptors(AcftbAttribute.values()));
        descriptors.addAll(getDescriptors(IndexedMtirpbAttribute.values()));
    }

    private List<AttributeDescriptor> getDescriptors(NitfAttribute[] attributes) {
        return Stream.of(attributes)
                .map(NitfAttribute::getAttributeDescriptor)
                .collect(Collectors.toList());
    }
}

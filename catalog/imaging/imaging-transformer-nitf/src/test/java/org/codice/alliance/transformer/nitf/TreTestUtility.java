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
package org.codice.alliance.transformer.nitf;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.function.Consumer;

import org.codice.imaging.nitf.core.RGBColour;
import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.common.FileType;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.codice.imaging.nitf.core.header.NitfHeader;
import org.codice.imaging.nitf.core.image.ImageBand;
import org.codice.imaging.nitf.core.image.ImageCategory;
import org.codice.imaging.nitf.core.image.ImageCompression;
import org.codice.imaging.nitf.core.image.ImageCoordinatesRepresentation;
import org.codice.imaging.nitf.core.image.ImageMode;
import org.codice.imaging.nitf.core.image.ImageRepresentation;
import org.codice.imaging.nitf.core.image.ImageSegment;
import org.codice.imaging.nitf.core.image.PixelJustification;
import org.codice.imaging.nitf.core.image.PixelValueType;
import org.codice.imaging.nitf.core.image.TargetId;
import org.codice.imaging.nitf.core.security.FileSecurityMetadata;
import org.codice.imaging.nitf.core.security.SecurityClassification;
import org.codice.imaging.nitf.core.security.SecurityMetadata;
import org.codice.imaging.nitf.core.tre.Tre;
import org.codice.imaging.nitf.core.tre.TreCollection;
import org.codice.imaging.nitf.core.tre.TreEntry;
import org.codice.imaging.nitf.core.tre.TreGroup;
import org.codice.imaging.nitf.core.tre.TreSource;
import org.codice.imaging.nitf.fluent.NitfCreationFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreTestUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(TreTestUtility.class);

    public static void createFileIfNecessary(String filename, Consumer<String> consumer) {
        File file = new File(filename);

        if (!file.exists()) {
            consumer.accept(filename);
        }
    }

    public static void createNitfNoImageNoTres(String filename) {
        new NitfCreationFlow().fileHeader(() -> createFileHeader())
                .write(filename);
    }

    public static void createNitfImageNoTres(String filename) {
        new NitfCreationFlow().fileHeader(() -> createFileHeader())
                .imageSegment(() -> createImageSegment())
                .write(filename);
    }

    public static void createNitfNoImageTres(String filename) {
        new NitfCreationFlow().fileHeader(() -> {
            try {
                return createFileHeaderWithMtirpb();
            } catch (NitfFormatException e) {
                LOGGER.error(e.getMessage(), e);
            }

            return null;
        })
                .write(filename);
    }

    public static void createNitfImageTres(String filename) {
        new NitfCreationFlow().fileHeader(() -> {
            try {
                return createFileHeaderWithMtirpb();
            } catch (NitfFormatException e) {
                LOGGER.error(e.getMessage(), e);
            }

            return null;
        })
                .imageSegment(() -> createImageSegment())
                .write(filename);
    }

    public static NitfHeader createFileHeaderWithMtirpb() throws NitfFormatException {
        NitfHeader nitfHeader = createFileHeader();
        TreCollection treCollection = nitfHeader.getTREsRawStructure();
        treCollection.add(createMtirpbTre());
        return nitfHeader;
    }

    public static Tre createMtirpbTre() throws NitfFormatException {
        final String[] fieldNames =
                {"MTI_DP", "MTI_PACKET_ID", "PATCH_NO", "WAMTI_FRAME_NO", "WAMTI_BAR_NO", "DATIME",
                        "ACFT_LOC", "ACFT_ALT", "ACFT_ALT_UNIT", "ACFT_HEADING", "MTI_LR",
                        "SQUINT_ANGLE", "COSGRZ", "NO_VALID_TARGETS"};
        final String[] values =
                {"00", "001", "0001", "00001", "1", "20141108235219", "+52.123456-004.123456",
                        "150000", "m", "000", " ", "      ", "0.03111", "001"};

        StringBuilder accumulator = new StringBuilder();
        Tre tre = mock(Tre.class);
        when(tre.getName()).thenReturn("MTIRPB");

        for (int i = 0; i < fieldNames.length; i++) {
            accumulator.append(values[i]);
            when(tre.getEntry(fieldNames[i])).thenReturn(new TreEntry(fieldNames[i], values[i], "String"));
        }

        TreGroup targetsGroup = createTreGroup(accumulator);
        TreEntry targetsEntry = new TreEntry("TARGETS");
        targetsEntry.addGroup(targetsGroup);
        when(tre.getEntry("TARGETS")).thenReturn(targetsEntry);
        when(tre.getSource()).thenReturn(TreSource.UserDefinedHeaderData);
        when(tre.getRawData()).thenReturn(accumulator.toString()
                .getBytes());
        return tre;
    }

    public static TreGroup createTreGroup(StringBuilder stringBuilder) throws NitfFormatException {
        final String[] treGroupFields =
                {"TGT_LOC", "TGT_LOC_ACCY", "TGT_VEL_R", "TGT_SPEED", "TGT_HEADING",
                        "TGT_AMPLITUDE", "TGT_CAT"};
        final String[] treGroupValues =
                {"+52.1234567-004.1234567", "000.00", "+013", "000", "000", "06", "U"};

        TreGroup treGroup = mock(TreGroup.class);

        for (int i = 0; i < treGroupFields.length; i++) {
            when(treGroup.getFieldValue(treGroupFields[i])).thenReturn(treGroupValues[i]);
            stringBuilder.append(treGroupValues[i]);
        }

        return treGroup;
    }

    public static NitfHeader createFileHeader() {
        TreCollection treCollection = new TreCollection();
        FileSecurityMetadata securityMetadata = createSecurityMetadata();
        NitfHeader nitfHeader = mock(NitfHeader.class);
        when(nitfHeader.getFileTitle()).thenReturn("TEST NITF");
        when(nitfHeader.getFileType()).thenReturn(FileType.NITF_TWO_ONE);
        when(nitfHeader.getComplexityLevel()).thenReturn(1);
        when(nitfHeader.getFileDateTime()).thenReturn(DateTime.getNitfDateTimeForNow());
        when(nitfHeader.getOriginatingStationId()).thenReturn("LOCALHOST");
        when(nitfHeader.getStandardType()).thenReturn("BF01");
        when(nitfHeader.getFileBackgroundColour()).thenReturn(new RGBColour((byte) 0,
                (byte) 0,
                (byte) 0));
        when(nitfHeader.getOriginatorsName()).thenReturn("");
        when(nitfHeader.getOriginatorsPhoneNumber()).thenReturn("");
        when(nitfHeader.getFileSecurityMetadata()).thenReturn(securityMetadata);
        when(nitfHeader.getTREsRawStructure()).thenReturn(treCollection);
        return nitfHeader;
    }

    public static FileSecurityMetadata createSecurityMetadata() {
        FileSecurityMetadata securityMetadata = mock(FileSecurityMetadata.class);
        when(securityMetadata.getFileType()).thenReturn(FileType.NITF_TWO_ONE);
        when(securityMetadata.getSecurityClassification()).thenReturn(SecurityClassification.UNCLASSIFIED);
        when(securityMetadata.getSecurityClassificationSystem()).thenReturn("");
        when(securityMetadata.getCodewords()).thenReturn("");
        when(securityMetadata.getControlAndHandling()).thenReturn("");
        when(securityMetadata.getReleaseInstructions()).thenReturn("");
        when(securityMetadata.getDeclassificationType()).thenReturn("");
        when(securityMetadata.getDeclassificationDate()).thenReturn("");
        when(securityMetadata.getDeclassificationExemption()).thenReturn("");
        when(securityMetadata.getDowngrade()).thenReturn("");
        when(securityMetadata.getDowngradeDate()).thenReturn("");
        when(securityMetadata.getClassificationText()).thenReturn("");
        when(securityMetadata.getClassificationAuthorityType()).thenReturn("");
        when(securityMetadata.getClassificationAuthority()).thenReturn("");
        when(securityMetadata.getClassificationReason()).thenReturn("");
        when(securityMetadata.getSecuritySourceDate()).thenReturn("");
        when(securityMetadata.getSecurityControlNumber()).thenReturn("");
        when(securityMetadata.getFileCopyNumber()).thenReturn("");
        when(securityMetadata.getFileNumberOfCopies()).thenReturn("");
        return securityMetadata;
    }

    public static ImageSegment createImageSegment() {
        SecurityMetadata securityMetadata = createSecurityMetadata();
        ImageBand imageBand = createImageBand();
        ImageSegment imageSegment = mock(ImageSegment.class);
        TreCollection treCollection = new TreCollection();

        when(imageSegment.getFileType()).thenReturn(FileType.NITF_TWO_ONE);
        when(imageSegment.getNumBands()).thenReturn(3);
        when(imageSegment.getActualBitsPerPixelPerBand()).thenReturn(8);
        when(imageSegment.getIdentifier()).thenReturn("12345");
        when(imageSegment.getImageDateTime()).thenReturn(DateTime.getNitfDateTimeForNow());
        when(imageSegment.getImageTargetId()).thenReturn(new TargetId());
        when(imageSegment.getImageIdentifier2()).thenReturn("");
        when(imageSegment.getSecurityMetadata()).thenReturn(securityMetadata);
        when(imageSegment.getImageSource()).thenReturn("");
        when(imageSegment.getNumberOfRows()).thenReturn(2048l);
        when(imageSegment.getNumberOfColumns()).thenReturn(2048l);
        when(imageSegment.getPixelValueType()).thenReturn(PixelValueType.INTEGER);
        when(imageSegment.getImageRepresentation()).thenReturn(ImageRepresentation.RGBTRUECOLOUR);
        when(imageSegment.getImageCategory()).thenReturn(ImageCategory.MULTISPECTRAL);
        when(imageSegment.getActualBitsPerPixelPerBand()).thenReturn(8);
        when(imageSegment.getPixelJustification()).thenReturn(PixelJustification.LEFT);
        when(imageSegment.getImageCoordinatesRepresentation()).thenReturn(
                ImageCoordinatesRepresentation.NONE);
        when(imageSegment.getImageCompression()).thenReturn(ImageCompression.NOTCOMPRESSED);
        when(imageSegment.getImageBandZeroBase(anyInt())).thenReturn(imageBand);
        when(imageSegment.getImageMode()).thenReturn(ImageMode.BANDSEQUENTIAL);
        when(imageSegment.getNumberOfBlocksPerRow()).thenReturn(4);
        when(imageSegment.getNumberOfBlocksPerColumn()).thenReturn(4);
        when(imageSegment.getNumberOfPixelsPerBlockHorizontal()).thenReturn(512L);
        when(imageSegment.getNumberOfPixelsPerBlockVertical()).thenReturn(512L);
        when(imageSegment.getImageDisplayLevel()).thenReturn(1);
        when(imageSegment.getAttachmentLevel()).thenReturn(2);
        when(imageSegment.getImageLocationRow()).thenReturn(0);
        when(imageSegment.getImageLocationColumn()).thenReturn(0);
        when(imageSegment.getImageMagnification()).thenReturn("1.00");
        when(imageSegment.getTREsRawStructure()).thenReturn(treCollection);
        return imageSegment;
    }

    public static ImageBand createImageBand() {
        ImageBand imageBand = new ImageBand();
        imageBand.setImageRepresentation("RGB");
        imageBand.setImageSubcategory("XXX");
        return imageBand;
    }
}

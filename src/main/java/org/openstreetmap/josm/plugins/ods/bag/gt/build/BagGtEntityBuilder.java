package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

import org.locationtech.jts.geom.Geometry;

public abstract class BagGtEntityBuilder<T extends OdEntity> implements OdEntityBuilder<T> {
    private final CRSUtil crsUtil;

    public BagGtEntityBuilder(CRSUtil crsUtil) {
        super();
        this.crsUtil = crsUtil;
    }

    @SuppressWarnings("static-method")
    public Object getReferenceId(SimpleFeature feature) {
        String id = FeatureUtil.getString(feature, "identificatie");
        return Long.valueOf(id);
    }

    protected void parse(SimpleFeature feature, OdEntity entity, DownloadResponse response) {
        entity.setDownloadResponse(response);
        entity.setReferenceId(getReferenceId(feature));
        entity.setPrimaryId(feature.getID());
        LocalDate date = response.getRequest().getDownloadTime().toLocalDate();
        if (date != null) {
            entity.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        }
        entity.setSource("BAG");
        try {
            CoordinateReferenceSystem crs = feature.getType()
                    .getCoordinateReferenceSystem();
            Geometry gtGeometry = getGeometry(feature);
            entity.setGeometry(crsUtil.toOsm(gtGeometry, crs));
        } catch (CRSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("static-method")
    protected Geometry getGeometry(SimpleFeature feature) {
        return (Geometry) feature.getDefaultGeometry();
    }
}

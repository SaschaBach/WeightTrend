package de.weighttrend.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Interface für die Attribute der Entitäten.
 * Das Interface findet seine Verwendung bei dem generischen Erzeugen der Datenbank Tabellen.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String columnName();

    String columnType();

    String additionalInformations();
}

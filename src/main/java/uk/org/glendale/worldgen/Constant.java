/**
 * Constant.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A universal constant. These are numeric values that apply to the given universe.
 */
@Entity
@Table(name="constants")
public class Constant {
    private String          name;
    private long            value;

    /**
     * Records name values for the different constants stored in the database.
     */
    public enum Name {
        SPEED("speed");

        private final String dbname;
        private Name(String dbname) {

            this.dbname = dbname;
        }

        public String getName() {
            return dbname;
        }

        public String toString() {
            return dbname;
        }
    }

    public Constant(String name, long value) {
        this.name = name;
        this.value = value;
    }

    protected Constant() {

    }

    @Id
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String toString() {
        return "[ " + name + ": " + value + " ]";
    }
}

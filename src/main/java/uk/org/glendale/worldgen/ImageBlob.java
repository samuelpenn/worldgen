package uk.org.glendale.worldgen;

import javax.persistence.*;

@Entity
@Table(name="blobs")
public class ImageBlob {
    @Id @GeneratedValue
    private int id;

    @Column
    private String name;

    @Column
    private byte[] data;

    protected ImageBlob() {

    }

    public ImageBlob(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }


}

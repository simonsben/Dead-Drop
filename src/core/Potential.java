package core;

import java.util.ArrayList;

public class Potential {
    public int index, encoding_id;
    public boolean is_advanced;
    ArrayList<image> image_set;

    public Potential(int _index, boolean _is_advanced, image img, int _encoding_id) {
        index = _index;
        is_advanced = _is_advanced;
        encoding_id = _encoding_id;
        image_set = new ArrayList<>();

        add(img);
    }
    public Potential(int _index, boolean _is_advanced, image img) {
        this(_index, _is_advanced, img, -1);
    }


    public void add(image img) {
        if (is_advanced && image_set.size() > 0)
            throw new IllegalArgumentException("Cannot add more than one image to basic potential encoding");
        image_set.add(img);
    }

    @Override
    public int hashCode() {
        if (is_advanced)
            return Integer.hashCode(encoding_id);
        return image_set.get(0).hashCode();
    }

    @Override
    public String toString() {
        if (!is_advanced)
            return index + " " + image_set.get(0).filename.getFileName().toString();

        StringBuilder listing = new StringBuilder();
        listing.append(index + " - advanced, " + encoding_id + " - ");

        for (image img : image_set) {
            listing.append(img.filename.getFileName());
            listing.append(' ');
        }
        return listing.toString();
    }
}

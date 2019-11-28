package core;

import java.util.ArrayList;

public class Potential {
    public int index;
    public short encoding_id;
    public boolean is_advanced;
    public ArrayList<image> image_set;

    public Potential(int _index, boolean _is_advanced, image img, short _encoding_id) {
        index = _index;
        is_advanced = _is_advanced;
        encoding_id = _encoding_id;
        image_set = new ArrayList<>();

        add(img);
    }
    public Potential(int _index, boolean _is_advanced, image img) {
        this(_index, _is_advanced, img, (short) -1);
    }


    public void add(image img) {
        if (!is_advanced && image_set.size() > 0)
            throw new IllegalArgumentException("Cannot add more than one image to basic potential encoding");
        image_set.add(img);
    }

    @Override
    public int hashCode() {
        System.out.printf("Computing hash for %d on %s\n", encoding_id, image_set.get(0).filename);
        if (is_advanced)
            return encoding_id;
        return image_set.get(0).hashCode();
    }

    @Override
    public String toString() {
//        if (!is_advanced)
//            return index + " " + image_set.get(0).filename.getFileName().toString();

        StringBuilder listing = new StringBuilder();
        listing.append(index);
        listing.append(": ");
        listing.append(is_advanced? "advanced " : "basic ");
        listing.append(image_set.get(0).encode_tech == 0? "naive " : "bpcs ");
        if (is_advanced)
            listing.append(encoding_id);

        for (image img : image_set) {
            listing.append(' ');
            listing.append(img.filename.getFileName());
        }
        return listing.toString();
    }
}

import core.image;

public abstract class image_encoder {
    image[] image_set;
    int data_length;
    int header_length;
    int data_capacity;

    public image_encoder(image[] image_set) {
        this.image_set = image_set;
    }

    public image_encoder(String[] filenames) {
        image[] image_set = new image[filenames.length];
        for (int index=0;index<filenames.length;index++)
            image_set[index] = new image(filenames[index]);

        this.image_set = image_set;
    }

    // Compute capacity of images in bytes
    public int get_capacity() {
        int capacity = 0;
        for (image img : this.image_set)
            capacity += img.total_capacity;

        this.data_capacity = capacity;
        return capacity;
    }

    public byte[] has_capacity(int data_length) {
        byte[] header = get_header(data_length);

        if ((data_length + header.length) > get_capacity()) {
            System.out.println("Too much data to fit into images");
            return null;
        }

        return header;
    }

    public void save_images() {
        for (image target_image : this.image_set)
            target_image.save_image();
    }

    public abstract byte[] get_header(int data_length);
    public abstract void encode_data(byte[] data);
    public abstract void decode_header(byte[] header);
}

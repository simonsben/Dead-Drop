public class image {
    byte[] image_data;

    image(String filename) {
        this.load_file(filename);
    }

    void load_file(String filename) {
        this.image_data = input.load_file("test_image.jpg");

        if (!header.is_jfif(this.image_data)) {
            this.image_data = null;
            System.out.println("Provided filename is invalid.");
        }

        System.out.println("Loaded valid image.");
    }

    public byte[] get_data() {
        return this.image_data.clone();
    }
}

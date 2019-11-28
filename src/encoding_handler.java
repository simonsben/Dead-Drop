import core.BPCS;
import core.Naive;
import core.image;
import core.technique;
import core.Potential;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static utilities.input.get_input;
import static utilities.output.read_out;
import static utilities.output.write_file;
import static utilities.strings.get_extension;

public class encoding_handler {
    technique naive = new Naive(), bpcs = new BPCS();
    HashMap<Short, Potential> potentials;
    Potential selected;

    public static void main(String[] args) {
        encoding_handler handler = new encoding_handler("processed");
        System.out.println(handler);

        handler.select_potential();
    }

    public encoding_handler(String directory) {
        Path dir_path = Paths.get(directory);
        File[] files = get_candidates(dir_path);
        potentials = check_candidates(files);
    }

    void decode_selection(String filename) {
        if (selected == null)
            throw new IllegalCallerException("Can't decode data without a valid selection");
        image[] selected_images = (image[]) selected.image_set.toArray();
        technique tech = selected_images[0].encode_tech == 0? naive : bpcs;

        image_encoder encoder;
        if (selected.is_advanced) encoder = new advanced_encoder(selected_images, tech, selected.encoding_id);
        else encoder = new basic_encoder(selected_images, tech);

        byte[] data = encoder.decode_data();

        if (filename == null)
            write_file(filename, data);
        else
            read_out(data);
    }

    public void select_potential() {
        System.out.println("Enter the entry index or encoding ID");
        short index = get_input();              // Get user index selection
        if (potentials.containsKey(index))      // If basic get encoding
            selected = potentials.get(index);
        else                                    // If advanced get encoding
            selected = potentials.values().stream()
                    .filter(potential -> potential.index == index).findFirst()
                    .orElse(null);

        // If index is invalid, try again
        if (selected == null) {
            System.out.println("Invalid index, try again.");
            select_potential();
            return;
        }
        System.out.print("Selected ");
        System.out.println(selected);
    }

    public HashMap<Short, Potential> check_candidates(File[] candidates) {
        HashMap<Short, Potential> potentials = new HashMap<>();
        short index = 0;

        for (File filename : candidates) {
            image img = new image(filename.toString(), naive);
            if (!img.was_used)
                img.load_image(bpcs);
            if (!img.was_used)
                continue;

            if (img.encode_mode == 0)           // If basic
                potentials.put(index, new Potential(index++, false, img));
            else if (img.encode_mode == 1) {    // If advanced
                short key = img.encoding_id;
                if (potentials.containsKey(key))
                    potentials.get(key).add(img);
                else
                    potentials.put(key, new Potential(index++, true, img, key));
            }
        }

        return potentials;
    }

    final static FilenameFilter filter = ((dir, name) -> get_extension(name).equals("png"));

    public static File[] get_candidates(Path directory) {
        File dir_file = directory.toFile();

        return dir_file.listFiles(filter);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Potential candidates:\n");

        potentials.forEach((key, potential) -> {
            output.append(potential);
            output.append('\n');
        });

        return output.toString();
    }
}

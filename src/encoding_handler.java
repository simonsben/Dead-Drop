import core.BPCS;
import core.Naive;
import core.Image;
import core.technique;
import core.Potential;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static core.header.decode_header;
import static utilities.data_management.compute_md5;
import static utilities.input.get_input;
import static utilities.output.read_out;
import static utilities.output.write_file;
import static utilities.strings.get_extension;

public class encoding_handler {
    Potential selected;
    HashMap<Short, Potential> potentials;
    technique naive = new Naive(), bpcs = new BPCS();
    String source_directory, target_filename;
    static String manual = "Expected parameters as:\n" +
            "-s source directory (i.e. location of embedded files)\n" +
            "-f target file (i.e. path for the recovered file)\n";

    public static void main(String[] args) {
        encoding_handler handler = new encoding_handler();
        handler.get_params(args);

        handler.select_potential();
        for (Image image : handler.selected.image_set)
            System.out.println(image);
        handler.decode_selection();
    }

    void get_params(String[] args) {
        for (int index = 0; index < args.length - 1; index++) {
            if (args[index].equals("-s"))
                source_directory = args[index + 1];
            else if (args[index].equals("-f"))
                target_filename = args[index + 1];
        }

        if (source_directory == null)
            throw new IllegalArgumentException("Source directory doesn't exist\n" + manual);
        if (!(new File(source_directory).exists()))
            throw new IllegalArgumentException("Source directory doesn't exist\n" + manual);
        if (target_filename == null)
            throw new IllegalArgumentException("Target filename must be provided.\n" + manual);
        get_potentials();
    }

    void get_potentials() {
        Path dir_path = Paths.get(source_directory);
        File[] files = get_candidates(dir_path);
        potentials = check_candidates(files);
    }

    void decode_selection() {
        if (selected == null)
            throw new IllegalCallerException("Can't decode data without a valid selection");
//        Image[] selected_images = selected.image_set.toArray(new Image[selected.image_set.size()]);
//        technique tech = selected_images[0].encode_tech == 0? naive : bpcs;
//
        String[] filenames = new String[selected.image_set.size()];
        for (int index = 0; index < filenames.length; index++)
            filenames[index] = selected.image_set.get(index).filename.toString();

        image_encoder encoder;
        if (selected.is_advanced) {
            encoder = new advanced_encoder(filenames, "bpcs");
            ((advanced_encoder) encoder).encoding_id = selected.encoding_id;
        }
        else encoder = new basic_encoder(filenames, "naive");

        byte[] recovered = encoder.decode_data();
        write_file(target_filename, recovered);
    }

    public void select_potential() {
        System.out.println(this);
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
            Image img = new Image(filename.toString(), naive);      // Try naive
            if (!img.was_used) decode_header(img, bpcs);            // Try bpcs
            if (!img.was_used) continue;                            // Unused image

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
